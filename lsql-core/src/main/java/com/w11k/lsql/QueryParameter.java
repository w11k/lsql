package com.w11k.lsql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryParameter {

    void set(PreparedStatement ps, int index) throws SQLException;


}
