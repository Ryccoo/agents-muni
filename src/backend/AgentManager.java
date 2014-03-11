package backend;

import backend.Agent;

import java.util.List;

/**
 * Created by richard on 22.2.2014.
 */
public interface AgentManager {
    Agent addAgent(Agent agent);
    Agent removeAgent(Agent agent);
    List<Agent> findAllAgents();
    Agent findAgent(Long id);
    boolean updateAgent(Agent agent);
}
