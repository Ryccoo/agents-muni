package test;

import backend.Mission;
import backend.MissionManagerImpl;
import config.IsisConfig;
import db.MissionTable;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.DBUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by richard on 11.03.2014.
 */
public class MissionManagerImplTest {
    private MissionManagerImpl manager;
    private DataSource dataSource;

    private DataSource prepareDataSource() throws SQLException {
        String jdbc_path = IsisConfig.getProperty("TestJdbcPath");
        BasicDataSource dataSource = new BasicDataSource();
        //we will use in memory database
        dataSource.setUrl(jdbc_path);

        Connection conn = null;
        conn = dataSource.getConnection();
        MissionTable.create(conn);
        DBUtils.closeQuietly(conn);
        return dataSource;
    }

    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
        dataSource = prepareDataSource();
        manager = new MissionManagerImpl();
        manager.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        Connection conn = null;
        conn = dataSource.getConnection();
        MissionTable.drop(conn);
        DBUtils.closeQuietly(conn);
    }

    @Test
    public void testCreateMission() throws Exception {
        Mission mission = newMission("Zabitie prezidenta", "USA", "Urobit co najtichsie", true);
        manager.createMission(mission);
        Long missionID = mission.getId();
        assertNotNull(missionID);

        Mission result = manager.findMission(missionID);
        assertEquals(mission, result);
        assertNotSame(mission, result);
        assertDeepEquals(mission, result);
    }

    @Test
    public void testRemoveMission() throws Exception {
        Mission mission1 = newMission("Zabitie prezidenta", "USA", "Urobit co najtichsie", true);
        Mission mission2 = newMission("Sledovacka", "Maroko", "Nezjest vsetky koblihy", false);
        manager.createMission(mission1);
        manager.createMission(mission2);

        assertNotNull(manager.findMission(mission1.getId()));
        assertNotNull(manager.findMission(mission2.getId()));

        manager.removeMission(mission2);
        assertNotNull(manager.findMission(mission1.getId()));
        assertNull(manager.findMission(mission2.getId()));
    }


    @Test
    public void testFindNonExisting() throws Exception {
        assertNull(manager.findMission(42l));
    }

    @Test
    public void testFindMission() throws Exception {
        Mission mission = newMission("Zabitie prezidenta", "USA", "Urobit co najtichsie", true);
        manager.createMission(mission);
        Long missionID = mission.getId();
        assertNotNull(missionID);

        Mission result = manager.findMission(missionID);
        assertEquals(mission, result);
        assertDeepEquals(mission, result);
    }

    @Test
    public void testFindAllMissions() throws Exception {
        Mission mission1 = newMission("Mission 1", "SVK", "Attack fico", true);
        Mission mission2 = newMission("Mission 2", "CZ", "Finish FI", false);
        manager.createMission(mission1);
        manager.createMission(mission2);

        List<Mission> result = manager.findAllMissions();
        assertEquals(2, result.size());
    }

    @Test
    public void testUpdateMission() throws Exception {
        String new_name = "Mission 2";
        Mission mission = newMission("Mission 1", "Karibik", "Prestrelka", true);
        manager.createMission(mission);

        mission.setName(new_name);
        manager.updateMission(mission);
        Mission result = manager.findMission(mission.getId());
        assertEquals(new_name, result.getName());
    }

    static Mission newMission(String name, String destination, String description, boolean secret) {

        Mission tmp = new Mission();
        tmp.setName(name);
        tmp.setDestination(destination);
        tmp.setDescription(description);
        tmp.setSecret(secret);

        return tmp;

    }

    static void assertDeepEquals(Mission expected, Mission actual) {

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getDestination(), actual.getDestination());
        assertEquals(expected.isSecret(), actual.isSecret());

    }

}
