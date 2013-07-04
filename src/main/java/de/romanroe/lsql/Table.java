package de.romanroe.lsql;

public class Table {

    final private LSql lSql;
    final private String tableName;

    public Table(LSql lSql, String tableName) {
        this.lSql = lSql;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public SelectStatement select(String columns) {
        return new SelectStatement(lSql, this, columns);
    }

    public SelectStatement select() {
        return select("*");
    }

}
