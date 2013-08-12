package com.w11k.lsql.converter;

import com.w11k.lsql.JavaSqlConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JavaBoolToSqlString extends JavaSqlConverter {

    private final String sqlStringValueForTrue;
    private final String sqlStringValueForFalse;

    public JavaBoolToSqlString(String sqlStringValueForTrue, String sqlStringValueForFalse) {
        this.sqlStringValueForTrue = sqlStringValueForTrue;
        this.sqlStringValueForFalse = sqlStringValueForFalse;
    }

    @Override public Object getValueFromResultSet(ResultSet rs, int index) throws SQLException {
        String val = rs.getString(index);
        if (val.equals(sqlStringValueForTrue)) {
            return true;
        } else if (val.equals(sqlStringValueForFalse)) {
            return false;
        } else {
            throw new IllegalArgumentException("Value must be "
                    + "'" + sqlStringValueForTrue + "' for yes and "
                    + "'" + sqlStringValueForFalse + "' for false.");
        }
    }

    @Override
    public void setValueInStatement(PreparedStatement ps, int index, Object val) throws SQLException {
        String yesOrNo = ((Boolean) val) ? sqlStringValueForTrue : sqlStringValueForFalse;
        ps.setString(index, yesOrNo);
    }


}
