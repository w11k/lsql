package com.w11k.lsql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface LiteralQueryParameter {

    String getSqlString();

    int getNumberOfQueryParameters();

    void set(PreparedStatement ps, int preparedStatementIndex, int localIndex) throws SQLException;

}
