package com.w11k.lsql.cli_tests.tests;

import com.google.common.base.Optional;
import com.w11k.lsql.cli.schema_public.Crud_Row;
import com.w11k.lsql.cli.schema_public.Crud_Table;
import com.w11k.lsql.utils.JdbcTestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import static java.lang.Integer.valueOf;
import static org.testng.Assert.assertEquals;

public final class CrudTest extends AbstractTestCliTest {

    @Test
    public void insert() throws Exception {
        Crud_Table crudTable = new Crud_Table(lSql);
        Optional<Integer> pk = crudTable.insert(new Crud_Row().withId(1).withFieldB("a"));
        assertEquals(pk.get(), valueOf(1));

        JdbcTestUtils.queryForEach(
                lSql.getConnectionProvider().call(),
                "select * from crud;",
                rs -> {
                    assertEquals(rs.getInt("id"), 1);
                    assertEquals(rs.getString("field_b"), "a");
                    return false;
                });
    }

    @Test
    public void insertAndLoad() {
        Crud_Table crudTable = new Crud_Table(lSql);
        Crud_Row p1 = crudTable.insertAndLoad(new Crud_Row().withId(1).withFieldA(9));
        assertEquals(p1.getId(), valueOf(1));
        assertEquals(p1.fieldA, Integer.valueOf(9));
    }

    @Test
    public void load() {
        Crud_Table crudTable = new Crud_Table(lSql);
        crudTable.insert(new Crud_Row().withId(1).withFieldB("a"));

        Optional<Crud_Row> rowOptional = crudTable.load(1);
        Assert.assertTrue(rowOptional.isPresent());
        Crud_Row row = rowOptional.get();
        assertEquals(row.getId(), new Integer(1));
        assertEquals(row.getFieldB(), "a");
    }

    @Test
    public void delete() {
        Crud_Table crudTable = new Crud_Table(lSql);
        crudTable.insert(new Crud_Row().withId(1).withFieldB("a"));
        crudTable.delete(new Crud_Row().withId(1));

        Optional<Crud_Row> row = crudTable.load(1);
        Assert.assertFalse(row.isPresent());
    }

    @Test
    public void deleteById() {
        Crud_Table crudTable = new Crud_Table(lSql);
        crudTable.insert(new Crud_Row().withId(1).withFieldB("a"));
        crudTable.deleteById(1);

        Optional<Crud_Row> row = crudTable.load(1);
        Assert.assertFalse(row.isPresent());
    }

    @Test
    public void update() {
        Crud_Table crudTable = new Crud_Table(lSql);
        Crud_Row row = new Crud_Row().withId(1).withFieldB("a");
        crudTable.insert(row);
        crudTable.update(row.withFieldB("b"));
        row = crudTable.load(1).get();
        assertEquals(row.getFieldB(), "b");
    }

    @Test
    public void save() {
        Crud_Table table = new Crud_Table(this.lSql);
        Crud_Row row = new Crud_Row().withFieldA(999).withFieldB("bbb");
        table.save(row);
    }

}
