package web;

import backend.Agent;
import backend.AgentManager;
import backend.MissionManagerImpl;
import utils.ServiceFailureException;
import utils.ValidationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(AgentsServlet.URL_MAPPING + "/*")
public class AgentsServlet extends HttpServlet {

    private static final String LIST_JSP = "/agents_jsp/list.jsp";
    public static final String URL_MAPPING = "/agents";

    private static final Logger log = Logger.getLogger(
            MissionManagerImpl.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showMissionsList(request, response);
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
                Boolean secret = (request.getParameter("secret") != null);

                //zpracování dat - vytvoření záznamu v databázi
                try {
                    Agent agent = new Agent();
                    agent.setName(name);
                    agent.setRank(rank);
                    agent.setSecret(secret);
                    getAgentManager().addAgent(agent);

                    //redirect-after-POST je ochrana před vícenásobným odesláním formuláře
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ValidationException e) {
                    request.setAttribute("chyba", e.getMessage());
                    showMissionsList(request, response);
                    return;
                } catch (ServiceFailureException e) {
                    System.out.println("Cannot add agent: " + e.getStackTrace());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    Agent agent = getAgentManager().findAgent(id);
                    getAgentManager().removeAgent(agent);
                    System.out.println("deleted agent" + agent);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    System.out.println("Cannot delete agent: " + e.getStackTrace());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/update":
                //TODO
                return;
            default:
                System.out.println("Unknown action `" + action + "`");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
                return;
        }
    }

    private AgentManager getAgentManager() {
        return (AgentManager) getServletContext().getAttribute("agentManager");
    }

    private void showMissionsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("agents", getAgentManager().findAllAgents());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}