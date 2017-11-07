package com.w11k.lsql.cli.java;

import com.w11k.lsql.TableLike;

public final class StatementRowExporter extends JavaRowClassExporter {

    public StatementRowExporter(TableLike tableLike, JavaExporter javaExporter) {
        super(tableLike, javaExporter);
    }

    @Override
    public String getLastPackageSegmentForSchema() {
        return this.getTableLike().getSchemaName();
    }


}
