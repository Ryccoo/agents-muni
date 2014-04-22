package backend;

import utils.DBUtils;
import utils.IllegalEntityException;
import utils.ServiceFailureException;
import utils.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by richard on 11.3.2014.
 */
public class AgentManagerImpl implements AgentManager {
    public static final Logger logger = Logger.getLogger(AgentManagerImpl.class.getName());
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
    public void updateAgent(Agent agent) throws ServiceFailureException {
        checkDataSource();
        validate(agent);

        if (agent.getId() == null) {
            throw new IllegalEntityException("body id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false); // it will be turned on by closeQuietly
            st = conn.prepareStatement(
                    "UPDATE agents SET name = ?, rank = ?, secret = ? WHERE id = ?");
            st.setString(1, agent.getName());
            st.setString(2, agent.getRank());
            st.setBoolean(3, agent.isSecret());
            st.setLong(4, agent.getId());

            int updated = st.executeUpdate();
            DBUtils.checkUpdatesCount(updated, agent, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating agent in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Agent findAgent(Long id) throws ServiceFailureException {

        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, rank, secret FROM agents WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleAgent(st);
        } catch (SQLException ex) {
            String msg = "Error when retrieving agent with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }

    }

    @Override
    public List<Agent> findAllAgents() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id,name,rank,secret FROM agents");
            return executeQueryForMultipleAgents(st);
        } catch (SQLException ex) {
            String msg = "Error when retrieving all agents";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void removeAgent(Agent agent) throws ServiceFailureException {
        checkDataSource();
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalEntityException("agent is not stored");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM agents WHERE id = ?");
            st.setLong(1, agent.getId());
            int deleted = st.executeUpdate();
            DBUtils.checkUpdatesCount(deleted, agent, false);
            conn.commit();
        } catch( SQLException ex) {
            String msg = "Error when removing agent " + agent;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void addAgent(Agent agent) throws ServiceFailureException {
        checkDataSource();
        validate(agent);
        if (agent.getId() != null) {
            throw new IllegalEntityException("agent id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO agents (name,rank,secret) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, agent.getName());
            st.setString(2, agent.getRank());
            st.setBoolean(3, agent.isSecret());
            int addedRows = st.executeUpdate();
            DBUtils.checkUpdatesCount(addedRows, agent, true);
            Long id = DBUtils.getId(st.getGeneratedKeys());
            agent.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting agent " + agent;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    static Agent executeQueryForSingleAgent(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Agent result = resultSetToAgent(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more agents with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Agent> executeQueryForMultipleAgents(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Agent> result = new ArrayList<Agent>();
        while (rs.next()) {
            result.add(resultSetToAgent(rs));
        }
        return result;
    }

    static private Agent resultSetToAgent(ResultSet rs) throws SQLException {
        Agent a = new Agent();
        a.setId(rs.getLong("id"));
        a.setName(rs.getString("name"));
        a.setRank(rs.getString("rank"));
        a.setSecret(rs.getBoolean("secret"));
        return a;
    }

    static private void validate(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getName() == null || agent.getName().length() == 0) {
            throw new ValidationException("name is not set");
        }
        if (agent.getRank() == null || agent.getRank().length() == 0) {
            throw new ValidationException("rank is not set");
        }
    }
}
