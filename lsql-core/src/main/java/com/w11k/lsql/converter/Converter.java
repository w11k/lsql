package com.w11k.lsql.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface Converter {

    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws Exception;

    public Object getValueFromResultSet(ResultSet rs, int index) throws Exception;

}
