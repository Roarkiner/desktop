package exceptions;

import java.util.Set;

import Model.ActivityModel;
import jakarta.validation.ConstraintViolation;

public class ActivityValidationException extends Exception {
    private Set<ConstraintViolation<ActivityModel>> validationErrors;

    public ActivityValidationException(String message, Set<ConstraintViolation<ActivityModel>> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public Set<ConstraintViolation<ActivityModel>> getValidationErrors() {
        return this.validationErrors;
    }
}