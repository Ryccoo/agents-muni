package test;

import backend.*;

import static test.AgentManagerImplTest.newAgent;
import static test.AgentManagerImplTest.assertDeepEquals;
import static test.MissionManagerImplTest.newMission;
import static test.MissionManagerImplTest.assertDeepEquals;

import db.AgentsMissionsTable;
import db.AgentsTable;
import db.MissionTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import org.apache.commons.dbcp.BasicDataSource;
import utils.DBUtils;
import utils.IllegalEntityException;

import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import static org.junit.Assert.*;

/**
 * Created by richard on 31.3.2014.
 */
public class AssigmentManagerImplTest {
    private AssigmentManagerImpl manager;
    private AgentManagerImpl agentManager;
    private MissionManagerImpl missionManager;
    private DataSource dataSource;

    private DataSource prepareDataSource() throws SQLException {
        String jdbc_path = System.getenv("ISIS_JDBC_PATH");
        BasicDataSource dataSource = new BasicDataSource();
        //we will use in memory database
        dataSource.setUrl(jdbc_path);

        Connection conn = null;
        conn = dataSource.getConnection();
        AgentsTable.create(conn);
        MissionTable.create(conn);
        AgentsMissionsTable.create(conn);
        DBUtils.closeQuietly(conn);
        return dataSource;
    }

    private Agent a1, a2, a3, a4, a5, agentWithNullId, agentNotInDB;
    private Mission m1, m2, m3, missionWithNullId, missionNotInDB;

    private void prepareTestData() {

        m1 = newMission("Mission 1", "Destination 1", "Description 1", false);
        m2 = newMission("Mission 2", "Destination 2", "Description 2", true);
        m3 = newMission("Mission 3", "Destination 3", "Description 3", false);

        a1 = newAgent("Agent 1", "Rank 1", true);
        a2 = newAgent("Agent 2", "Rank 2", true);
        a3 = newAgent("Agent 3", "Rank 3", true);
        a4 = newAgent("Agent 4", "Rank 4", true);
        a5 = newAgent("Agent 5", "Rank 5", true);

        agentManager.addAgent(a1);
        agentManager.addAgent(a2);
        agentManager.addAgent(a3);
        agentManager.addAgent(a4);
        agentManager.addAgent(a5);

        missionManager.createMission(m1);
        missionManager.createMission(m2);
        missionManager.createMission(m3);


        missionWithNullId = newMission("Mission NOID", "Dest NOID", "DESC NO ID", false);
        missionNotInDB = newMission("Mission NODB", "Dest NODB", "Desc NODB", true);
        missionWithNullId.setId(m3.getId() + 55);
        agentNotInDB = newAgent("Agent NODB", "Rank NODB", true);
        agentNotInDB.setId(a5.getId()+ 55);

    }

    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
        dataSource = prepareDataSource();
        manager = new AssigmentManagerImpl();
        agentManager = new AgentManagerImpl();
        missionManager = new MissionManagerImpl();
        agentManager.setDataSource(dataSource);
        missionManager.setDataSource(dataSource);
        manager.setDataSource(dataSource);
        agentWithNullId = newAgent("Agent NOID", "Rank NOID", false);

        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException {
        Connection conn = null;
        conn = dataSource.getConnection();
        AgentsMissionsTable.drop(conn);
        MissionTable.drop(conn);
        AgentsTable.drop(conn);
        DBUtils.closeQuietly(conn);
    }

    @Test
    public void testAssignAgentToMission() throws Exception {
        m1 = missionManager.findAllMissions().get(0);
        a1 = agentManager.findAllAgents().get(0);

        manager.assignAgentToMission(a1, m1);
        List<Agent> agents = manager.getMissionAgents(m1);
        List<Mission> missions = manager.getAgentMissions(a1);
        assertEquals(1, agents.size());
        assertEquals(1, missions.size());
        assertDeepEquals(a1, agents.get(0));
        assertDeepEquals(m1, missions.get(0));
    }

    @Test
    public void testremoveAgentFromMission() throws Exception {
        manager.assignAgentToMission(a1, m1);
        manager.assignAgentToMission(a2, m1);
        List<Agent> agents = manager.getMissionAgents(m1);
        assertEquals(2, agents.size());
        manager.removeAgentFromMission(a1, m1);
        agents = manager.getMissionAgents(m1);
        assertEquals(1, agents.size());
        assertDeepEquals(a2, agents.get(0));
    }

    @Test
    public void testGetAgentsMissions() throws Exception {
        manager.assignAgentToMission(a1, m1);
        manager.assignAgentToMission(a2, m1);
        manager.assignAgentToMission(a2, m2);
        List<Mission> missionsA1 = manager.getAgentMissions(a1);
        List<Mission> missionsA2 = manager.getAgentMissions(a2);
        assertEquals(1, missionsA1.size());
        assertEquals(2, missionsA2.size());
        assertTrue(missionsA2.contains(m1));
        assertTrue(missionsA2.contains(m2));
    }

    @Test (expected = IllegalEntityException.class)
    public void testAddWithWrongAgent() {
        manager.assignAgentToMission(agentWithNullId, m1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddWithWithNullAgent() {
        manager.assignAgentToMission(null, m1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddWithWithNullMission() {
        manager.assignAgentToMission(a1, null);
    }
}
