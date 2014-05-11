package frontend.workers;

import backend.Agent;
import backend.Mission;
import frontend.base.ui.MainUI;

import javax.swing.*;
import java.util.List;

/**
 * Created by richard on 11.5.2014.
 */
public class AgentUpdateWorker extends SwingWorker<Agent, Void> {

    private Agent agent_to_perform;
    private List<Mission> agent_missions;

    public void setAgentToPerform(Agent agent_to_perform) {
        this.agent_to_perform = agent_to_perform;
    }

    public void setAgent_missions(List<Mission> agent_missions) {
        this.agent_missions = agent_missions;
    }

    @Override
    protected Agent doInBackground() throws Exception {
        MainUI.agentsManager.updateAgent(agent_to_perform);
        // remove agent from all his missions
        MainUI.assigmentManager.removeAllAgentMissions(agent_to_perform);
        // add selected missions
        for(Mission mission : agent_missions) {
            MainUI.assigmentManager.assignAgentToMission(agent_to_perform, mission);
        }

        return agent_to_perform;
    }

    @Override
    protected void done() {
        new AgentsTableRefreshWorker().execute();
    }
}