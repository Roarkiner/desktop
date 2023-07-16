package exceptions;

import java.util.Set;

import model.AthleteModel;
import jakarta.validation.ConstraintViolation;

public class AthleteValidationException extends Exception {
    private transient Set<ConstraintViolation<AthleteModel>> validationErrors;

    public AthleteValidationException(String message, Set<ConstraintViolation<AthleteModel>> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public Set<ConstraintViolation<AthleteModel>> getValidationErrors() {
        return this.validationErrors;
    }
}