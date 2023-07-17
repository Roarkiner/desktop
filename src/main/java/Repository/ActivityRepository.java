package repository;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import model.ActivityModel;
import model.AthleteModel;

public class ActivityRepository {

    private MongoCollection<AthleteModel> athleteCollection;
    private AthleteRepository athleteRepository;
    private ObjectId athleteId;

    public ActivityRepository(MongoCollection<AthleteModel> athleteCollection, ObjectId athleteId) {
        this.athleteCollection = athleteCollection;
        this.athleteRepository = new AthleteRepository(athleteCollection);
        this.athleteId = athleteId;
    }

    public ActivityModel getActivity(ObjectId activityId) {
        AthleteModel athlete = athleteRepository.getAthlete(athleteId);
        if (athlete != null) {
            return athlete.getActivities().stream()
                    .filter(activity -> activity.getActivityId().equals(activityId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public List<ActivityModel> getAllActivities() {
        AthleteModel athlete = athleteRepository.getAthlete(athleteId);
        if (athlete != null) {
            return athlete.getActivities();
        }

        return new ArrayList<>();
    }

    public ObjectId saveActivity(ActivityModel activityModel) {
        ObjectId activityId = new ObjectId();
        activityModel.setActivityId(activityId);
        athleteCollection.updateOne(Filters.eq("_id", athleteId),
                new Document("$push", new Document("activities", activityModel)));
        return activityId;
    }

    public void updateActivity(ActivityModel activityModel) {
        Document filter = new Document("_id", athleteId)
                .append("activities",
                        new Document("$elemMatch", new Document("activityId", activityModel.getActivityId())));

        Document update = new Document("$set",
                new Document("activities.$.name", activityModel.getName())
                        .append("activities.$.duration", activityModel.getDuration())
                        .append("activities.$.date", activityModel.getDate())
                        .append("activities.$.rpe", activityModel.getRpe()));

        athleteCollection.updateOne(filter, update);
    }

    public void deleteActivity(ObjectId activityId) {
        athleteCollection.updateOne(Filters.eq("_id", athleteId),
                new Document("$pull", new Document("activities", new Document("_id", activityId))));
    }
}