package backend;

import utils.DBUtils;
import utils.IllegalEntityException;
import utils.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by richard on 31.3.2014.
 */
public class AssigmentManagerImpl implements AssigmentManager {

    private static final Logger logger = Logger.getLogger(
            AssigmentManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }


    @Override
    public void assignAgentToMission(Agent agent, Mission mission) {
    }

    @Override
    public void removeAgentFromMission(Agent agent, Mission mission) {

    }

    @Override
    public List<Agent> getMissionAgents(Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "STATETMENT HERE !"); // need a statement here :D   
            return AgentManagerImpl.executeQueryForMultipleAgents(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all missions from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Mission> getAgentMissions(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalEntityException("agent id is null");
        }
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, rank, secret FROM agents");
            return MissionManagerImpl.executeQueryForMultipleMissions(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all missions from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }
}
