package com.w11k.lsql.schemaexporter;

import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class SchemaExporter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static File pathRelativeToProjectRoot(String fileInProjectRoot, String folderRelativeToProjectRoot) {
        try {
            URL resource = SchemaExporter.class.getResource("/");
            File folder = new File(resource.toURI());

            while (folder != null) {
                File maybeFile = new File(folder, fileInProjectRoot);
                if (maybeFile.exists()) {
                    return new File(folder, folderRelativeToProjectRoot);
                }
                folder = folder.getParentFile();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("No parent folder with file '" + fileInProjectRoot + "' found");
    }

    private final LSql lSql;

    private File outputDir = null;

    private String packageName;

    public SchemaExporter(LSql lSql) {
        this.lSql = lSql;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputPath(File outputDir) {
        this.outputDir = outputDir;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void export() {
        String packageFolder = this.packageName.replaceAll("\\.", File.separator);
        File packageFolderFile = new File(this.outputDir, packageFolder);

        packageFolderFile.mkdirs();
        assert packageFolderFile.isDirectory();
        assert packageFolderFile.exists();

        for (Table table : this.lSql.getTables()) {
            logger.info("Generating POJO for table '" + table.getTableName() + "'");
            new TableExporter(table, this, packageFolderFile).export();
        }
    }


}
