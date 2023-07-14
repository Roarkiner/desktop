import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import Controller.ActivityController;
import Controller.AthleteController;
import Enum.SexEnum;
import Model.ActivityModel;
import Model.AthleteModel;
import Repository.ActivityRepository;
import Repository.AthleteRepository;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import exceptions.ActivityValidationException;
import exceptions.AthleteValidationException;

import java.util.Date;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Connection {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Connection.class);

    public static void main(String[] args) {
        Logger mongoLogger = (Logger) LoggerFactory.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARN);
        ConnectionString connectionString = new ConnectionString(
                "mongodb+srv://application-desktop:6z7Z2i6K0sqYxG71@cluster0.aywre9k.mongodb.net/?retryWrites=true&w=majority");
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
            MongoDatabase desktopDatabase = mongoClient.getDatabase("desktop");
            AthleteRepository athleteRepository = new AthleteRepository(
                    desktopDatabase.getCollection("athletes", AthleteModel.class));

            AthleteController athleteController = new AthleteController(athleteRepository);
            AthleteModel athleteToSave = new AthleteModel(
                    "LOCTIN",
                    "Jeffrey",
                    new Date(),
                    SexEnum.UNDEFINED);
            ObjectId savedAthleteId = athleteController.saveAthlete(athleteToSave);

            AthleteModel athleteModelReturned = athleteController.getAthleteById(savedAthleteId);
            if (athleteModelReturned != null) {
                String messageToLog = athleteModelReturned.getFirstName() + " " + athleteModelReturned.getLastName();
                logger.info("Created Athlete : " + messageToLog);
            } else {
                logger.error("Athlete not found");
            }

            AthleteModel athleteUpdateModel = athleteToSave;
            athleteUpdateModel.setId(savedAthleteId);
            athleteUpdateModel.setLastName("HOIZEY");
            athleteUpdateModel.setFirstName("Evan");
            athleteController.updateAthlete(athleteUpdateModel);

            AthleteModel updatedAthleteModelReturned = athleteController.getAthleteById(savedAthleteId);
            if (updatedAthleteModelReturned != null) {
                String messageToLog = updatedAthleteModelReturned.getFirstName() + " "
                        + updatedAthleteModelReturned.getLastName();
                logger.info("Updated Athlete : " + messageToLog);
            } else {
                logger.error("Athlete not found");
            }

            ActivityController activityController = new ActivityController(
                    new ActivityRepository(
                            desktopDatabase.getCollection("athletes", AthleteModel.class),
                            savedAthleteId));

            activityController.saveActivity(new ActivityModel("PushUp", 20d, new Date(), 2d));
            athleteController.deleteAthlete(savedAthleteId);
            AthleteModel deletedAthleteModelReturned = athleteController.getAthleteById(savedAthleteId);
            if (deletedAthleteModelReturned != null) {
                String messageToLog = deletedAthleteModelReturned.getFirstName() + " "
                        + deletedAthleteModelReturned.getLastName();
                logger.info("Updated Athlete : " + messageToLog);
            } else {
                logger.error("Athlete not found");
            }

        } catch (org.bson.BsonInvalidOperationException exception) {
            logger.error("Impossible to convert _id to ObjectId", exception);
        } catch (IllegalArgumentException exception) {
            logger.error("Database not found", exception);
        } catch (AthleteValidationException | ActivityValidationException exception) {
            logger.error(exception.getMessage());
        }  catch (Exception exception) {
            throw exception;
        }
    }
}