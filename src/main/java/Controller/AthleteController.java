package Controller;

import java.util.List;

import org.bson.types.ObjectId;

import Model.AthleteModel;
import Repository.AthleteRepository;

public class AthleteController {
    private AthleteRepository athleteRepository;

    public AthleteController(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }

    public AthleteModel getAthleteById(ObjectId athleteId) {
        return athleteRepository.getAthlete(athleteId);
    }

    public List<AthleteModel> getAllActivities() {
        return athleteRepository.getAllAthletes();
    }

    public ObjectId saveAthlete(AthleteModel athleteModel) {
        return athleteRepository.saveAthlete(athleteModel);
    }

    public void updateAthlete(AthleteModel athleteModel) {
        athleteRepository.updateAthlete(athleteModel);
    }

    public void deleteAthlete(ObjectId athleteId) {
        athleteRepository.deleteAthlete(athleteId);
    }
}
