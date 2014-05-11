package frontend.workers;

import backend.Agent;
import frontend.base.ui.MainUI;

import javax.swing.*;

/**
 * Created by richard on 11.5.2014.
 */
public class AgentDeleteWorker extends SwingWorker<Agent, Void> {

        private Long id;

        public void setId(Long id) {
            this.id = id;
        }

        @Override
        protected Agent doInBackground() throws Exception {
            Agent agent = MainUI.agentsManager.findAgent(id);
            if(agent != null) {
                // remove agent from all his missions
                MainUI.assigmentManager.removeAllAgentMissions(agent);
                MainUI.agentsManager.removeAgent(agent);
            }

            return agent;
        }

        @Override
        protected void done() {
            new AgentsTableRefreshWorker().execute();
        }
    }