import java.util.List;

/**
 * Created by richard on 25.2.2014.
 */
public interface AssigmentManager {
    boolean assignAgentToMission(Agent agent, Mission mission);
    boolean removeAgentFromMission(Agent agent, Mission mission);
    List<Agent> getMissionAgents(Mission mission);
    List<Mission> getAgentMissions(Agent agent);
}
