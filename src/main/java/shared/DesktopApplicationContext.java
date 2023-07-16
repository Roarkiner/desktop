package shared;

import org.bson.types.ObjectId;

import controller.ActivityController;
import controller.AthleteController;
import controller.AthleteWorkoutInformationsController;
import repository.ActivityRepository;
import repository.AthleteRepository;

public class DesktopApplicationContext {
    private static DesktopApplicationContext instance;
    private Connection databaseConnection;
    private ObjectId connectedUserId;
    private AthleteController athleteController;
    private ActivityController activityController;
    private AthleteWorkoutInformationsController athleteWorkoutInformationsController;

    private DesktopApplicationContext() {
        this.databaseConnection = createDatabaseConnection();
        this.connectedUserId = null;
        this.athleteController = new AthleteController(new AthleteRepository(databaseConnection.athleteCollection));
    }

    public static synchronized DesktopApplicationContext getInstance() {
        if (instance == null) {
            instance = new DesktopApplicationContext();
        }
        return instance;
    }

    private Connection createDatabaseConnection() {
        return new Connection();
    }

    public Connection getDatabaseConnection() {
        return databaseConnection;
    }

    public ObjectId getConnectedUserId() {
        return connectedUserId;
    }

    public void connectNewUser(ObjectId userId) {
        setActivityController(new ActivityController(new ActivityRepository(databaseConnection.athleteCollection, userId)));
        setAthleteWorkoutInformationsController(new AthleteWorkoutInformationsController(activityController));
        this.connectedUserId = userId;
    }

    public AthleteController getAthleteController() {
        return athleteController;
    }

    public void setActivityController(ActivityController activityController) {
        this.activityController = activityController;
    }

    public ActivityController getActivityController() {
        return activityController;
    }

    public void setAthleteWorkoutInformationsController(AthleteWorkoutInformationsController athleteWorkoutInformationsController) {
        this.athleteWorkoutInformationsController = athleteWorkoutInformationsController;
    }

    public AthleteWorkoutInformationsController getAthleteWorkoutInformationsController() {
        return athleteWorkoutInformationsController;
    }
}