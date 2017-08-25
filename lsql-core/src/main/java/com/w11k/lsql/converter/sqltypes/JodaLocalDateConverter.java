package com.w11k.lsql.converter.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import org.joda.time.LocalDate;

import java.sql.*;

public class JodaLocalDateConverter extends Converter {

    public JodaLocalDateConverter() {
        super(LocalDate.class, Types.DATE);
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        LocalDate dt = (LocalDate) val;
        Timestamp ts = new Timestamp(dt.toDate().getTime());
        ps.setTimestamp(index, ts);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return new LocalDate(timestamp.getTime());
        } else {
            return null;
        }
    }
}
