package doc;

import com.w11k.lsql.LSql;
import com.w11k.lsql.cli.Main;
import com.w11k.lsql.dialects.H2Config;
import com.w11k.lsql.jdbc.ConnectionProviders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import setup.Db;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.w11k.lsql.cli.tests.TestUtils.pathRelativeToProjectRoot;


public class CrudExamples {

    private LSql lSql;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        String url = "jdbc:h2:mem:lsqlexamples;mode=postgresql";

        Connection conn = DriverManager.getConnection(url);
        lSql = new LSql(H2Config.class, ConnectionProviders.fromInstance(conn));
        Db.createTables(lSql);

        String[] args = {
                "config:" + H2Config.class.getCanonicalName() + " ",
                "url:" + url,
                "user:",
                "password:",
                "sqlStatements:" + pathRelativeToProjectRoot("pom.xml", "./src/test/java/com/w11k/lsql/cli/tests"),
                "package:" + CrudExamples.class.getPackage().getName(),
                "outDirJava:" + pathRelativeToProjectRoot("pom.xml", "./src/generated/java/")
        };
        Main.main(args);
    }

    @Test
    public void insert() {
        /*
        [[[
        fdsfjdsfdsfdsfds fdsjfk jds lfdsf

        - fsfdsfs
        - fjdfdslfs

        ```
        */

//        Person_Table personTable = new Person_Table(this.lSql);
//        Person_Row personRow = new Person_Row()
//                .withId(1)
//                .withFirstName("Linda");
//        personTable.insert(personRow);
    }

}
