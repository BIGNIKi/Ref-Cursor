package refcursorconnector;
import java.sql.*;


public class Main {
    private static final String url = "jdbc:postgresql://localhost:5433/postgres";
    private static final String user = "postgres";
    private static final String password = "postgres";

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);

        // create table
        Statement createTable = conn.createStatement();
        createTable.execute("Create Table accounts(id int primary key, name varchar, address text)");
        createTable.close();

        // insert data into accounts
        // TODO

        // Setup function to call
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '" +
                " DECLARE " +
                "    mycurs refcursor; " +
                " BEGIN " +
                "    OPEN mycurs FOR SELECT * FROM accounts; " +
                "    RETURN mycurs; " +
                " END;' language plpgsql");
        stmt.close();

        // We must be inside a transaction for cursors to work.
        conn.setAutoCommit(false);

        // Function call.
        CallableStatement func = conn.prepareCall("{? = call refcursorfunc() }");
        func.registerOutParameter(1, Types.OTHER);
        func.execute();
        ResultSet results = (ResultSet) func.getObject(1);
        while (results.next()) {
            System.out.println(results.getString(1));
        }
        results.close();
        func.close();
    }
}
