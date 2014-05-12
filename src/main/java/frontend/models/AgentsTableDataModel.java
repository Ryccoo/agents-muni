package frontend.models;

import backend.Agent;
import backend.Mission;
import frontend.base.ui.MainUI;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by richard on 6.5.2014.
 */
public class AgentsTableDataModel extends AbstractTableModel {

    private static AgentsTableDataModel instance = null;
    private static Object lock = new Object();
    private ArrayList<Agent> agents = new ArrayList<Agent>();
    private boolean inDetail = false;

    private boolean inDetailEdit = false;
    private Mission detailMission;
    private ArrayList<Agent> mission_agents = new ArrayList<Agent>();

    public static AgentsTableDataModel getInstance() {
        if(instance==null) {
            synchronized(lock) {
                if(instance==null) {
                    instance = new AgentsTableDataModel();
                }
            }
        }
        return instance;
    }

    public void isInDetail(boolean inDetail) {
        this.inDetail = inDetail;
    }

    public void detaiLEditing(Mission mission) {
        this.inDetailEdit = true;
        this.detailMission = mission;
        this.mission_agents = (ArrayList<Agent>) MainUI.assigmentManager.getMissionAgents(mission);
        this.agents = (ArrayList<Agent>) MainUI.agentsManager.findAllAgents();
    }

    @Override
    public int getRowCount() {
        return agents.size();
    }

    @Override
    public int getColumnCount() {
        if(inDetail) {
            return 4;
        }
        if(inDetailEdit) {
            return 5;
        }
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex>=agents.size()) {
            throw new IllegalArgumentException("Invalid row index.");
        }
        Agent agent = agents.get(rowIndex);
        if(inDetailEdit) {
            switch(columnIndex) {
                case 0: return mission_agents.contains(agent);
                case 1: return agent.getId();
                case 2: return agent.getName();
                case 3: return agent.getRank();
                case 4: return agent.isSecret();
            }
        }
        switch(columnIndex) {
            case 0: return agent.getId();
            case 1: return agent.getName();
            case 2: return agent.getRank();
            case 3: return agent.isSecret();
            case 4: return MainUI.guiBundle.getString("Gui.Base.Details");
            case 5: return MainUI.guiBundle.getString("Gui.Base.Delete");
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
                    case 4:return Boolean.class;
                    default: throw new IllegalArgumentException("Invalid column index.");
                }
            }
            switch(columnIndex) {
                case 0:return Long.class;
                case 1:return String.class;
                case 2:return String.class;
                case 3:return Boolean.class;
                case 4:return Button.class;
                case 5:return JButton.class;
                default: throw new IllegalArgumentException("Invalid column index.");
            }
        }

    @Override
    public String getColumnName(int column) {
        if(inDetailEdit) {
            switch (column) {
                case 0: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Assigned.Label");
                case 1: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Id.Label");
                case 2: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Name.Label");
                case 3: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Rank.Label");
                case 4: return MainUI.agentsBundle.getString("Gui.Agents.Fields.IsSecret.Label");
                default: throw new IllegalArgumentException("column index");
            }
        }
        switch (column) {
            case 0: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Id.Label");
            case 1: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Name.Label");
            case 2: return MainUI.agentsBundle.getString("Gui.Agents.Fields.Rank.Label");
            case 3: return MainUI.agentsBundle.getString("Gui.Agents.Fields.IsSecret.Label");
            case 4: return MainUI.guiBundle.getString("Gui.Base.Details");
            case 5: return MainUI.guiBundle.getString("Gui.Base.Delete");
            default: throw new IllegalArgumentException("column index");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex==4 || columnIndex==5) {
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
                    mission_agents.add(agents.get(rowIndex));
                } else {
                    mission_agents.remove(agents.get(rowIndex));
                }
            }
        }
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
        int lastRow = agents.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void clerTable() {
        agents.clear();
    }

    public ArrayList<Agent> getMission_agents() {
        return mission_agents;
    }
}