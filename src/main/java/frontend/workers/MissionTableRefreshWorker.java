package frontend.workers;

import backend.Mission;
import frontend.base.ui.MainUI;
import frontend.models.MissionsTableDataModel;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by richard on 11.5.2014.
 */
public class MissionTableRefreshWorker extends SwingWorker<List<Mission>, Void> {

    @Override
    protected List<Mission> doInBackground() throws Exception {
        List<Mission> missions = MainUI.missionManager.findAllMissions();

        return missions;
    }

    @Override
    protected void done() {
        try {
            List<Mission> missions = get();
            MissionsTableDataModel.getInstance().clerTable();
            for(Mission mission :  missions) {
                MissionsTableDataModel.getInstance().addMission(mission);
            }
            MainUI.mainFrame.getMissions_count_label().setText("Missions in system : " + missions.size());
            MainUI.mainFrame.getRefreshMissionsButton().setEnabled(true);
        } catch (ExecutionException ex) {
            System.out.println("Error");
        } catch (InterruptedException ex) {
            // K tomuto by v tomto případě nemělo nikdy dojít (viz níže)
            throw new RuntimeException("Operation interrupted (this should never happen)",ex);
        }
    }
}