package web;

import backend.AgentManagerImpl;
import backend.AssigmentManagerImpl;
import backend.MissionManagerImpl;
import config.IsisConfig;
import db.CreateTables;
import org.apache.commons.dbcp.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by richard on 22.4.2014.
 */
@WebListener
public class ServerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("aplikace inicializována");
        ServletContext servletContext = ev.getServletContext();
        BasicDataSource ds;
        ds = CreateTables.prepareDataSource(IsisConfig.getProperty("JdbcPath"));
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");

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
