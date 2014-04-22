package web;

import backend.Agent;
import backend.AgentManager;
import backend.AgentManagerImpl;
import db.AgentsTable;
import db.CreateTables;
import org.apache.commons.dbcp.BasicDataSource;
import utils.DBUtils;
import utils.ServiceFailureException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by richard on 22.4.2014.
 */
@WebServlet(AgentsServlet.URL_MAPPING + "/*")
public class AgentsServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/agents";

    public static final Logger log = Logger.getLogger(AgentManagerImpl.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showAgentList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //aby fungovala čestina z formuláře
        request.setCharacterEncoding("utf-8");
        //akce podle přípony v URL
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //načtení POST parametrů z formuláře
                String name = request.getParameter("name");
                String rank = request.getParameter("rank");
                boolean secret = (boolean) request.getParameter("secret")
                //kontrola vyplnění hodnot
                if (name == null || name.length() == 0 || rank == null || rank.length() == 0) {
                    request.setAttribute("chyba", "Je nutné vyplnit všechny hodnoty !");
                    showAgentList(request, response);
                    return;
                }
                //zpracování dat - vytvoření záznamu v databázi
                try {

                    Book book = new Book(null, name, author);
                    getAgentManager().createBook(book);
                    log.debug("created {}",book);
                    //redirect-after-POST je ochrana před vícenásobným odesláním formuláře
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (BookException e) {
                    log.error("Cannot add book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    getAgentManager().deleteBook(id);
                    log.debug("deleted book {}",id);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (BookException e) {
                    log.error("Cannot delete book", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //TODO
                return;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    private AgentManager getAgentManager() {
        return (AgentManager) getServletContext().getAttribute("agentManager");
    }

    private void showAgentList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("books", AgentManager().getAllBooks());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            String msg = "Cannot show agents";
            log.log(Level.SEVERE, msg, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}