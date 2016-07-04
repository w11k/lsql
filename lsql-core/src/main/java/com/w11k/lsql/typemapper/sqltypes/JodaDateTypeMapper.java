package com.w11k.lsql.typemapper.sqltypes;

import com.w11k.lsql.LSql;
import com.w11k.lsql.typemapper.TypeMapper;
import org.joda.time.DateTime;

import java.sql.*;

public class JodaDateTypeMapper extends TypeMapper {

    public static final JodaDateTypeMapper INSTANCE = new JodaDateTypeMapper();

    public JodaDateTypeMapper() {
        super(
          DateTime.class,
          new int[]{Types.TIMESTAMP},
          Types.TIMESTAMP
        );
    }

    @Override
    protected void setValue(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
        DateTime dt = (DateTime) val;
        Timestamp ts = new Timestamp(dt.getMillis());
        ps.setTimestamp(index, ts);
    }

    @Override
    protected Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        if (timestamp != null) {
            return new DateTime(timestamp.getTime());
        } else {
            return null;
        }
    }
}
