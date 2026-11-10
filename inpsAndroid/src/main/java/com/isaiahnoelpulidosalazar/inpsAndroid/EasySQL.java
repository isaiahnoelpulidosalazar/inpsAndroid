package com.isaiahnoelpulidosalazar.inpsAndroid;

import android.content.Context;
import android.util.Log;

import com.isaiahnoelpulidosalazar.inpsAndroid.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Legacy string-pair SQLite facade. Every call still swallows exceptions (logging them), as in v1,
 * but now runs on parameterised statements and one cached connection per database.
 *
 * @deprecated Since 2.0.0. Use {@link Database}. See {@code migrating_to_v2.md}.
 */
@Deprecated
public class EasySQL {

    private static final String TAG = "EasySQL";

    private final Context appContext;
    private final Map<String, Database> databases = new HashMap<>();

    public EasySQL(Context context) {
        this.appContext = context.getApplicationContext();
    }

    private synchronized Database db(String name) {
        Database d = databases.get(name);
        if (d == null) {
            d = new Database(appContext, name);
            databases.put(name, d);
        }
        return d;
    }

    public void createTable(String databaseName, String tableName, String[] columns) {
        try {
            Map<String, String> map = new LinkedHashMap<>();
            for (String column : columns) {
                int colon = column.indexOf(':');
                if (colon <= 0) throw new IllegalArgumentException("Expected \"name:TYPE\" but got: " + column);
                map.put(column.substring(0, colon), column.substring(colon + 1));
            }
            db(databaseName).createTable(tableName, map);
        } catch (Exception e) {
            Log.e(TAG, "createTable failed", e);
        }
    }

    public boolean doesTableExist(String databaseName, String tableName) {
        try {
            return db(databaseName).tableExists(tableName);
        } catch (Exception e) {
            Log.e(TAG, "doesTableExist failed", e);
            return false;
        }
    }

    public void deleteTable(String databaseName, String tableName) {
        try {
            db(databaseName).dropTable(tableName);
        } catch (Exception e) {
            Log.e(TAG, "deleteTable failed", e);
        }
    }

    public void clearTable(String databaseName, String tableName) {
        try {
            Database d = db(databaseName);
            if (d.tableExists(tableName)) d.clearTable(tableName);
        } catch (Exception e) {
            Log.e(TAG, "clearTable failed", e);
        }
    }

    /** Each value is {@code "column:value"}; the value is everything after the first colon. */
    public void insertToTable(String databaseName, String tableName, String[] values) {
        try {
            Database d = db(databaseName);
            if (!d.tableExists(tableName)) return;
            Map<String, String> row = new LinkedHashMap<>();
            for (String pair : values) {
                int colon = pair.indexOf(':');
                if (colon <= 0) throw new IllegalArgumentException("Expected \"column:value\" but got: " + pair);
                row.put(pair.substring(0, colon), pair.substring(colon + 1));
            }
            d.insert(tableName, row);
        } catch (Exception e) {
            Log.e(TAG, "insertToTable failed", e);
        }
    }

    public void deleteFromTable(String databaseName, String tableName, String columnValuePair) {
        try {
            Database d = db(databaseName);
            if (!d.tableExists(tableName)) return;
            int colon = columnValuePair.indexOf(':');
            if (colon <= 0) throw new IllegalArgumentException("Expected \"column:value\" but got: " + columnValuePair);
            d.delete(tableName, columnValuePair.substring(0, colon), columnValuePair.substring(colon + 1));
        } catch (Exception e) {
            Log.e(TAG, "deleteFromTable failed", e);
        }
    }

    public List<String> getTableValues(String databaseName, String tableName) {
        List<String> out = new ArrayList<>();
        for (String[] row : getTableValuesAsArray(databaseName, tableName)) {
            for (String cell : row) out.add(cell);
        }
        return out;
    }

    public List<String[]> getTableValuesAsArray(String databaseName, String tableName) {
        List<String[]> out = new ArrayList<>();
        try {
            Database d = db(databaseName);
            if (!d.tableExists(tableName)) return out;
            for (Map<String, Object> row : d.selectAll(tableName)) {
                String[] cells = new String[row.size()];
                int i = 0;
                for (Map.Entry<String, Object> e : row.entrySet()) {
                    Object v = e.getValue();
                    boolean numeric = v instanceof Long || v instanceof Double;
                    cells[i++] = numeric ? e.getKey() + ":" + v : e.getKey() + ":'" + v + "'";
                }
                out.add(cells);
            }
        } catch (Exception e) {
            Log.e(TAG, "getTableValues failed", e);
        }
        return out;
    }
}
