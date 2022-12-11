package refcursorconnector;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;

public class PostgresService {
    /**
     * Возвращает ref-cursor к текущей БД
     */
    public static ResultSet getRefCursor(Connection jbdcConnection) {
        try {
            var stmt = jbdcConnection.createStatement();
            stmt.execute("CREATE OR REPLACE FUNCTION refcursorfunc() RETURNS refcursor AS '" +
                    " DECLARE " +
                    "    mycurs refcursor; " +
                    " BEGIN " +
                    "    OPEN mycurs FOR SELECT * FROM accounts; " +
                    "    RETURN mycurs; " +
                    " END;' language plpgsql");
            stmt.close();

            jbdcConnection.setAutoCommit(false);

            CallableStatement func = jbdcConnection.prepareCall("{? = call refcursorfunc() }");
            func.registerOutParameter(1, Types.OTHER);
            func.execute();
            return (ResultSet) func.getObject(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initTable(Connection jbdcConnection) throws FileNotFoundException {
        var runner = new ScriptRunner(jbdcConnection);
        var reader = new BufferedReader(new FileReader("src/main/resources/create_table.sql"));
        runner.runScript(reader);
    }
}
