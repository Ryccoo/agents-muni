package frontend.workers;

import backend.Agent;
import backend.Mission;
import frontend.base.ui.MainUI;

import javax.swing.*;
import java.util.List;

/**
 * Created by richard on 11.5.2014.
 */
public class MissionUpdateWorker extends SwingWorker<Mission, Void> {

    private Mission mission_to_perform;
    private List<Agent> mission_agents;

    public void setMission_to_perform(Mission mission_to_perform) {
        this.mission_to_perform = mission_to_perform;
    }

    public void setMission_agents(List<Agent> mission_agents) {
        this.mission_agents = mission_agents;
    }

    @Override
    protected Mission doInBackground() throws Exception {
        MainUI.missionManager.updateMission(mission_to_perform);
        // remove all agents from this mission
        MainUI.assigmentManager.removeAllMissionAgents(mission_to_perform);
        // add selected agents
        for(Agent agent : mission_agents) {
            MainUI.assigmentManager.assignAgentToMission(agent, mission_to_perform);
        }

        return mission_to_perform;
    }

    @Override
    protected void done() {
        new MissionTableRefreshWorker().execute();
    }
}