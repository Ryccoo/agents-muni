package test;

import backend.Agent;
import backend.AgentManagerImpl;
import java.util.List;
import db.AgentsTable;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

import static org.junit.Assert.*;
/**
 * Created by richard on 11.3.2014.
 */
public class AgentManagerImplTest {

    private AgentManagerImpl manager;
    private Connection conn;

    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
        String jdbc_path = System.getenv("ISIS_JDBC_PATH");
        conn = DriverManager.getConnection(jdbc_path);
        AgentsTable.create(conn);
        manager = new AgentManagerImpl(conn);
    }

    @After
    public void tearDown() throws SQLException {
        AgentsTable.drop(conn);
    }

    @Test
    public void testUpdateAgent() throws Exception {
        String new_name = "Jano Halak";
        Agent agent = newAgent("Jano Laco", "Medium killer", true);
        manager.addAgent(agent);

        agent.setName(new_name);
        Boolean ret = manager.updateAgent(agent);
        assertTrue("Agent was not updated correctly", ret);
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

    private Agent newAgent(String name, String rank, boolean secret) {

        Agent tmp = new Agent();
        tmp.setName(name);
        tmp.setRank(rank);
        tmp.setSecret(secret);

        return tmp;

    }

    private void assertDeepEquals(Agent expected, Agent actual) {

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getRank(), actual.getRank());
        assertEquals(expected.isSecret(), actual.isSecret());

    }
}
