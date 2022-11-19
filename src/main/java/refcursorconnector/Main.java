package refcursorconnector;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.*;


public class Main {
    private static final String createTableFileName = Paths.get(".","src", "main", "resources", "create_table.sql").normalize().toString();

    private static final String url = "jdbc:postgresql://localhost:5433/postgres";
    private static final String user = "postgres";
    private static final String password = "postgres";

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url, user, password);

        var runner = new ScriptRunner(connection);
        var reader = new BufferedReader(new FileReader(createTableFileName));
        runner.runScript(reader);

        // Setup function to call
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '" +
                " DECLARE " +
                "    mycurs refcursor; " +
                " BEGIN " +
                "    OPEN mycurs FOR SELECT * FROM accounts; " +
                "    RETURN mycurs; " +
                " END;' language plpgsql");
        stmt.close();

        // We must be inside a transaction for cursors to work.
        connection.setAutoCommit(false);

        // Function call.
        CallableStatement func = connection.prepareCall("{? = call refcursorfunc() }");
        func.registerOutParameter(1, Types.OTHER);
        func.execute();
        ResultSet results = (ResultSet) func.getObject(1);
        while (results.next()) {
            System.out.println(results.getString(2));
        }
        results.close();
        func.close();
    }
}
