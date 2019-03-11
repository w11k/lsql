package com.w11k.lsql.cli;

import com.w11k.lsql.cli.java.JavaExporter;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public final class TestUtils {

    public static File pathRelativeToProjectRoot(String fileInProjectRoot, String folderRelativeToProjectRoot) {
        try {
            URL resource = JavaExporter.class.getResource("/");
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

}
