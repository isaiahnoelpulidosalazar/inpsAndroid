package com.isaiahnoelpulidosalazar.inpsandroid.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Small, safe wrapper over one SQLite database file (on-disk database version 1).
 *
 * <ul>
 *   <li>One long-lived, lazily opened {@link SQLiteOpenHelper} per instance; construction does not
 *       touch disk.</li>
 *   <li>Identifiers (table and column names) are validated; values are bound as parameters so user
 *       text cannot alter the SQL.</li>
 * </ul>
 *
 * <p><b>Blocking disk I/O:</b> every database operation blocks on disk I/O and must be called from
 * a background thread.</p>
 */
public final class EasySQL implements Closeable {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    /** Column type/constraint text such as {@code INTEGER PRIMARY KEY}. Quotes and semicolons are rejected. */
    private static final Pattern COLUMN_TYPE = Pattern.compile("[A-Za-z0-9_ (),]+");

    private final SQLiteOpenHelper helper;

    /**
     * Creates a lightweight handle for {@code databaseName}. Opening the database file is deferred
     * until the first database read or write.
     *
     * @param context      Android context (application context is retained)
     * @param databaseName SQLite database filename, or {@code null} for an in-memory database
     */
    public EasySQL(Context context, String databaseName) {
        helper = new SQLiteOpenHelper(context.getApplicationContext(), databaseName, null, 1) {
            @Override public void onCreate(SQLiteDatabase db) { }
            @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
        };
    }

    /**
     * Creates the table if it does not exist. Blocks on disk I/O.
     *
     * @param table   validated SQL table identifier
     * @param columns ordered map of column name to SQL column definition (e.g. {@code TEXT NOT NULL})
     * @throws IllegalArgumentException if any identifier or column definition is invalid, or {@code columns} is empty
     */
    public void createTable(String table, Map<String, String> columns) {
        requireIdentifier(table);
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("A table needs at least one column");
        }
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(quote(table)).append(" (");
        boolean first = true;
        for (Map.Entry<String, String> column : columns.entrySet()) {
            requireIdentifier(column.getKey());
            if (column.getValue() == null || !COLUMN_TYPE.matcher(column.getValue()).matches()) {
                throw new IllegalArgumentException("Invalid column type: " + column.getValue());
            }
            if (!first) sql.append(", ");
            first = false;
            sql.append(quote(column.getKey())).append(' ').append(column.getValue());
        }
        helper.getWritableDatabase().execSQL(sql.append(')').toString());
    }

    /**
     * Checks whether {@code table} (or a view with that name) exists in the database. Blocks on
     * disk I/O.
     *
     * @param table validated SQL table identifier
     * @return {@code true} if the table or view exists, {@code false} otherwise
     * @throws IllegalArgumentException if {@code table} is not a valid SQL identifier
     */
    public boolean tableExists(String table) {
        requireIdentifier(table);
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type IN ('table','view') AND name = ? COLLATE NOCASE",
                new String[]{table})) {
            return c.moveToFirst();
        }
    }

    /**
     * Drops {@code table} if it exists. Blocks on disk I/O.
     *
     * @param table validated SQL table identifier
     * @throws IllegalArgumentException if {@code table} is not a valid SQL identifier
     */
    public void dropTable(String table) {
        requireIdentifier(table);
        helper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + quote(table));
    }

    /**
     * Deletes every row while keeping the table schema intact. Blocks on disk I/O.
     *
     * @param table validated SQL table identifier
     * @return number of deleted rows
     * @throws IllegalArgumentException if {@code table} is not a valid SQL identifier
     */
    public int clearTable(String table) {
        requireIdentifier(table);
        return helper.getWritableDatabase().delete(quote(table), null, null);
    }

    /**
     * Inserts one row into {@code table} using bound parameter values. Blocks on disk I/O.
     *
     * @param table  validated SQL table identifier
     * @param values map of validated column names to string values
     * @return row ID of the newly inserted row, or {@code -1} if an error occurred
     * @throws IllegalArgumentException if {@code table} or any column name is invalid
     */
    public long insert(String table, Map<String, String> values) {
        requireIdentifier(table);
        ContentValues cv = new ContentValues();
        for (Map.Entry<String, String> e : values.entrySet()) {
            requireIdentifier(e.getKey());
            cv.put(e.getKey(), e.getValue());
        }
        return helper.getWritableDatabase().insert(quote(table), null, cv);
    }

    /**
     * Deletes rows matching {@code column = value}. SQLite applies the column's type affinity to
     * the bound text, so numeric columns match numeric strings. Blocks on disk I/O.
     *
     * @param table  validated SQL table identifier
     * @param column validated SQL column identifier
     * @param value  parameterized match value
     * @return number of deleted rows
     * @throws IllegalArgumentException if {@code table} or {@code column} is invalid
     */
    public int delete(String table, String column, String value) {
        requireIdentifier(table);
        requireIdentifier(column);
        return helper.getWritableDatabase().delete(quote(table), quote(column) + " = ?", new String[]{value});
    }

    /**
     * Reads every row from {@code table}. Values are returned as {@link Long}, {@link Double},
     * {@link String}, {@code byte[]}, or {@code null}, keyed by column name in column order.
     * Blocks on disk I/O.
     *
     * @param table validated SQL table identifier
     * @return list of row maps in query order
     * @throws IllegalArgumentException if {@code table} is invalid
     */
    public List<Map<String, Object>> selectAll(String table) {
        requireIdentifier(table);
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Cursor c = helper.getReadableDatabase().query(quote(table), null, null, null, null, null, null)) {
            String[] names = c.getColumnNames();
            while (c.moveToNext()) {
                Map<String, Object> row = new LinkedHashMap<>(names.length * 2);
                for (int i = 0; i < names.length; i++) {
                    switch (c.getType(i)) {
                        case Cursor.FIELD_TYPE_INTEGER: row.put(names[i], c.getLong(i)); break;
                        case Cursor.FIELD_TYPE_FLOAT:   row.put(names[i], c.getDouble(i)); break;
                        case Cursor.FIELD_TYPE_BLOB:    row.put(names[i], c.getBlob(i)); break;
                        case Cursor.FIELD_TYPE_NULL:    row.put(names[i], null); break;
                        default:                        row.put(names[i], c.getString(i));
                    }
                }
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * Executes {@code work} inside a single atomic SQLite transaction; any unchecked exception rolls
     * back all changes. Blocks on disk I/O.
     *
     * @param work operations to run atomically
     */
    public void inTransaction(Runnable work) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            work.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /** Closes the underlying {@link SQLiteOpenHelper} and releases file handles. */
    @Override
    public void close() {
        helper.close();
    }

    private static void requireIdentifier(String name) {
        if (name == null || !IDENTIFIER.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + name);
        }
    }

    private static String quote(String identifier) {
        return '"' + identifier + '"';
    }
}
