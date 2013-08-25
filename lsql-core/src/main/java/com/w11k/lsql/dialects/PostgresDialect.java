package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.ByTypeConverter;
import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;

public class PostgresDialect implements Dialect {

    @Override
    public Converter getConverter() {
        return new ByTypeConverter();
    }

    @Override
    public Optional<Object> extractGeneratedPk(ResultSet resultSet) throws Exception {
        return null;
    }
}
