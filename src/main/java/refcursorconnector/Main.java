package refcursorconnector;
import java.sql.*;


public class Main {
    private static final String url = "jdbc:postgresql://localhost:5433/postgres";
    private static final String user = "midpoint";
    private static final String password = "y8cDv3pEDu6c69R7znm1";

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(url, user, password);

        // Setup function to call.
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '" +
                " DECLARE " +
                "    mycurs refcursor; " +
                " BEGIN " +
                "    OPEN mycurs FOR SELECT 1 UNION SELECT 2; " +
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
