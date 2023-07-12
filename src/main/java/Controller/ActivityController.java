package Controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;

import Model.ActivityModel;
import Repository.ActivityRepository;
import exceptions.NotEnoughActivitiesException;

public class ActivityController {
    private ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public ActivityModel getActivityById(ObjectId activityId) {
        return activityRepository.getActivity(activityId);
    }

    public List<ActivityModel> getAllActivities() {
        return activityRepository.getAllActivities();
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

    public ObjectId saveActivity(ActivityModel activityModel) {
        double load = calculateLoad(activityModel.getDuration(), activityModel.getRpe());
        activityModel.setLoad(load);
        return activityRepository.saveActivity(activityModel);
    }

    public void updateActivity(ActivityModel activityModel) {
        double load = calculateLoad(activityModel.getDuration(), activityModel.getRpe());
        activityModel.setLoad(load);
        activityRepository.updateActivity(activityModel);
    }

    public void deleteActivity(ObjectId activityId) {
        activityRepository.deleteActivity(activityId);
    }

    private double calculateLoad(double duration, double rpe) {
        return duration * rpe;
    }

    public double calculateSumOfLoadsOfActivities(List<ActivityModel> activities) {
        double sumOfLoads = 0d;
        for (ActivityModel activity : activities) {
            sumOfLoads += activity.getLoad();
        }
        return sumOfLoads;
    }

    public List<ActivityModel> getActivitiesFromPastWeeks(List<ActivityModel> activities, int numWeeksAgo)
            throws NotEnoughActivitiesException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -numWeeksAgo);

        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        Date endDate = calendar.getTime();

        boolean hasOlderActivity = activities.stream()
                .anyMatch(activity -> activity.getDate().before(startDate));

        if (!hasOlderActivity) {
            throw new NotEnoughActivitiesException("No activity older than the start date found.");
        }

        return getActivitiesBetweenDates(activities, startDate, endDate);
    }
}
