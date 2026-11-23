/*
 * Based on the androidx WebWorkerSQLiteDriver reference worker:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:sqlite/sqlite-web-worker-test/web-worker/worker.js
 *
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import sqlite3InitModule from './sqlite3.js';

let sqlite3 = null;
let openDatabase = null;

const databases = new Map();
const announcedTotalChanges = new Map();
const databaseChanges = new BroadcastChannel('bookkme-db-changes');
const statements = new Map();

let nextDatabaseId = 0;
let nextStatementId = 0;

async function createDatabaseOpener(instance) {
    if (instance.oo1.OpfsDb) {
        console.info('SQLite storage: OPFS, shared between tabs.');
        return (fileName) => new instance.oo1.OpfsDb(fileName);
    }
    try {
        const pool = await instance.installOpfsSAHPoolVfs({name: 'bookkme-sahpool'});
        console.info('SQLite storage: OPFS SAH pool, this tab only (page is not cross-origin isolated).');
        return (fileName) => new pool.OpfsSAHPoolDb('/' + fileName);
    } catch (error) {
        console.warn('SQLite OPFS storage is unavailable, using an in-memory database.', error);
        return () => new instance.oo1.DB(':memory:');
    }
}

function openRequest(id, requestData) {
    try {
        const newDatabaseId = nextDatabaseId++;
        databases.set(newDatabaseId, openDatabase(requestData.fileName));
        postMessage({'id': id, data: {'databaseId': newDatabaseId}});
    } catch (error) {
        postMessage({'id': id, error: error.message});
    }
}

function prepareRequest(id, requestData) {
    try {
        const newStatementId = nextStatementId++;
        const resultData = {
            'statementId': newStatementId,
            'parameterCount': 0,
            'columnNames': []
        };
        const database = databases.get(requestData.databaseId);
        if (!database) {
            postMessage({'id': id, error: "Invalid database ID: " + requestData.databaseId});
            return;
        }
        const statement = database.prepare(requestData.sql);
        statements.set(newStatementId, statement);
        resultData.parameterCount = sqlite3.capi.sqlite3_bind_parameter_count(statement);
        for (let i = 0; i < statement.columnCount; i++) {
            resultData.columnNames.push(sqlite3.capi.sqlite3_column_name(statement, i));
        }
        postMessage({'id': id, data: resultData});
    } catch (error) {
        postMessage({'id': id, error: error.message});
    }
}

function stepRequest(id, requestData) {
    const statement = statements.get(requestData.statementId);
    if (!statement) {
        postMessage({'id': id, error: "Invalid statement ID: " + requestData.statementId});
        return;
    }
    try {
        const resultData = {
            'rows': [],
            'columnTypes': []
        };
        statement.reset();
        statement.clearBindings();
        for (let i = 0; i < requestData.bindings.length; i++) {
            statement.bind(i + 1, requestData.bindings[i]);
        }
        while (statement.step()) {
            if (!resultData.columnTypes.length) {
                for (let i = 0; i < statement.columnCount; i++) {
                    resultData.columnTypes.push(sqlite3.capi.sqlite3_column_type(statement, i));
                }
            }
            resultData.rows.push(statement.get([]));
        }
        announceCommittedWrites(statement);
        postMessage({'id': id, data: resultData});
    } catch (error) {
        postMessage({'id': id, error: error.message});
    }
}

function announceCommittedWrites(statement) {
    const database = statement.db;
    if (!sqlite3.capi.sqlite3_get_autocommit(database.pointer)) return;
    const totalChanges = sqlite3.capi.sqlite3_total_changes(database.pointer);
    if (totalChanges === (announcedTotalChanges.get(database) ?? 0)) return;
    announcedTotalChanges.set(database, totalChanges);
    databaseChanges.postMessage('changed');
}

function closeRequest(id, requestData) {
    if (requestData.statementId !== undefined && requestData.statementId != null) {
        const statement = statements.get(requestData.statementId);
        if (!statement) {
            postMessage({'id': id, error: "Invalid statement ID: " + requestData.statementId});
            return;
        }
        try {
            statement.finalize();
            statements.delete(requestData.statementId);
        } catch (error) {
            postMessage({'id': id, error: error.message});
        }
    }

    if (requestData.databaseId !== undefined && requestData.databaseId != null) {
        const database = databases.get(requestData.databaseId);
        if (!database) {
            postMessage({'id': id, error: "Invalid database ID: " + requestData.databaseId});
            return;
        }
        try {
            database.close();
            databases.delete(requestData.databaseId);
        } catch (error) {
            postMessage({'id': id, error: error.message});
        }
    }
}

const commandMap = {
    'open': openRequest,
    'prepare': prepareRequest,
    'step': stepRequest,
    'close': closeRequest,
};

function handleMessage(e) {
    const requestMsg = e.data;
    if (!Object.hasOwn(requestMsg, 'data') && requestMsg.data == null) {
        postMessage({'id': requestMsg.id, 'error': "Invalid request, missing 'data'."});
        return;
    }
    if (!Object.hasOwn(requestMsg.data, 'cmd') && requestMsg.data.cmd == null) {
        postMessage({'id': requestMsg.id, 'error': "Invalid request, missing 'cmd'."});
        return;
    }
    const command = requestMsg.data.cmd;
    const requestHandler = commandMap[command];
    if (requestHandler) {
        requestHandler(requestMsg.id, requestMsg.data);
    } else {
        postMessage(
            {'id': requestMsg.id, 'error': "Invalid request, unknown command: '" + command + "'."}
        );
    }
}

const messageQueue = [];
onmessage = (e) => {
    if (!openDatabase) {
        messageQueue.push(e);
    } else {
        handleMessage(e);
    }
};

globalThis.sqlite3ApiConfig = {disable: {vfs: {'opfs-wl': true}}};

sqlite3InitModule().then(async instance => {
    sqlite3 = instance;
    openDatabase = await createDatabaseOpener(instance);
    while (messageQueue.length > 0) {
        handleMessage(messageQueue.shift());
    }
});
