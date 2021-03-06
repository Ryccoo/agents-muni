package backend;

import utils.DBUtils;
import utils.IllegalEntityException;
import utils.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
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
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO agents_missions (agentId, missionId) VALUES (?, ?)"); // need a statement here :D
            st.setLong(1, agent.getId());
            st.setLong(2, mission.getId());
            int inserted = st.executeUpdate();
            DBUtils.checkUpdatesCount(inserted, agent, false);
            conn.commit();
            logger.log(Level.INFO, "Agent assigned to mission");
        } catch (SQLException ex) {
            String msg = "SQL Error when adding agent to mission";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void removeAgentFromMission(Agent agent, Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
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
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM agents_missions WHERE agentId = ? AND missionId = ?"); // need a statement here :D
            st.setLong(1, agent.getId());
            st.setLong(2, mission.getId());
            int deleted = st.executeUpdate();
            DBUtils.checkUpdatesCount(deleted, agent, false);
            conn.commit();
            logger.log(Level.INFO, "Agent removed from mission");
        } catch (SQLException ex) {
            String msg = "SQL Error when adding agent to mission";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
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
                    "SELECT id, name, rank, secret FROM agents INNER JOIN agents_missions ON agents_missions.agentid = agents.id WHERE agents_missions.missionId = ?"); // need a statement here :D
            st.setLong(1, mission.getId());
            logger.log(Level.INFO, "Mission agents collected");
            return AgentManagerImpl.executeQueryForMultipleAgents(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all mission agents from DB";
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
                    "SELECT id, name, destination, description, secret FROM missions INNER JOIN agents_missions ON agents_missions.missionid = missions.id WHERE agents_missions.agentId = ?");
            st.setLong(1, agent.getId());
            logger.log(Level.INFO, "Agent missions collected");
            return MissionManagerImpl.executeQueryForMultipleMissions(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all missions from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void removeAllAgentMissions(Agent agent) {
        List<Mission> missions = getAgentMissions(agent);
        for(Mission mission : missions) {
            removeAgentFromMission(agent, mission);
        }
        logger.log(Level.INFO, "All agent missions associations removed");
    }

    @Override
    public void removeAllMissionAgents(Mission mission) {
        List<Agent> agents = getMissionAgents(mission);
        for(Agent agent : agents) {
            removeAgentFromMission(agent, mission);
        }
        logger.log(Level.INFO, "All mission agents associations removed");
    }
}
