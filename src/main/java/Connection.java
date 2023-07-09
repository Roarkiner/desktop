import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import Enum.SexEnum;
import Model.AthleteModel;
import Repository.AthleteRepository;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.util.Date;

import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.LoggerFactory;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Connection {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Connection.class);
    public static void main(String[] args) {
        Logger mongoLogger = (Logger) LoggerFactory.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARN);
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
            BsonValue savedAthleteId = athleteRepository.saveAthlete(athleteToSave);

            AthleteModel athleteModelReturned = athleteRepository.getAthlete(savedAthleteId.asObjectId().getValue());
            if(athleteModelReturned != null) {
                String messageToLog = athleteModelReturned.getFirstName() + " " + athleteModelReturned.getLastName();
                logger.info(messageToLog);
            } else {
                logger.error("Athlete not found");
            }

            athleteToSave.setLastName("HOIZEY");

            
        } catch(org.bson.BsonInvalidOperationException exception) {
            logger.error("Impossible to convert _id to ObjectId", exception);
        } catch(IllegalArgumentException exception) {
            logger.error("Database not found", exception);
        } catch(Exception exception) {
            throw exception;
        }
    }
}