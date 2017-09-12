package com.w11k.lsql.cli.java;

import com.w11k.lsql.ColumnsContainer;

import java.io.File;

public final class StatementExporter extends JavaRowClassExporter {


    public StatementExporter(ColumnsContainer columnsContainer, JavaExporter javaExporter, File rootPackage) {
        super(columnsContainer, javaExporter, rootPackage);
    }
}
