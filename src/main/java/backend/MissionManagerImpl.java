package backend;

import utils.DBUtils;
import utils.ValidationException;
import utils.IllegalEntityException;
import utils.ServiceFailureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Created by tulak on 11.03.2014.
 */
public class MissionManagerImpl implements MissionManager {
    private static final Logger logger = Logger.getLogger(
            MissionManagerImpl.class.getName());

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
    public void createMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        validate(mission);
        if (mission.getId() != null) {
            throw new IllegalEntityException("mission id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO missions (name,destination,description,secret) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, mission.getName());
            st.setString(2, mission.getDestination());
            st.setString(3, mission.getDescription());
            st.setBoolean(4, mission.isSecret());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            mission.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting mission into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void removeMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM missions WHERE id = ?");
            st.setLong(1, mission.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting mission from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Mission> findAllMissions() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, description, destination, secret FROM missions");
            return executeQueryForMultipleMissions(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all missions from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Mission findMission(Long id) throws ServiceFailureException {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, name, description, destination, secret FROM missions WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleMission(st);
        } catch (SQLException ex) {
            String msg = "Error when getting mission with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        validate(mission);
        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE missions SET name = ?, description = ?, destination = ?, secret = ? WHERE id = ?");
            st.setString(1, mission.getName());
            st.setString(2, mission.getDescription());
            st.setString(3, mission.getDestination());
            st.setBoolean(4, mission.isSecret());
            st.setLong(5, mission.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating mission in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    static Mission executeQueryForSingleMission(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Mission result = resultSetToMission(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more missions with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    static List<Mission> executeQueryForMultipleMissions(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Mission> result = new ArrayList<Mission>();
        while (rs.next()) {
            result.add(resultSetToMission(rs));
        }
        return result;
    }

    static private Mission resultSetToMission(ResultSet rs) throws SQLException {
        Mission m = new Mission();
        m.setId(rs.getLong("id"));
        m.setName(rs.getString("name"));
        m.setDescription(rs.getString("description"));
        m.setDestination(rs.getString("destination"));
        m.setSecret(rs.getBoolean("secret"));
        return m;
    }

    private static void validate(Mission mission) {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }
        if (mission.getName() == null) {
            throw new ValidationException("name is not set");
        }
        if (mission.getDestination() == null) {
            throw new ValidationException("destination is not set");
        }
        if (mission.getDescription() == null) {
            throw new ValidationException("description is not set");
        }
    }
}
