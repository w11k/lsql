package com.w11k.lsql.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Converter {

    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException;

    public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException;

}
