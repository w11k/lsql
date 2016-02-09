package com.w11k.lsql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LiteralQueryParameter {

    public String getSqlString() {
        return "";
    }

    public int getNumberOfQueryParameters() {
        return 0;
    }

    public void set(PreparedStatement ps, int preparedStatementIndex, int localIndex) throws SQLException {
    }

}
