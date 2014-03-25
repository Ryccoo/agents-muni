package backend;

import backend.Agent;
import backend.AgentManager;

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
    private Connection conn;

    public AgentManagerImpl(Connection connection) {
        this.conn = connection;
    }

    @Override
    public boolean updateAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalArgumentException("agent is not stored");
        }

        PreparedStatement st = null;
        Boolean ret = false;
        try {
            st = conn.prepareStatement("UPDATE agents SET name = ?, rank = ?, secret = ? WHERE id = ?");
            st.setString(1, agent.getName());
            st.setString(2, agent.getRank());
            st.setBoolean(3, agent.isSecret());
            st.setLong(4, agent.getId());

            int updated = st.executeUpdate();
            if(updated != 1) {
                throw new ServiceFailureException("Internal error: Expected to update 1 record, updated `"+ updated+"` records");
            }
            ret = true;
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating agent with id " + agent.getId(), ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            return ret;
        }
    }

    @Override
    public Agent findAgent(Long id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("SELECT id, name, rank, secret FROM agents WHERE id = ?");
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Agent agent = resultSetToAgent(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + agent + " and " + resultSetToAgent(rs));
                }

                return agent;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving agent with id " + id, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @Override
    public List<Agent> findAllAgents() {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("SELECT id,name,rank,secret FROM agents");
            ResultSet rs = st.executeQuery();

            List<Agent> result = new ArrayList<Agent>();
            while (rs.next()) {
                result.add(resultSetToAgent(rs));
            }
            return result;

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all agents", ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void removeAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() == null) {
            throw new IllegalArgumentException("agent is not stored");
        }
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM agents WHERE id = ?");
            st.setLong(1, agent.getId());
            int deleted = st.executeUpdate();
            if(deleted != 1) {
                throw new ServiceFailureException("Internal error: Expected to delete 1 record, deleted `"+ deleted +"` records");
            }
        } catch( SQLException ex) {
            throw new ServiceFailureException("Error when removing agent " + agent, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void addAgent(Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent is null");
        }
        if (agent.getId() != null) {
            throw new IllegalArgumentException("agent id is already set");
        }
        if (agent.getName().length() == 0) {
            throw new IllegalArgumentException("agent name must be set");
        }
        if (agent.getRank().length() == 0) {
            throw new IllegalArgumentException("agent rank must be set");
        }

        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "INSERT INTO agents (name,rank,secret) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, agent.getName());
            st.setString(2, agent.getRank());
            st.setBoolean(3, agent.isSecret());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows "
                        + "inserted when trying to insert agent " + agent);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            agent.setId(getKey(keyRS, agent));

        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting agent " + agent, ex);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private Agent resultSetToAgent(ResultSet rs) throws SQLException {
        Agent a = new Agent();
        a.setId(rs.getLong("id"));
        a.setName(rs.getString("name"));
        a.setRank(rs.getString("rank"));
        a.setSecret(rs.getBoolean("secret"));
        return a;
    }

    private Long getKey(ResultSet keyRS, Agent agent) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert agent " + agent
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert agent " + agent
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert agent " + agent
                    + " - no key found");
        }
    }
}
