package frontend.detail.ui;

import backend.Agent;
import backend.Mission;
import db.AgentsTable;
import frontend.base.ui.MainUI;
import frontend.models.MissionsTableDataModel;
import frontend.workers.AgentUpdateWorker;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class DetailAgent extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable AgentDetailTable;
    private JTextField agent_name;
    private JTextField agent_rank;
    private JCheckBox agent_secret;
    private JButton edit_button;

    private Long agentID;
    private Agent agent;
    private List<Mission> missions;
    private boolean editing;

    public DetailAgent(Long id) {
        this.agentID = id;
        this.agent = MainUI.agentsManager.findAgent(agentID);
        this.missions = MainUI.assigmentManager.getAgentMissions(this.agent);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setDetail();
        prepareTable();
        editButton();
    }

    private void editButton() {
        edit_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agent_name.setEditable(true);
                agent_rank.setEditable(true);
                agent_secret.setEnabled(true);
                editing = true;
                prepareTableEditing();
            }
        });
    }

    private void prepareTableEditing() {
        MissionsTableDataModel model = new MissionsTableDataModel();
        model.detaiLEditing(agent);
        AgentDetailTable.setModel(model);
    }

    private void prepareTable() {
        MissionsTableDataModel model = new MissionsTableDataModel();
        model.isInDetail(true);
        AgentDetailTable.setModel(model);
        for(Mission mission : missions) {
            model.addMission(mission);
        }
    }

    private void setDetail() {
        agent_name.setText(agent.getName());
        agent_rank.setText(agent.getRank());
        agent_secret.setSelected(agent.isSecret());
    }

    private void onOK() {
        if(editing) {
            agent.setName(agent_name.getText());
            agent.setRank(agent_rank.getText());
            agent.setSecret(agent_secret.isSelected());
            AgentUpdateWorker worker = new AgentUpdateWorker();
            worker.setAgentToPerform(agent);
            MissionsTableDataModel model = (MissionsTableDataModel)AgentDetailTable.getModel();
            List<Mission> new_missions = model.getAgent_missions();
            worker.setAgent_missions(new_missions);
            worker.execute();
        }
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
