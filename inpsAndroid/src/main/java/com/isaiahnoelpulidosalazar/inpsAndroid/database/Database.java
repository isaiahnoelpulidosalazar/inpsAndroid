package com.isaiahnoelpulidosalazar.inpsAndroid.database;

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
 * Small, safe wrapper over one SQLite database file.
 *
 * <ul>
 *   <li>One long-lived {@link SQLiteOpenHelper} per instance (v1 opened and closed the file several
 *       times per call).</li>
 *   <li>Identifiers (table/column names) are validated; values are always bound as parameters, so
 *       user text can never alter the SQL.</li>
 *   <li>Same on-disk format as v1 (database version 1), so existing files open unchanged.</li>
 * </ul>
 *
 * <p>Blocking disk I/O: call from a background thread.</p>
 */
public final class Database implements Closeable {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    /** Column type/constraint text such as {@code INTEGER PRIMARY KEY}. Quotes and semicolons are rejected. */
    private static final Pattern COLUMN_TYPE = Pattern.compile("[A-Za-z0-9_ (),]+");

    private final SQLiteOpenHelper helper;

    public Database(Context context, String databaseName) {
        helper = new SQLiteOpenHelper(context.getApplicationContext(), databaseName, null, 1) {
            @Override public void onCreate(SQLiteDatabase db) { }
            @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
        };
    }

    /** Creates the table if it does not exist. {@code columns} maps column name to type, in order. */
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

    public boolean tableExists(String table) {
        try (Cursor c = helper.getReadableDatabase().rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type IN ('table','view') AND name = ? COLLATE NOCASE",
                new String[]{table})) {
            return c.moveToFirst();
        }
    }

    public void dropTable(String table) {
        requireIdentifier(table);
        helper.getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + quote(table));
    }

    /** Deletes every row, keeping the table. @return rows deleted. */
    public int clearTable(String table) {
        requireIdentifier(table);
        return helper.getWritableDatabase().delete(quote(table), null, null);
    }

    /** Inserts one row. @return the new row id, or -1 on failure. */
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
     * Deletes rows where {@code column = value}. SQLite applies the column's type affinity to the
     * bound text, so numeric columns match numeric text. @return rows deleted.
     */
    public int delete(String table, String column, String value) {
        requireIdentifier(table);
        requireIdentifier(column);
        return helper.getWritableDatabase().delete(quote(table), quote(column) + " = ?", new String[]{value});
    }

    /**
     * Reads every row. Values are {@link Long}, {@link Double}, {@link String}, {@code byte[]} or
     * {@code null}, keyed by column name in column order.
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

    /** Runs {@code work} in one transaction; an exception rolls everything back. */
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
