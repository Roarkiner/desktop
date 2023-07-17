package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;

import model.ActivityModel;
import repository.ActivityRepository;
import exceptions.ActivityValidationException;
import exceptions.NotEnoughActivitiesException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ActivityController {
    private ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityModel getActivityById(ObjectId activityId) {
        return activityRepository.getActivity(activityId);
    }

    public List<ActivityModel> getAllActivities() {
        List<ActivityModel> activities = activityRepository.getAllActivities();
        activities.sort(Comparator.comparing(ActivityModel::getDate));
        return activities;
    }

    public ObjectId saveActivity(ActivityModel activityModel) throws ActivityValidationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ActivityModel>> validationErrors = validator.validate(activityModel);
        if (!validationErrors.isEmpty()) {
            throw new ActivityValidationException("There's validation exceptions", validationErrors);
        }

        double load = calculateLoad(activityModel.getDuration(), activityModel.getRpe());
        activityModel.setLoad(load);

        return activityRepository.saveActivity(activityModel);
    }

    public void updateActivity(ActivityModel activityModel) throws ActivityValidationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ActivityModel>> validationErrors = validator.validate(activityModel);
        if (!validationErrors.isEmpty()) {
            throw new ActivityValidationException("There's validation exceptions", validationErrors);
        }
        
        double load = calculateLoad(activityModel.getDuration(), activityModel.getRpe());
        activityModel.setLoad(load);
        
        activityRepository.updateActivity(activityModel);
    }

    public void deleteActivity(ObjectId activityId) throws IllegalArgumentException {
        List<ActivityModel> activitiesBeforeDeletion = getAllActivities();
        int numberOfActivitiesBeforeDeletion = activitiesBeforeDeletion.size();
        activityRepository.deleteActivity(activityId);
        List<ActivityModel> activitiesAfterDeletion = getAllActivities();
        int numberOfActivitiesAfterDeletion = activitiesAfterDeletion.size();
        if (numberOfActivitiesBeforeDeletion == numberOfActivitiesAfterDeletion) {
            throw new IllegalArgumentException("No activity matches the id : " + activityId);
        }
    }
    
    public List<ActivityModel> getActivitiesBetweenDates(List<ActivityModel> activities, Date startDate, Date endDate) {
        List<ActivityModel> activitiesBetweenDates = new ArrayList<>();
        for (ActivityModel activity : activities) {
            Date activityDate = activity.getDate();
            if (activityDate != null && activityDate.compareTo(startDate) >= 0
                    && activityDate.compareTo(endDate) <= 0) {
                activitiesBetweenDates.add(activity);
            }
        }
        activitiesBetweenDates.sort(Comparator.comparing(ActivityModel::getDate));
        return activitiesBetweenDates;
    }
    
    public List<List<ActivityModel>> getActivitiesFromPastWeeksPerDay(List<ActivityModel> activities, int numWeeksAgo)
            throws NotEnoughActivitiesException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -numWeeksAgo);

        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        Date firstWeekSunday = calendar.getTime();

        List<ActivityModel> activitiesOfFirstWeek = getActivitiesBetweenDates(activities, startDate, firstWeekSunday);

        boolean hasOlderActivity = activities.stream()
                .anyMatch(activity -> activity.getDate().before(startDate));

        if (!hasOlderActivity && activitiesOfFirstWeek.isEmpty()) {
            throw new NotEnoughActivitiesException("Not enough activities for : " + numWeeksAgo + " weeks ago.");
        }
        
        if (numWeeksAgo == 1) {
            return groupActivitiesByDay(activitiesOfFirstWeek);
        }

        calendar.add(Calendar.WEEK_OF_YEAR, numWeeksAgo - 1);
        Date endDate = calendar.getTime();

        List<ActivityModel> activitiesBetweenDates = getActivitiesBetweenDates(activities, startDate, endDate);

        return groupActivitiesByDay(activitiesBetweenDates);
    }

    public List<List<ActivityModel>> groupActivitiesByDay(List<ActivityModel> activities) {
        Map<LocalDate, List<ActivityModel>> activityMap = new HashMap<>();

        for (ActivityModel activity : activities) {
            Date date = activity.getDate();
            LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
            List<ActivityModel> dayActivities = activityMap.getOrDefault(localDate, new ArrayList<>());
            dayActivities.add(activity);
            activityMap.put(localDate, dayActivities);
        }

        for (List<ActivityModel> dayActivities : activityMap.values()) {
            dayActivities.sort(Comparator.comparing(ActivityModel::getDate));
        }

        List<List<ActivityModel>> sortedActivities = new ArrayList<>(activityMap.values());
        sortedActivities.sort(Comparator.comparing(o -> o.get(0).getDate()));

        return sortedActivities;
    }

    public double calculateLoad(double duration, double rpe) {
        return duration * rpe;
    }

    public double calculateSumOfLoadsOfActivities(List<List<ActivityModel>> activitiesPerDays) {
        double sumOfLoads = 0d;
        for (List<ActivityModel> activitiesPerDay : activitiesPerDays) {
            for (ActivityModel activity : activitiesPerDay) {
                sumOfLoads += activity.getLoad();
            }
        }
        return sumOfLoads;
    }
}
