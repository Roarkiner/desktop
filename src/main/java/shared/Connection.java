package shared;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.AthleteModel;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.LoggerFactory;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Connection {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Connection.class);

    MongoCollection<AthleteModel> athleteCollection;

    public Connection() {
        ConnectionString connectionString = new ConnectionString(
                "mongodb+srv://application-desktop:6z7Z2i6K0sqYxG71@cluster0.aywre9k.mongodb.net/?retryWrites=true&w=majority");
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
    
        try(MongoClient mongoClient = MongoClients.create(clientSettings)) {
            MongoDatabase desktopDatabase = mongoClient.getDatabase("desktop");
            athleteCollection = desktopDatabase.getCollection("athletes", AthleteModel.class);
        } catch (Exception e) {
            logger.error("Database not found", e);
        }
    }

}