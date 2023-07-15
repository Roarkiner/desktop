import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Controller.ActivityController;
import Controller.AthleteWorkoutInformationsController;
import Enum.PhysicalConditionEnum;
import Model.ActivityModel;
import Model.AthleteWorkoutInformationsModel;
import exceptions.NotEnoughActivitiesException;

class AthleteWorkoutInformationsControllerTest {

    @Mock
    private ActivityController activityController;

    private AthleteWorkoutInformationsController athleteWorkoutInformationsController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        athleteWorkoutInformationsController = new AthleteWorkoutInformationsController(activityController);
    }

    @Test
    void testCalculateMonotony() {
        // Arrange
        List<List<ActivityModel>> activitiesOfTheWeekPerDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        activitiesOfTheWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity 1", 60, calendar.getTime(), 8, 480)));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfTheWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity 2", 120, calendar.getTime(), 9, 1080)));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfTheWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity 3", 60, calendar.getTime(), 5, 600),
                        new ActivityModel("Activity 4", 120, calendar.getTime(), 6, 720)));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfTheWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity 5", 90, calendar.getTime(), 7, 630)));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfTheWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity 6", 60, calendar.getTime(), 10, 600),
                        new ActivityModel("Activity 7", 60, calendar.getTime(), 10, 600)));

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfTheWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity 8", 150, calendar.getTime(), 10, 1500)));

        double loadAverage = 844.285714;

        // Act
        double monotony = athleteWorkoutInformationsController.calculateMonotony(activitiesOfTheWeekPerDays,
                loadAverage, 7);

        // Assert
        double roundedMonotony = Math.round(monotony * 100.0) / 100.0;
        Assertions.assertEquals(1.57, roundedMonotony);
    }

    @Test
    void testCalculateLoadAverage() {
        // Arrange
        double sumOfLoads = 5910;
        int numberOfDays = 7;

        // Act
        double result = athleteWorkoutInformationsController.calculateLoadAverage(sumOfLoads, numberOfDays);

        // Assert
        Assertions.assertEquals(sumOfLoads / numberOfDays, result);
    }

    @Test
    void testCalculateConstraint() {
        // Arrange
        double sumOfLoads = 5910;
        double monotony = 1.57;

        // Act
        double result = athleteWorkoutInformationsController.calculateConstraint(sumOfLoads, monotony);

        // Assert
        Assertions.assertEquals(sumOfLoads * monotony, result);
    }

    @Test
    void testCalculateFitness() {
        // Arrange
        double sumOfLoads = 5910;
        double constraint = 9278.7;

        // Act
        double result = athleteWorkoutInformationsController.calculateFitness(sumOfLoads, constraint);

        // Assert
        Assertions.assertEquals(sumOfLoads - constraint, result);
    }

    @Test
    void testCalculateAWCR() {
        // Arrange
        double acuteLoad = 844.3;
        double chronicLoad = 900.48;

        // Act
        double result = athleteWorkoutInformationsController.calculateACWR(acuteLoad, chronicLoad);

        // Assert
        Assertions.assertEquals(acuteLoad / chronicLoad, result);
    }

    private static Stream<Arguments> provideDataForGetPhysicalCondition() {
        return Stream.of(
                Arguments.of(1.3, 5081, 1.0, PhysicalConditionEnum.OPTIMAL),
                Arguments.of(2.3, 5081, 1.0, PhysicalConditionEnum.EXHAUSTING),
                Arguments.of(1.3, 8000, 1.0, PhysicalConditionEnum.EXHAUSTING),
                Arguments.of(3.3, 5081, 1.0, PhysicalConditionEnum.DANGEROUS),
                Arguments.of(1.3, 15081, 1.0, PhysicalConditionEnum.DANGEROUS),
                Arguments.of(1.3, 5081, 1.7, PhysicalConditionEnum.DANGEROUS),
                Arguments.of(2.3, 8000, 1.7, PhysicalConditionEnum.DANGEROUS),
                Arguments.of(1.3, 5081, 1.4, PhysicalConditionEnum.RAS));
    }

    @ParameterizedTest
    @MethodSource("provideDataForGetPhysicalCondition")
    void testGetPhysicalCondition(double monotony, double constraint, double awcr,
            PhysicalConditionEnum physicalConditionExpected) {
        // Arrange
        AthleteWorkoutInformationsModel athleteWorkoutInformations = new AthleteWorkoutInformationsModel(0.0, monotony,
                constraint, 0.0, awcr);

        // Act
        PhysicalConditionEnum result = athleteWorkoutInformationsController
                .getPhysicalCondition(athleteWorkoutInformations);

        // Assert
        Assertions.assertEquals(physicalConditionExpected, result);
    }

    private static Stream<Arguments> provideDataForGetAthleteWorkoutInformations() {
        return Stream.of(
                Arguments.of(5910.0, 1.57, 9293.0, -3383.0, 1.0, false),
                Arguments.of(5910.0, 1.57, 9293.0, -3383.0, null, false),
                Arguments.of(5910.0, 1.57, 9293.0, -3383.0, null, true));
    }

    @ParameterizedTest
    @MethodSource("provideDataForGetAthleteWorkoutInformations")
    void testGetAthleteWorkoutInformations(double expectedTotalLoad, double expectedMonotony, double expectedConstraint,
            double expectedFitness, Double expectedAwcr, boolean shouldThrow) throws NotEnoughActivitiesException {
        // Arrange
        //#region
        Calendar calendar = Calendar.getInstance();
        List<ActivityModel> activities = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -4);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 8, 480));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 5, 600));
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 6, 720));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 90, calendar.getTime(), 7, 630));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 8, 480));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 5, 600));
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 6, 720));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 90, calendar.getTime(), 7, 630));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 8, 480));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 5, 600));
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 6, 720));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 90, calendar.getTime(), 7, 630));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 8, 480));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 5, 600));
        activities.add(new ActivityModel("Activity", 120, calendar.getTime(), 6, 720));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 90, calendar.getTime(), 7, 630));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        activities.add(new ActivityModel("Activity", 60, calendar.getTime(), 10, 600));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activities.add(new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500));
        //#endregion

        //#region
        List<List<ActivityModel>> activitiesOfLastWeekPerDays = new ArrayList<>();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        activitiesOfLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 8, 480)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 5, 600),
                        new ActivityModel("Activity", 120, calendar.getTime(), 6, 720)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 90, calendar.getTime(), 7, 630)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600),
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500)));
        //#endregion

        //#region
        List<List<ActivityModel>> activitiesOfFourLastWeekPerDays = new ArrayList<>();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, -4);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 8, 480)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 5, 600),
                        new ActivityModel("Activity", 120, calendar.getTime(), 6, 720)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 90, calendar.getTime(), 7, 630)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600),
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500)));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 8, 480)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 5, 600),
                        new ActivityModel("Activity", 120, calendar.getTime(), 6, 720)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 90, calendar.getTime(), 7, 630)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600),
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500)));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 8, 480)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 5, 600),
                        new ActivityModel("Activity", 120, calendar.getTime(), 6, 720)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 90, calendar.getTime(), 7, 630)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600),
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500)));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, +1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 8, 480)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 120, calendar.getTime(), 9, 1080)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 5, 600),
                        new ActivityModel("Activity", 120, calendar.getTime(), 6, 720)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 90, calendar.getTime(), 7, 630)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600),
                        new ActivityModel("Activity", 60, calendar.getTime(), 10, 600)));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        activitiesOfFourLastWeekPerDays.add(
                Arrays.asList(
                        new ActivityModel("Activity", 150, calendar.getTime(), 10, 1500)));
        //#endregion

        // Act & Assert
        when(activityController.getAllActivities()).thenReturn(activities);
        when(activityController.getActivitiesFromPastWeeksPerDay(activities, 1)).then(invocation -> {
            if (shouldThrow) {
                throw new NotEnoughActivitiesException("");
            } else {
                return activitiesOfLastWeekPerDays;
            }
        });
        when(activityController.calculateSumOfLoadsOfActivities(activitiesOfLastWeekPerDays)).thenReturn(5910.0);
        when(activityController.getActivitiesFromPastWeeksPerDay(activities, 4)).then(invocation -> {
            if (expectedAwcr == null) {
                throw new NotEnoughActivitiesException("");
            } else {
                return activitiesOfFourLastWeekPerDays;
            }
        });
        when(activityController.calculateSumOfLoadsOfActivities(activitiesOfFourLastWeekPerDays)).thenReturn(5910.0 * 4);

        if (shouldThrow) {
            Assertions.assertThrows(NotEnoughActivitiesException.class, () -> {
                athleteWorkoutInformationsController.getAthleteWorkoutInformations();
            });
        } else {
            AthleteWorkoutInformationsModel result = Assertions.assertDoesNotThrow(() -> {
                return athleteWorkoutInformationsController.getAthleteWorkoutInformations();
            });
            Assertions.assertEquals(expectedTotalLoad, result.getTotalLoad());
            Assertions.assertEquals(expectedMonotony, result.getMonotony());
            Assertions.assertEquals(expectedConstraint, result.getConstraint());
            Assertions.assertEquals(expectedFitness, result.getFitness());
            Assertions.assertEquals(expectedAwcr, result.getAwcr());
        }
    }
}