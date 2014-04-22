package web;

import backend.Mission;
import backend.MissionManager;
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

@WebServlet(MissionsServlet.URL_MAPPING + "/*")
public class MissionsServlet extends HttpServlet {

    private static final String LIST_JSP = "/missions_jsp/list.jsp";
    public static final String URL_MAPPING = "/missions";

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
                String description = request.getParameter("description");
                String destination = request.getParameter("destination");
                Boolean secret = (request.getParameter("secret") != null);

                //zpracování dat - vytvoření záznamu v databázi
                try {
                    Mission mission = new Mission();
                    mission.setName(name);
                    mission.setDescription(description);
                    mission.setDestination(destination);
                    mission.setSecret(secret);
                    getMissionManager().createMission(mission);

                    //redirect-after-POST je ochrana před vícenásobným odesláním formuláře
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ValidationException e) {
                    request.setAttribute("chyba", e.getMessage());
                    showMissionsList(request, response);
                    return;
                } catch (ServiceFailureException e) {
                    System.out.println("Cannot add mission: " + e.getStackTrace());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    return;
                }
            case "/delete":
                try {
                    Long id = Long.valueOf(request.getParameter("id"));
                    Mission mission = getMissionManager().findMission(id);
                    getMissionManager().removeMission(mission);
                    System.out.println("deleted mission " + mission);
                    response.sendRedirect(request.getContextPath()+URL_MAPPING);
                    return;
                } catch (ServiceFailureException e) {
                    System.out.println("Cannot delete mission: " + e.getStackTrace());
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

    private MissionManager getMissionManager() {
        return (MissionManager ) getServletContext().getAttribute("missionManager");
    }

    private void showMissionsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("missions", getMissionManager().findAllMissions());
            request.getRequestDispatcher(LIST_JSP).forward(request, response);
        } catch (ServiceFailureException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}