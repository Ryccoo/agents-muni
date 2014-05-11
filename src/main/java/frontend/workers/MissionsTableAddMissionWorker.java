package frontend.workers;

import backend.Mission;
import frontend.base.ui.MainUI;

import javax.swing.*;

/**
 * Created by richard on 11.5.2014.
 */
public class MissionsTableAddMissionWorker extends SwingWorker<Mission, Void> {

    private Mission mission_to_perform;

    public void setMission_to_perform(Mission mission_to_perform) {
        this.mission_to_perform = mission_to_perform;
    }

    @Override
    protected Mission doInBackground() throws Exception {
        MainUI.missionManager.createMission(mission_to_perform);

        return mission_to_perform;
    }

    @Override
    protected void done() {
        new MissionTableRefreshWorker().execute();
    }
}