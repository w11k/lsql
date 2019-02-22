package com.w11k.lsql.query;

import com.w11k.lsql.LSql;
import com.w11k.lsql.PojoMapper;
import com.w11k.lsql.RowDeserializer;
import com.w11k.lsql.converter.Converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class PojoQuery<T> extends AbstractQuery<T> {

    private final PojoMapper<T> pojoMapper;

    private final Class<T> pojoClass;

    private final RowDeserializer<T> rowDeserializer = new RowDeserializer<T>() {
        @Override
        public T createRow() {
            return PojoQuery.this.pojoMapper.newInstance();
        }

        @Override
        public String getDeserializedFieldName(LSql lSql, String internalSqlName) {
            return lSql.convertInternalSqlToRowKey(internalSqlName);
        }

        @Override
        public void deserializeField(
                LSql lSql, T row, Converter converter, String internalSqlColumnName, ResultSet resultSet, int resultSetColumnPosition)
                throws Exception {

            PojoQuery.this.pojoMapper.setValue(
                    row,
                    this.getDeserializedFieldName(lSql, internalSqlColumnName),
                    converter.getValueFromResultSet(lSql, resultSet, resultSetColumnPosition));
        }
    };

    public PojoQuery(LSql lSql, PreparedStatement preparedStatement, Class<T> pojoClass, Map<String, Converter> outConverters) {
        super(lSql, preparedStatement, outConverters);
        this.pojoMapper = PojoMapper.getFor(pojoClass);
        this.pojoClass = pojoClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> toTree() {
        return (List<T>) new QueryToTreeConverter(this, new PojoEntityCreator<T>(this.pojoClass)).getTree();
    }

    @Override
    protected RowDeserializer<T> getRowDeserializer() {
        return this.rowDeserializer;
    }

}
