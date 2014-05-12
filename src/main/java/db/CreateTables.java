package db;

import org.apache.commons.dbcp.BasicDataSource;
import utils.DBUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by richard on 22.4.2014.
 */
public class CreateTables {
    public static BasicDataSource prepareDataSource(String path) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(path);

        return dataSource;
    }

    public static void main(String [ ] args) throws SQLException {
        String jdbc_path = System.getenv("ISIS_JDBC_PATH");
        BasicDataSource dataSource = prepareDataSource(jdbc_path);

        Connection conn = null;
        conn = dataSource.getConnection();
        AgentsTable.create(conn);
        MissionTable.create(conn);
        AgentsMissionsTable.create(conn);
        DBUtils.closeQuietly(conn);


        System.out.println("Tables created");
    }
}
