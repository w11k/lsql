package com.w11k.lsql.dialects;

import com.google.common.base.Optional;
import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;

public interface Dialect {

    Converter getConverter();

    Optional<Object> extractGeneratedPk(ResultSet resultSet) throws Exception;

}
