package com.w11k.lsql.converter;

import com.w11k.lsql.LSql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Converter {

    public void setValueInStatement(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException;

    public Object getValueFromResultSet(LSql lSql, ResultSet rs, int index) throws SQLException;

}
