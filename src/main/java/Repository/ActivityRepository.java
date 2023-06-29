package Repository;

import java.util.ArrayList;
import java.util.List;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import Model.ActivityModel;

import static com.mongodb.client.model.Filters.eq;

public class ActivityRepository {

    private MongoCollection<ActivityModel> activityCollection;

    public ActivityRepository(MongoCollection<ActivityModel> activityCollection){
        this.activityCollection = activityCollection;
    }

    public ActivityModel getActivity(ObjectId activityId){
        return activityCollection.find(eq("_id", activityId)).first();
    }

    public List<ActivityModel> getAllActivities(){
        List<ActivityModel> activityList = new ArrayList<>();
        activityCollection.find()
            .into(activityList);
        return activityList;
    }

    public BsonValue saveActivity(ActivityModel activityModel){
        InsertOneResult insertResult = activityCollection.insertOne(activityModel);
        return insertResult.getInsertedId();
    }

    public ActivityModel updateActivity(ActivityModel activityModel){
        Bson filterById = eq("_id", activityModel.getId());
        FindOneAndReplaceOptions findOneAndReplaceOptions = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        return activityCollection.findOneAndReplace(filterById, activityModel, findOneAndReplaceOptions);
    }

    public long deleteActivity(ObjectId activityId){
        DeleteResult deleteResult = activityCollection.deleteOne(eq("_id", activityId));
        return deleteResult.getDeletedCount();
    }
}