package db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by tulak on 08.04.2014.
 */
public class AgentsMissionsTable {
    public static void create(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE agents_missions (" +
                "agentId BIGINT REFERENCES agents (id)," +
                "missionId BIGINT REFERENCES missions (id), " +
                "PRIMARY KEY (agentId, missionId)" +
                ")").executeUpdate();
    }

    public static void drop(Connection conn) throws SQLException {
        conn.prepareStatement("DROP TABLE agents_missions").executeUpdate();
    }
}
