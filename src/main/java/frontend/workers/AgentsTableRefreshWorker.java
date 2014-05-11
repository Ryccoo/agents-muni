package frontend.workers;

import backend.Agent;
import frontend.base.ui.MainUI;
import frontend.models.AgentsTableDataModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by richard on 6.5.2014.
 */
public class AgentsTableRefreshWorker extends SwingWorker<List<Agent>, Void> {

    @Override
    protected List<Agent> doInBackground() throws Exception {
        List<Agent> agents = MainUI.agentsManager.findAllAgents();

        return agents;
    }

    @Override
    protected void done() {
        try {
            List<Agent> agents = get();
            AgentsTableDataModel.getInstance().clerTable();
            for(Agent agent :  agents) {
                AgentsTableDataModel.getInstance().addAgent(agent);
            }
            MainUI.mainFrame.getAgents_count_label().setText("Agents in system : " + agents.size());
            MainUI.mainFrame.getRefreshAgentsButton().setEnabled(true);
        } catch (ExecutionException ex) {
            System.out.println("Error");
        } catch (InterruptedException ex) {
            // K tomuto by v tomto případě nemělo nikdy dojít (viz níže)
            throw new RuntimeException("Operation interrupted (this should never happen)",ex);
        }
    }
}