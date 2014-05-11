package frontend.dialogs.ui;

import backend.Agent;
import sun.management.resources.agent;

import javax.swing.*;
import java.awt.event.*;

public class AgentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel agent_dialog;
    private JTextField agent_rank;
    private JCheckBox secret;
    private JTextField agent_name;
    private JLabel name_required;
    private JLabel rank_required;

    private Agent agent = new Agent();

    public AgentDialog() {
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
    }

    private void onOK() {
        name_required.setText("");
        rank_required.setText("");
        boolean valid = true;
        if(agent_name.getText().isEmpty()) {
            name_required.setText("Name is required");
            valid = false;
        }
        if(agent_rank.getText().isEmpty()) {
            rank_required.setText("Rank is required");
            valid = false;
        }
        if(valid) {
            dispose();
        }
    }

    private void onCancel() {
        agent = null;
        dispose();
    }

    public Agent getAgent() {
        if(agent==null) {
            return null;
        }
        agent.setName(agent_name.getText());
        agent.setRank(agent_rank.getText());
        agent.setSecret(secret.isSelected());

        return agent;
    }
}
