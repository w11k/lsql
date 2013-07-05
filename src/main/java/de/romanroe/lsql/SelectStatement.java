package de.romanroe.lsql;

public class SelectStatement {

    private final Table table;
    private final String columns;

    public SelectStatement(Table table, String columns) {
        this.table = table;
        this.columns = columns;
    }

    public Table getTable() {
        return table;
    }

    public Where where(String whereString) {
        String select = "select " + columns + " from " + table.getTableName();
        if (whereString != null && !whereString.equals("")) {
            select += " where " + whereString;
        }
        return new Where(this, select);
    }

    public Where where() {
        return where(null);
    }
}
