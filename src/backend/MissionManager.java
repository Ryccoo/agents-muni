package backend;

import backend.Mission;

import java.util.List;

/**
 * Created by richard on 22.2.2014.
 */
public interface MissionManager {
    Mission createMission(Mission mission);
    Mission removeMission(Mission mission);
    List<Mission> findAllMissions();
    Mission findMission(int id);
    boolean updateMission(Mission mission);
}
