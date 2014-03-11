package backend;

import backend.Mission;

import java.util.List;

/**
 * Created by richard on 22.2.2014.
 */
public interface MissionManager {
    Mission createMission(Mission mission) throws UnsupportedOperationException;
    Mission removeMission(Mission mission) throws UnsupportedOperationException;
    List<Mission> findAllMissions() throws UnsupportedOperationException;
    Mission findMission(Long id) throws UnsupportedOperationException;
    boolean updateMission(Mission mission) throws UnsupportedOperationException;
}
