# Database

`Database` is a small, safe wrapper over one SQLite database file. It replaces the deprecated `EasySQL`.

## Purpose & Scope

The purpose of this utility is to provide:
- One long-lived `SQLiteOpenHelper` per instance instead of repeated open/close cycles
- Validated identifiers (tables and columns must match `[A-Za-z_][A-Za-z0-9_]*`) and fully parameter-bound values, so user text can never alter the SQL
- Typed row reads and all-or-nothing transactions
- The same on-disk format as v1 (database version 1), so existing files open unchanged

`Database` implements `Closeable`. It performs blocking disk I/O, so call it from a background thread.

Column types may contain only letters, digits, `_`, spaces, parentheses, and commas (for example `INTEGER PRIMARY KEY`). Quotes and semicolons are rejected, so `DEFAULT 'x'` is not accepted. Invalid input throws `IllegalArgumentException`.

## API Reference

### Database(Context context, String databaseName)

Opens a helper bound to the application context and the named database file.

```java
Database db = new Database(context, "mydb");
```

### void createTable(String table, Map<String, String> columns)

Creates the table if it does not exist. The map is column name to type, in order.

```java
Map<String, String> cols = new LinkedHashMap<>();
cols.put("id", "INTEGER PRIMARY KEY");
cols.put("name", "TEXT");
db.createTable("users", cols);
```

### boolean tableExists(String table)

Checks whether a table or view with that name exists (case-insensitive).

```java
boolean exists = db.tableExists("users");
```

### void dropTable(String table)

Drops the table if it exists.

```java
db.dropTable("users");
```

### int clearTable(String table)

Deletes every row while keeping the table. Returns the number of rows deleted.

```java
int removed = db.clearTable("users");
```

### long insert(String table, Map<String, String> values)

Inserts one row and returns the new row id, or `-1` on failure. Quotes and colons in values are safe.

```java
Map<String, String> row = new LinkedHashMap<>();
row.put("id", "1");
row.put("name", "O'Brien: the \"first\"");
long rowId = db.insert("users", row);
```

### int delete(String table, String column, String value)

Deletes rows where `column = value`. SQLite applies the column's type affinity, so numeric columns match numeric text. Returns rows deleted.

```java
db.delete("users", "id", "1");
```

### List<Map<String, Object>> selectAll(String table)

Reads every row. Values are `Long`, `Double`, `String`, `byte[]`, or `null`, keyed by column name in column order.

```java
for (Map<String, Object> r : db.selectAll("users")) {
    long id = (Long) r.get("id");
    String name = (String) r.get("name");
}
```

### void inTransaction(Runnable work)

Runs the work in one transaction. An exception rolls everything back.

```java
db.inTransaction(() -> {
    db.insert("users", rowA);
    db.insert("users", rowB);
});
```

### void close()

Closes the underlying helper. Call it when finished with the file.

```java
db.close();
```
