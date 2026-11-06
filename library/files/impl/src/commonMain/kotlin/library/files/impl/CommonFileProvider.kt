package library.files.impl

import library.files.api.FileProvider
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.uuid.Uuid

abstract class CommonFileProvider(
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : FileProvider {
    override fun exists(file: Path): Boolean {
        return fileSystem.exists(file)
    }

    override fun writeText(file: Path, content: String) {
        val directory = file.parent ?: ".".toPath()
        fileSystem.createDirectories(directory)
        val temporary = directory / Uuid.random().toString()
        try {
            fileSystem.write(temporary) { writeUtf8(content) }
            fileSystem.atomicMove(temporary, file)
        } finally {
            fileSystem.delete(temporary, mustExist = false)
        }
    }

    override fun deleteRecursively(path: Path) {
        fileSystem.deleteRecursively(path, mustExist = false)
    }
}
