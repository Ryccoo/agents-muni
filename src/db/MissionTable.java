package db;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * Created by richard on 25.3.2014.
 */
public class MissionTable {
    public static void create(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE missions (" +
                "id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                "name VARCHAR(100)," +
                "destination VARCHAR(100)," +
                "description CLOB," +
                "secret BOOLEAN" +
                ")").executeUpdate();
    }

    public static void drop(Connection conn) throws SQLException {
        conn.prepareStatement("DROP TABLE missions").executeUpdate();
    }
}
