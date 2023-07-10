package Repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import Model.AthleteModel;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class AthleteRepository {

    private MongoCollection<AthleteModel> athleteCollection;

    public AthleteRepository(MongoCollection<AthleteModel> athleteCollection) {
        this.athleteCollection = athleteCollection;
    }

    public AthleteModel getAthlete(ObjectId athleteId) {
        return athleteCollection.find(eq("_id", athleteId)).first();
    }

    public List<AthleteModel> getAllAthletes() {
        List<AthleteModel> athleteList = new ArrayList<>();
        athleteCollection.find()
                .into(athleteList);
        return athleteList;
    }

    public ObjectId saveAthlete(AthleteModel athleteModel) {
        InsertOneResult insertResult = athleteCollection.insertOne(athleteModel);
        return insertResult.getInsertedId().asObjectId().getValue();
    }

    public AthleteModel updateAthlete(AthleteModel athleteModel) {
        Bson filterById = eq("_id", athleteModel.getId());
        FindOneAndReplaceOptions findOneAndReplaceOptions = new FindOneAndReplaceOptions()
                .returnDocument(ReturnDocument.AFTER);
        return athleteCollection.findOneAndReplace(filterById, athleteModel, findOneAndReplaceOptions);
    }

    public long deleteAthlete(ObjectId athleteId) {
        DeleteResult deleteResult = athleteCollection.deleteOne(eq("_id", athleteId));
        return deleteResult.getDeletedCount();
    }
}
