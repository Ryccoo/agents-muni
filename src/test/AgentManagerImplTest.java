package test;

import backend.Agent;
import backend.AgentManagerImpl;
import db.AgentsTable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import org.apache.commons.dbcp.BasicDataSource;
import utils.DBUtils;

import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import static org.junit.Assert.*;
/**
 * Created by richard on 11.3.2014.
 */
public class AgentManagerImplTest {

    private AgentManagerImpl manager;
    private DataSource dataSource;

    private DataSource prepareDataSource() throws SQLException {
        String jdbc_path = System.getenv("ISIS_JDBC_PATH");
        BasicDataSource dataSource = new BasicDataSource();
        //we will use in memory database
        dataSource.setUrl(jdbc_path);

        Connection conn = null;
        conn = dataSource.getConnection();
        AgentsTable.create(conn);
        DBUtils.closeQuietly(conn);
        return dataSource;
    }

    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
        dataSource = prepareDataSource();
        manager = new AgentManagerImpl();
        manager.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        Connection conn = null;
        conn = dataSource.getConnection();
        AgentsTable.drop(conn);
        DBUtils.closeQuietly(conn);
    }

    @Test
    public void testUpdateAgent() throws Exception {
        String new_name = "Jano Halak";
        Agent agent = newAgent("Jano Laco", "Medium killer", true);
        manager.addAgent(agent);

        agent.setName(new_name);
        manager.updateAgent(agent);
        Agent result = manager.findAgent(agent.getId());
        assertEquals(new_name, result.getName());
    }

    @Test
    public void testFindNonExistingAgent() throws Exception {
        assertNull(manager.findAgent(42l));
    }

    @Test
    public void testFindAgent() throws Exception {

        Agent agent = newAgent("Jano Laco", "Medium killer", true);
        Agent agent2 = newAgent("Sterling Archer", "Literally rank archer", true);
        manager.addAgent(agent);
        Long agentID = agent.getId();
        Agent result = manager.findAgent(agentID);
        assertEquals(agent, result);
        assertDeepEquals(agent, result);

    }

    @Test
    public void testFindAllAgents() throws Exception {
        Agent agent1 = newAgent("Jano Laco", "Medium killer", true);
        Agent agent2 = newAgent("Sterling Archer", "Literally rank archer", true);
        manager.addAgent(agent1);
        manager.addAgent(agent2);

        List<Agent> result = manager.findAllAgents();
        assertEquals(2, result.size());
    }

    @Test
    public void testRemoveAgent() throws Exception {

        Agent agent1 = newAgent("Jano", "Pro", false);
        Agent agent2 = newAgent("Laco", "Newbie", true);
        manager.addAgent(agent1);
        manager.addAgent(agent2);

        assertNotNull(manager.findAgent(agent1.getId()));
        assertNotNull(manager.findAgent(agent2.getId()));

        manager.removeAgent(agent2);
        assertNotNull(manager.findAgent(agent1.getId()));
        assertNull(manager.findAgent(agent2.getId()));

    }

    @Test
    public void testAddAgent() throws Exception {

        Agent agent = newAgent("Jano Laco", "ExtraSuperPro", true);
        manager.addAgent(agent);
        Long agentID = agent.getId();
        assertNotNull(agentID);

        Agent result = manager.findAgent(agentID);
        assertEquals(agent, result);
        assertNotSame(agent, result);
        assertDeepEquals(agent, result);

    }

    static Agent newAgent(String name, String rank, boolean secret) {

        Agent tmp = new Agent();
        tmp.setName(name);
        tmp.setRank(rank);
        tmp.setSecret(secret);

        return tmp;

    }

    static void assertDeepEquals(Agent expected, Agent actual) {

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getRank(), actual.getRank());
        assertEquals(expected.isSecret(), actual.isSecret());

    }
}
