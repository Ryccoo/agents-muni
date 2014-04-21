package web;

import backend.AgentManagerImpl;
import db.CreateTables;
import org.apache.commons.dbcp.BasicDataSource;
import sun.management.resources.agent;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
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
        String jdbc_path = System.getenv("Database");
        BasicDataSource dataSource = CreateTables.prepareDataSource(jdbc_path);
//        servletContext.setAttribute("agentManager");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("aplikace končí");
    }
}
