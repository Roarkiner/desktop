import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import Enum.SexEnum;
import Model.AthleteModel;
import Repository.AthleteRepository;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Connection {
    private static Logger logger = LoggerFactory.getLogger(Connection.class);
    public static void main(String[] args) {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://application-desktop:6z7Z2i6K0sqYxG71@cluster0.aywre9k.mongodb.net/?retryWrites=true&w=majority");
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();

        try(MongoClient mongoClient = MongoClients.create(clientSettings)) {
            MongoDatabase desktopDatabase = mongoClient.getDatabase("desktop");
            AthleteRepository athleteRepository = new AthleteRepository(desktopDatabase.getCollection("athletes", AthleteModel.class));
            
            AthleteModel athleteToSave = new AthleteModel(
                "LOCTIN", 
                "Jeffrey", 
                new Date(), 
                SexEnum.UNDEFINED
            );
            athleteRepository.saveAthlete(athleteToSave);

            AthleteModel athleteModelReturned = athleteRepository.getAthlete(athleteToSave.getId());
            if(athleteModelReturned != null) {
                logger.debug(athleteModelReturned.getFirstName() + " " + athleteModelReturned.getLastName());
            } else {
                logger.error("Athlete not found");
            }
        }
    }
}