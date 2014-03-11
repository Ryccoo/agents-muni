package test;

import backend.Agent;
import backend.AgentManagerImpl;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by richard on 11.3.2014.
 */
public class AgentManagerImplTest {

    private AgentManagerImpl manager;

    @Before
    public void setUp() throws SQLException {
        manager = new AgentManagerImpl();
    }

    @Test
    public void testUpdateAgent() throws Exception {

    }

    @Test
    public void testFindAgent() throws Exception {

        assertNull(manager.findAgent(42l));
        Agent agent = newAgent("Jano Laco", "Medium killer", true);
        manager.addAgent(agent);
        Long agentID = agent.getId();
        Agent result = manager.findAgent(agentID);
        assertEquals(agent, result);
        assertDeepEquals(agent, result);

    }

    @Test
    public void testFindAllAgents() throws Exception {

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
