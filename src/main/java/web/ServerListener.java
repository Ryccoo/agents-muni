package web;

import backend.AgentManagerImpl;
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
        BasicDataSource dataSource = CreateTables.prepareDataSource("jdbc:derby:/home/richard/IdeaProjects/db/Agents;create=true");
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            AgentsTable.create(conn);
            DBUtils.closeQuietly(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        servletContext.setAttribute("agentManager");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace končí");
    }
}
