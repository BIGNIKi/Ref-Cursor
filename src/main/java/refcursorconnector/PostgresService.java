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

    /**
     * Инициализирует таблицу с пользователями в БД postgresSQL
     */
    public static void initTable(Connection jbdcConnection) throws FileNotFoundException {
        var runner = new ScriptRunner(jbdcConnection);
        var reader = new BufferedReader(new FileReader("src/main/resources/create_table.sql"));
        runner.runScript(reader);
    }

    /**
     * Возвращает схему для активного соединения с БД
     */
    public static ResultSet getScheme(Connection jbdcConnection) {
        try {
            final String SCHEMA_QUERY = "SELECT * FROM accounts WHERE id IS NULL";
            var stmt = jbdcConnection.createStatement();
            var result = stmt.executeQuery(SCHEMA_QUERY);
            jbdcConnection.commit();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
