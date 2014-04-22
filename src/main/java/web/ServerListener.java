package web;

import backend.AgentManagerImpl;
import backend.AssigmentManagerImpl;
import backend.MissionManager;
import backend.MissionManagerImpl;
import db.AgentsTable;
import db.CreateTables;
import org.apache.commons.dbcp.BasicDataSource;
import sun.management.resources.agent;
import utils.DBUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by richard on 22.4.2014.
 */
@WebListener
public class ServerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("aplikace inicializována");
        ServletContext servletContext = ev.getServletContext();
        AgentManagerImpl agentManager = new AgentManagerImpl();
        BasicDataSource ds;
        ds = CreateTables.prepareDataSource("jdbc:derby:/home/richard/IdeaProjects/db/Agents;create=true");
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn;
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        AgentManagerImpl agentm = new AgentManagerImpl();
        MissionManagerImpl mism = new MissionManagerImpl();
        AssigmentManagerImpl asigm = new AssigmentManagerImpl();
        agentm.setDataSource(ds);
        mism.setDataSource(ds);
        asigm.setDataSource(ds);
        servletContext.setAttribute("agentManager", agentm);
        servletContext.setAttribute("missionManager", mism);
        servletContext.setAttribute("assigmentManager", asigm);
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace končí");
    }
}
