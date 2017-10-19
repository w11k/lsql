package com.w11k.lsql.cli.java;

import com.w11k.lsql.ColumnsContainer;

public final class StatementRowExporter extends JavaRowClassExporter {

    public StatementRowExporter(ColumnsContainer columnsContainer, JavaExporter javaExporter) {
        super(columnsContainer, javaExporter);
    }

    @Override
    public String getLastPackageSegmentForSchema() {
        return this.getColumnsContainer().getSchemaName();
    }


}
