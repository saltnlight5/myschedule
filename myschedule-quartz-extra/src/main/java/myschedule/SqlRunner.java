package myschedule;

import org.apache.commons.io.IOUtils;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;

/**
 * A utility program to execute any sql files. You may use this to quickly setup your database with Quartz
 * dbTable.sql schema files.
 */
public class SqlRunner {
    String[] args;
    public SqlRunner(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) throws Exception {
        new SqlRunner(args).run();
    }

    private void run() throws Exception {
        String driver = System.getProperty("driver", "com.mysql.jdbc.Driver");
        String url = System.getProperty("url", "jdbc:mysql://localhost:3306/quartz");
        String user = System.getProperty("user", "quartz");
        String password = System.getProperty("password", "quartz123");
        String argsMode = System.getProperty("argsMode", "sql"); // sql or file
        String sqlSep = System.getProperty("sqlSep", ";"); // SQL statement separator

        log("Loading JDBC driver " + driver);
        Class.forName(driver);

        log("Connecting to " + url + " with user=" + user);
        Connection conn = DriverManager.getConnection(url, user, password);

        try {
            Statement statement = conn.createStatement();
            for (String arg : args) {
                String sqlText = arg;
                if (argsMode.equals("file")) {
                    log("Reading file: " + arg);
                    sqlText = IOUtils.toString(new FileReader(arg));
                }

                for (String sqlStatement : sqlText.split(sqlSep)) {
                    log("Executing sql: " + sqlStatement);
                    boolean result = statement.execute(sqlStatement);
                    log("Sql result=" + result);
                }
            }
            statement.close();
            log("Done.");
        } finally {
            conn.close();
        }
    }

    private void log(String msg) {
        System.out.println(new Date() + " INFO " + msg);
    }
}
