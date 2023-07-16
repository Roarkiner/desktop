package controller;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import model.AthleteModel;
import repository.AthleteRepository;
import exceptions.AthleteValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AthleteController {
    private AthleteRepository athleteRepository;

    public AthleteController(AthleteRepository athleteRepository) {
        this.athleteRepository = athleteRepository;
    }


    public AthleteModel getAthleteById(ObjectId athleteId) {
        return athleteRepository.getAthlete(athleteId);
    }

    public List<AthleteModel> getAllAthletes() {
        return athleteRepository.getAllAthletes();
    }

    public ObjectId saveAthlete(AthleteModel athleteModel) throws AthleteValidationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AthleteModel>> validationErrors = validator.validate(athleteModel);
        if (!validationErrors.isEmpty()) {
            throw new AthleteValidationException("There's validation exceptions", validationErrors);
        }

        return athleteRepository.saveAthlete(athleteModel);
    }

    public void updateAthlete(AthleteModel athleteModel) throws AthleteValidationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AthleteModel>> validationErrors = validator.validate(athleteModel);
        if (!validationErrors.isEmpty()) {
            throw new AthleteValidationException("There's validation exceptions", validationErrors);
        }

        athleteRepository.updateAthlete(athleteModel);
    }

    public void deleteAthlete(ObjectId athleteId) throws IllegalArgumentException {
        long deletedAthlete = athleteRepository.deleteAthlete(athleteId);
        if(deletedAthlete == 0) {
            throw new IllegalArgumentException("No athlete matches the id : " + athleteId);
        }
    }
}
