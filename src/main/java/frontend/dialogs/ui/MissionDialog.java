package frontend.dialogs.ui;

import backend.Mission;
import frontend.base.ui.MainUI;

import javax.swing.*;
import java.awt.event.*;

public class MissionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel mission_dialog;
    private JCheckBox secret;
    private JTextField mission_name;
    private JLabel name_required;
    private JTextField mission_destinaion;
    private JLabel destination_required;
    private JTextArea mission_description;
    private JLabel description_required;

    private Mission mission = new Mission();

    public MissionDialog() {
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
        description_required.setText("");
        destination_required.setText("");
        boolean valid = true;
        if(mission_name.getText().isEmpty()) {
            name_required.setText(MainUI.missionsBundle.getString("Gui.Missions.Validations.Name.Required"));
            valid = false;
        }
        if(mission_description.getText().isEmpty()) {
            description_required.setText(MainUI.missionsBundle.getString("Gui.Missions.Validations.Description.Required"));
            valid = false;
        }
        if(mission_destinaion.getText().isEmpty()) {
            destination_required.setText(MainUI.missionsBundle.getString("Gui.Missions.Validations.Destination.Required"));
            valid = false;
        }
        if(valid) {
            dispose();
        } else {
            this.pack();
        }
    }

    private void onCancel() {
        mission = null;
        dispose();
    }

    public Mission getMission() {
        if(mission==null) {
            return null;
        }
        mission.setName(mission_name.getText());
        mission.setDestination(mission_destinaion.getText());
        mission.setDescription(mission_description.getText());
        mission.setSecret(secret.isSelected());

        return mission;
    }
}