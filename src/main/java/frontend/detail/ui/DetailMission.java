package frontend.detail.ui;

import backend.Agent;
import backend.Mission;
import frontend.base.ui.MainUI;
import frontend.models.AgentsTableDataModel;
import frontend.models.MissionsTableDataModel;
import frontend.workers.AgentUpdateWorker;
import frontend.workers.MissionDeleteWorker;
import frontend.workers.MissionUpdateWorker;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class DetailMission extends JDialog {
    private JPanel contentPane;
    private JTable MissionDetailTable;
    private JTextField mission_name;
    private JTextField mission_destination;
    private JCheckBox mission_secret;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton edit_button;
    private JTextArea mission_description;

    private Long missionID;
    private Mission mission;
    private List<Agent> agents;
    private boolean editing;

    public DetailMission(Long id) {
        this.missionID = id;
        this.mission = MainUI.missionManager.findMission(id);
        this.agents = MainUI.assigmentManager.getMissionAgents(this.mission);

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
                mission_name.setEditable(true);
                mission_description.setEditable(true);
                mission_destination.setEditable(true);
                mission_secret.setEnabled(true);
                editing = true;
                prepareTableEditing();
            }
        });
    }

    private void prepareTableEditing() {
        AgentsTableDataModel model = new AgentsTableDataModel();
        model.detaiLEditing(mission);
        MissionDetailTable.setModel(model);
    }

    private void prepareTable() {
        AgentsTableDataModel model = new AgentsTableDataModel();
        model.isInDetail(true);
        MissionDetailTable.setModel(model);
        for(Agent agent : agents) {
            model.addAgent(agent);
        }
    }

    private void setDetail() {
        mission_name.setText(mission.getName());
        mission_description.setText(mission.getDescription());
        mission_destination.setText(mission.getDestination());
        mission_secret.setSelected(mission.isSecret());
    }

    private void onOK() {
        if(editing) {
            mission.setName(mission_name.getText());
            mission.setDescription(mission_description.getText());
            mission.setDestination(mission_destination.getText());
            mission.setSecret(mission_secret.isSelected());
            MissionUpdateWorker worker = new MissionUpdateWorker();
            worker.setMission_to_perform(mission);
            AgentsTableDataModel model = (AgentsTableDataModel)MissionDetailTable.getModel();
            List<Agent> new_agents = model.getMission_agents();
            worker.setMission_agents(new_agents);
            worker.execute();
        }
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }
}
