package frontend.workers;

import backend.Mission;
import frontend.base.ui.MainUI;

import javax.swing.*;

/**
 * Created by richard on 11.5.2014.
 */
public class MissionDeleteWorker extends SwingWorker<Mission, Void> {

    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    protected Mission doInBackground() throws Exception {
        Mission mission = MainUI.missionManager.findMission(id);
        if(mission != null) {
            // remove all agents from this mission
            MainUI.assigmentManager.removeAllMissionAgents(mission);
            MainUI.missionManager.removeMission(mission);
        }

        return mission;
    }

    @Override
    protected void done() {
        new MissionTableRefreshWorker().execute();
    }
}