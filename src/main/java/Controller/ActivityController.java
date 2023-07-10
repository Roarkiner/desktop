package Controller;

import java.util.List;

import org.bson.types.ObjectId;

import Model.ActivityModel;
import Repository.ActivityRepository;

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

    public ObjectId saveActivity(ActivityModel activityModel) {
        Double load = calculateLoad(activityModel.getDuration(), activityModel.getRpe());
        activityModel.setLoad(load);
        return activityRepository.saveActivity(activityModel);
    }

    public void updateActivity(ActivityModel activityModel) {
        Double load = calculateLoad(activityModel.getDuration(), activityModel.getRpe());
        activityModel.setLoad(load);
        activityRepository.updateActivity(activityModel);
    }

    public void deleteActivity(ObjectId activityId) {
        activityRepository.deleteActivity(activityId);
    }

    private Double calculateLoad(Double duration, Double rpe) {
        return duration * rpe;
    }
}
