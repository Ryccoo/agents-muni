package frontend.models;

import backend.Agent;
import backend.Mission;
import com.sun.org.apache.xpath.internal.operations.Bool;
import frontend.base.ui.MainUI;
import sun.management.resources.agent;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by richard on 9.5.2014.
 */
public class MissionsTableDataModel extends AbstractTableModel {

    private static MissionsTableDataModel instance = null;
    private static Object lock = new Object();
    private ArrayList<Mission> missions = new ArrayList<Mission>();
    private boolean inDetail = false;

    private boolean inDetailEdit = false;
    private Agent detailAgent;
    private ArrayList<Mission> agent_missions = new ArrayList<Mission>();

    public static MissionsTableDataModel getInstance() {
        if(instance==null) {
            synchronized(lock) {
                if(instance==null) {
                    instance = new MissionsTableDataModel();
                }
            }
        }
        return instance;
    }

    public void isInDetail(boolean inDetail) {
        this.inDetail = inDetail;
    }

    public void detaiLEditing(Agent agent) {
        this.inDetailEdit = true;
        this.detailAgent = agent;
        this.agent_missions = (ArrayList<Mission>) MainUI.assigmentManager.getAgentMissions(agent);
        this.missions = (ArrayList<Mission>) MainUI.missionManager.findAllMissions();
    }

    @Override
    public int getRowCount() {
        return missions.size();
    }

    @Override
    public int getColumnCount() {
        if(inDetail) {
            return 5;
        }
        if(inDetailEdit) {
            return 6;
        }
        return 7;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex>=missions.size()) {
            throw new IllegalArgumentException("Invalid row index.");
        }
        Mission mission = missions.get(rowIndex);
        if(inDetailEdit) {
            switch(columnIndex) {
                case 0: return agent_missions.contains(mission);
                case 1: return mission.getId();
                case 2: return mission.getName();
                case 3: return mission.getDestination();
                case 4: return mission.getDescription();
                case 5: return mission.isSecret();
            }
        }
        switch(columnIndex) {
            case 0: return mission.getId();
            case 1: return mission.getName();
            case 2: return mission.getDestination();
            case 3: return mission.getDescription();
            case 4: return mission.isSecret();
            case 5: return "Detail";
            case 6: return "Delete";
        }
        throw new IllegalArgumentException("Invalid column index.");
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(inDetailEdit) {
            switch(columnIndex) {
                case 0:return Boolean.class;
                case 1:return Long.class;
                case 2:return String.class;
                case 3:return String.class;
                case 4:return String.class;
                case 5:return Boolean.class;
                default: throw new IllegalArgumentException("Invalid column index.");
            }
        }
        switch(columnIndex) {
            case 0:return Long.class;
            case 1:return String.class;
            case 2:return String.class;
            case 3:return String.class;
            case 4:return Boolean.class;
            case 5:return Button.class;
            case 6:return JButton.class;
            default: throw new IllegalArgumentException("Invalid column index.");
        }
    }

    @Override
    public String getColumnName(int column) {
        if(inDetailEdit) {
            switch (column) {
                case 0: return "Assigned";
                case 1: return "Id";
                case 2: return "Name";
                case 3: return "Destination";
                case 4: return "Description";
                case 5: return "Secret";
                default: throw new IllegalArgumentException("column index");
            }
        }
        switch (column) {
            case 0: return "Id";
            case 1: return "Name";
            case 2: return "Destination";
            case 3: return "Description";
            case 4: return "Secret";
            case 5: return "Detail";
            case 6: return "Delete";
            default: throw new IllegalArgumentException("column index");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex==5 || columnIndex==6) {
            return true;
        }
        if(inDetailEdit && columnIndex==0) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(aValue instanceof Boolean) {
            if(columnIndex==0) {
                if(((Boolean) aValue).booleanValue()) {
                    agent_missions.add(missions.get(rowIndex));
                } else {
                    agent_missions.remove(missions.get(rowIndex));
                }
            }
        }
    }

    public void addMission(Mission mission) {
        missions.add(mission);
        int lastRow = missions.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void clerTable() {
        missions.clear();
    }

    public ArrayList<Mission> getAgent_missions() {
        return agent_missions;
    }
}