package com.w11k.lsql;

import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public class RowPojoTest {

    private static class Pojo1 extends RowPojo {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void getProperty() throws Exception {
        Pojo1 p1 = new Pojo1();
        p1.setName("max");
        assertEquals(p1.get("name"), "max");
    }

    @Test
    public void getNoProperty() throws Exception {
        Pojo1 p1 = new Pojo1();
        p1.put("foo", "bar");
        assertEquals(p1.get("foo"), "bar");
    }

    @Test
    public void getAsProperty() throws Exception {
        Pojo1 p1 = new Pojo1();
        p1.setName("max");
        assertEquals(p1.getString("name"), "max");
    }

    @Test
    public void getAsNoProperty() throws Exception {
        Pojo1 p1 = new Pojo1();
        p1.put("foo", "bar");
        assertEquals(p1.getString("foo"), "bar");
    }

    @Test
    public void putProperty() throws Exception {
        Pojo1 p1 = new Pojo1();
        p1.put("name", "max");
        assertEquals(p1.getName(), "max");
    }

    @Test
    public void keySet() throws Exception {
        Pojo1 p1 = new Pojo1();
        p1.put("foo", "bar");
        Set<String> keys = p1.keySet();
        assertTrue(keys.contains("foo"));
        assertTrue(keys.contains("name"));
    }

    @Test
    public void size() {
        Pojo1 p1 = new Pojo1();
        p1.put("foo", "bar");
        assertEquals(p1.size(), 2);
    }

    @Test
    public void isEmpty() {
        assertFalse(new Pojo1().isEmpty());
    }

    @Test
    public void containsKey() {
        assertTrue(new Pojo1().containsKey("name"));
    }

    @Test
    public void containsValue() {
        Pojo1 p1 = new Pojo1();
        p1.setName("max");
        assertTrue(p1.containsValue("max"));
    }

    @Test
    public void remove() {
        Pojo1 p1 = new Pojo1();
        p1.setName("max");
        p1.remove("name");
        assertNull(p1.getName());
    }

    @Test
    public void values() {
        Pojo1 p1 = new Pojo1();
        p1.setName("max");
        Collection<Object> values = p1.values();
        assertTrue(values.contains("max"));
    }

    @Test
    public void entrySet() {
        Pojo1 p1 = new Pojo1();
        p1.setName("max");
        p1.put("foo", "bar");
        Set<Map.Entry<String, Object>> entries = p1.entrySet();
        assertEquals(entries.size(), 2);
    }

}
