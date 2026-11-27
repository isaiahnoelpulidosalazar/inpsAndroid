package com.isaiahnoelpulidosalazar.inpsandroid.core.database;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class EasySQLTest {

    private EasySQL db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = new EasySQL(context, "test_inps.db");
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testCrudAndTransactions() {
        assertFalse(db.tableExists("items"));

        Map<String, String> cols = new LinkedHashMap<>();
        cols.put("id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        cols.put("name", "TEXT NOT NULL");
        cols.put("score", "REAL");
        db.createTable("items", cols);
        assertTrue(db.tableExists("items"));

        Map<String, String> row1 = new LinkedHashMap<>();
        row1.put("name", "Alpha");
        row1.put("score", "9.5");
        long id1 = db.insert("items", row1);
        assertTrue(id1 > 0);

        Map<String, String> row2 = new LinkedHashMap<>();
        row2.put("name", "Beta");
        row2.put("score", "4.0");
        db.insert("items", row2);

        List<Map<String, Object>> rows = db.selectAll("items");
        assertEquals(2, rows.size());
        assertEquals("Alpha", rows.get(0).get("name"));
        assertEquals(9.5, (Double) rows.get(0).get("score"), 1e-6);

        assertEquals(1, db.delete("items", "name", "Alpha"));
        assertEquals(1, db.selectAll("items").size());

        // Transaction rollback on exception
        assertThrows(RuntimeException.class, () -> db.inTransaction(() -> {
            db.insert("items", Collections.singletonMap("name", "RolledBack"));
            throw new RuntimeException("force rollback");
        }));
        assertEquals(1, db.selectAll("items").size());

        // Transaction commit
        db.inTransaction(() -> db.insert("items", Collections.singletonMap("name", "Committed")));
        assertEquals(2, db.selectAll("items").size());

        assertEquals(2, db.clearTable("items"));
        assertEquals(0, db.selectAll("items").size());

        db.dropTable("items");
        assertFalse(db.tableExists("items"));
    }

    @Test
    public void testRejectsUnsafeIdentifiers() {
        Map<String, String> validCols = Collections.singletonMap("col", "TEXT");
        assertThrows(IllegalArgumentException.class, () -> db.createTable("bad; DROP TABLE x", validCols));
        assertThrows(IllegalArgumentException.class, () -> db.createTable("empty", Collections.emptyMap()));
        assertThrows(IllegalArgumentException.class,
                () -> db.createTable("bad_col_type", Collections.singletonMap("c", "TEXT; DROP TABLE x")));
        assertThrows(IllegalArgumentException.class, () -> db.tableExists("bad; DROP TABLE x"));
        assertThrows(IllegalArgumentException.class, () -> db.dropTable("bad name"));
        assertThrows(IllegalArgumentException.class, () -> db.clearTable(null));
        assertThrows(IllegalArgumentException.class, () -> db.insert("items", Collections.singletonMap("1bad", "v")));
        assertThrows(IllegalArgumentException.class, () -> db.delete("items", "bad-col", "v"));
        assertThrows(IllegalArgumentException.class, () -> db.selectAll("123invalid"));
    }
}
