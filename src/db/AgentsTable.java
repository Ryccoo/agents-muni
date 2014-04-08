package db;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tulak on 19.03.2014.
 */
public abstract class AgentsTable {
    public static void create(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE agents (" +
                "id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                "name VARCHAR(100)," +
                "rank VARCHAR(50)," +
                "secret BOOLEAN" +
                ")").executeUpdate();
    }

    public static void drop(Connection conn) throws SQLException {
        conn.prepareStatement("DROP TABLE agents").executeUpdate();
    }
}
