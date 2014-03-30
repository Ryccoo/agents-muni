package backend;

import backend.Agent;

import java.util.List;

/**
 * Created by richard on 22.2.2014.
 */
public interface AgentManager {
    void addAgent(Agent agent);
    void removeAgent(Agent agent);
    List<Agent> findAllAgents();
    Agent findAgent(Long id);
    void updateAgent(Agent agent);
}
