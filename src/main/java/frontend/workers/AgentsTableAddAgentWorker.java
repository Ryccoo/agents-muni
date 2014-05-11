package frontend.workers;

import backend.Agent;
import frontend.base.ui.MainUI;
import frontend.models.AgentsTableDataModel;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by richard on 9.5.2014.
 */
public class AgentsTableAddAgentWorker extends SwingWorker<Agent, Void> {

    private Agent agent_to_perform;

    public void setAgentToPerform(Agent agent_to_perform) {
        this.agent_to_perform = agent_to_perform;
    }

    @Override
    protected Agent doInBackground() throws Exception {
        MainUI.agentsManager.addAgent(agent_to_perform);

        return agent_to_perform;
    }

    @Override
    protected void done() {
        new AgentsTableRefreshWorker().execute();
    }
}