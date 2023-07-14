package exceptions;

import java.util.Set;

import Model.AthleteModel;
import jakarta.validation.ConstraintViolation;

public class AthleteValidationException extends Exception {
    private Set<ConstraintViolation<AthleteModel>> validationErrors;

    public AthleteValidationException(String message, Set<ConstraintViolation<AthleteModel>> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public Set<ConstraintViolation<AthleteModel>> getValidationErrors() {
        return this.validationErrors;
    }
}