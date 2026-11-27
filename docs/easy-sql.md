# EasySQL

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.database`

`EasySQL` is a parameterized SQLite wrapper over a single database file (on-disk database version `1`). Instance construction is lightweight—opening the underlying `SQLiteOpenHelper` is deferred until the first query.

> **Blocking disk I/O:** Every `EasySQL` method blocks on disk I/O and should be called from a background thread. Dispatch UI updates back via `MainThread`.

## Security & Validation

- Table and column names must match `[A-Za-z_][A-Za-z0-9_]*`
- Column type definitions must match `[A-Za-z0-9_ (),]+` (quotes and semicolons are rejected)
- Values are bound as SQL parameters rather than concatenated

## API Reference

| Method | Description |
|---|---|
| `new EasySQL(Context context, String databaseName)` | Creates a handle without touching disk yet. |
| `createTable(String table, Map<String, String> columns)` | Creates a table if it does not exist. |
| `tableExists(String table)` | Validates `table` and returns whether the table or view exists. |
| `dropTable(String table)` | Drops `table` if it exists. |
| `clearTable(String table)` | Deletes all rows while keeping the table schema; returns deleted count. |
| `insert(String table, Map<String, String> values)` | Inserts one row and returns the new row ID (or `-1` on failure). |
| `delete(String table, String column, String value)` | Deletes rows matching `column = ?`; returns deleted count. |
| `selectAll(String table)` | Reads every row as `List<Map<String, Object>>` (`Long`, `Double`, `String`, `byte[]`, or `null`). |
| `inTransaction(Runnable work)` | Runs `work` inside an atomic SQLite transaction. |
| `close()` | Closes the underlying `SQLiteOpenHelper` (`Closeable`). |

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.database.EasySQL;

EasySQL db = new EasySQL(context, "app_data.db");

Map<String, String> columns = new LinkedHashMap<>();
columns.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
columns.put("body", "TEXT NOT NULL");

db.createTable("notes", columns);
if (db.tableExists("notes")) {
    db.inTransaction(() -> {
        db.insert("notes", Collections.singletonMap("body", "First note"));
        db.insert("notes", Collections.singletonMap("body", "Second note"));
    });
}
List<Map<String, Object>> rows = db.selectAll("notes");
db.close();
```
