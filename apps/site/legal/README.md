# Published legal documents

The site serves every published version of the BookkMe privacy policy, terms of service and data processing agreement, in English, Ukrainian and Polish:

| Path | Shows |
|---|---|
| `/privacy`, `/terms`, `/dpa` | The version in effect, or the first upcoming one before any takes effect, in the site language |
| `/<document>/<version>` | That version in the site language. This is the URL stored in `user.legal_document_version` |
| `/<document>/<version>/<en\|uk\|pl>` | That version in one language |

Each page links its three languages, announces an upcoming version, marks a superseded one and lists the version history.

## Folder layout

```
legal/
  <privacy|terms|dpa>/<version>/en.md, uk.md, pl.md, effective-at.txt
  published.sha256
```

`effective-at.txt` holds the UTC time the version takes effect, the same value as `effective_at` in the user database. The texts are drafted in the server repository (`docs/legal/bookkme/`); this folder keeps the copies that were published.

The site renders the texts with its own small Markdown parser (`apps/site/src/jsMain/kotlin/me/bookk/site/legal/markdown/Markdown.kt`). It supports headings (with an optional leading `<a id="..."></a>` anchor), paragraphs, `- ` bullet lists, `1. ` numbered lists, `---` separators, tables, `**bold**`, `[links](...)` (including `#anchor` and `mailto:`) and bare `https://` URLs. Anything else shows up as literal text, so extend the parser before publishing a text that uses more.

## Published versions never change

`published.sha256` locks every published file. The `:site:generateLegalTexts` task, which every site build runs, fails when a file was edited, deleted or never locked, or still contains a `{{placeholder}}`. Users' acceptance records point at a version's URL, so its text must stay what they read. To correct a mistake, publish a new version.

## Publishing a version

1. Finish the text in the server repository in all three languages, with the new version and effective date in its header.
2. Copy it here. Each document has its own versions, so publish only the documents that changed:

   ```bash
   apps/site/legal/import.sh ~/IdeaProjects/bookk-server/docs/legal/bookkme privacy 2027-03-01 2027-03-01T00:00:00Z
   ```

   The script refuses an existing version, a missing language, a remaining placeholder or a header that does not name the version. It appends the new files to `published.sha256` and prints the SQL for the next step.
3. Deploy the site and check that `https://bookkme.com/<document>/<version>` is live.
4. For the privacy policy and terms only, insert the version into `user.legal_document_version` through NocoDB with the printed values. The DPA has no database row: it is part of the terms for business owners.

Insert the row only after the page is live, because a version in effect is required at every sign-up. An `effective_at` in the future lets the site announce the change before it applies.
