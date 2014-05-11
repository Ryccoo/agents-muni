package frontend.base.ui;

import backend.*;
import db.CreateTables;
import frontend.detail.ui.DetailAgent;
import frontend.detail.ui.DetailMission;
import frontend.dialogs.ui.AgentDialog;
import frontend.dialogs.ui.MissionDialog;
import frontend.models.AgentsTableDataModel;
import frontend.models.MissionsTableDataModel;
import frontend.workers.*;
import org.apache.commons.dbcp.BasicDataSource;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by richard on 28.4.2014.
 */
public class MainUI {
    public static AgentManagerImpl agentsManager = new AgentManagerImpl();
    public static MissionManagerImpl missionManager = new MissionManagerImpl();
    public static AssigmentManagerImpl assigmentManager = new AssigmentManagerImpl();
    public static MainUI mainFrame;

    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JButton addAgentButton;
    private JButton refreshAgentsButton;
    private JTable agentsTable;
    private JButton addMissionButton;
    private JButton refreshMissionsButton;
    private JLabel agents_count_label;
    private JLabel missions_count_label;
    private JTable missionsTable;

    public MainUI() {
        agentsTable.setModel(AgentsTableDataModel.getInstance());
        missionsTable.setModel(MissionsTableDataModel.getInstance());

        refreshAgentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AgentsTableRefreshWorker worker = new AgentsTableRefreshWorker();
                worker.execute();
                refreshAgentsButton.setEnabled(false);
            }
        });

        refreshMissionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MissionTableRefreshWorker worker = new MissionTableRefreshWorker();
                worker.execute();
                refreshMissionsButton.setEnabled(false);
            }
        });

        agentsTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        agentsTable.getColumn("Delete").setCellEditor(
                new ButtonEditor(new JCheckBox("Delete"), "agent"));
        agentsTable.getColumn("Detail").setCellRenderer(new ButtonRenderer());
        agentsTable.getColumn("Detail").setCellEditor(
                new ButtonEditor(new JCheckBox("Detail"), "agent"));

        missionsTable.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        missionsTable.getColumn("Delete").setCellEditor(
                new ButtonEditor(new JCheckBox("Delete"), "mission"));
        missionsTable.getColumn("Detail").setCellRenderer(new ButtonRenderer());
        missionsTable.getColumn("Detail").setCellEditor(
                new ButtonEditor(new JCheckBox("Detail"), "mission"));


        addAgentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AgentDialog adialog = new AgentDialog();
                adialog.pack();
                adialog.setModal(true);
                adialog.setVisible(true);
                Agent newAgent = adialog.getAgent();
                if (newAgent != null) {
                    AgentsTableAddAgentWorker worker = new AgentsTableAddAgentWorker();
                    worker.setAgentToPerform(newAgent);
                    worker.execute();
                }
            }
        });

        addMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MissionDialog mdialog = new MissionDialog();
                mdialog.pack();
                mdialog.setModal(true);
                mdialog.setVisible(true);
                Mission newMission = mdialog.getMission();
                if(newMission != null) {
                    MissionsTableAddMissionWorker worker = new MissionsTableAddMissionWorker();
                    worker.setMission_to_perform(newMission);
                    worker.execute();
                }
            }
        });
    }

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        mainFrame = new MainUI();

        // Prepare datasource
        BasicDataSource ds;
        ds = CreateTables.prepareDataSource(System.getenv("ISIS_JDBC_PATH"));
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        agentsManager.setDataSource(ds);
        assigmentManager.setDataSource(ds);
        missionManager.setDataSource(ds);


        JFrame frame = new JFrame("ISIS Agents");
        frame.setContentPane(mainFrame.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JButton getRefreshAgentsButton() {
        return refreshAgentsButton;
    }

    public JLabel getAgents_count_label() {
        return agents_count_label;
    }

    public JLabel getMissions_count_label() {
        return missions_count_label;
    }

    public JButton getRefreshMissionsButton() {
        return refreshMissionsButton;
    }

    public JPanel getPanel1() {
        return panel1;
    }
}

/**
 * @version 1.0 11/09/98
 */

class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

/**
 * @version 1.0 11/09/98
 */

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;

    private String label;
    private Long id;
    private String model;

    private boolean isPushed;

    public void setModel(String model) {
        this.model = model;
    }

    public ButtonEditor(JCheckBox checkBox, String model) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
        this.model = model;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        id = (Long) table.getValueAt(row, 0);
        button.setText(label);
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            if(label=="Detail") {
                if(model=="agent") {
                    DetailAgent detail = new DetailAgent(id);
                    detail.pack();
                    detail.setModal(true);
                    detail.setVisible(true);
                }
                if(model=="mission") {
                    DetailMission detail = new DetailMission(id);
                    detail.pack();
                    detail.setModal(true);
                    detail.setVisible(true);
                }
            }
            if(label=="Delete") {
                int dialogResult = JOptionPane.showConfirmDialog(MainUI.mainFrame.getPanel1(), "Are you sure ?", "Delete " + model + " ?", JOptionPane.YES_NO_OPTION);
                if(dialogResult==0) {
                    if(model=="agent") {
                        AgentDeleteWorker worker = new AgentDeleteWorker();
                        worker.setId(id);
                        worker.execute();
                    } else if(model=="mission") {
                        MissionDeleteWorker worker = new MissionDeleteWorker();
                        worker.setId(id);
                        worker.execute();
                    }
                }
            }
        }
        isPushed = false;
        return new String(label);
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
