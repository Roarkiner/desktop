import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Controller.ActivityController;
import Model.ActivityModel;
import Repository.ActivityRepository;
import exceptions.ActivityValidationException;
import exceptions.NotEnoughActivitiesException;

class ActivityControllerTest {

    @Mock
    private ActivityRepository activityRepository;

    private ActivityController activityController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        activityController = new ActivityController(activityRepository);
    }

    @Test
    void testGetActivityById() {
        // Arrange
        ObjectId activityId = new ObjectId();
        ActivityModel mockActivity = new ActivityModel("Test", 1.0, new Date(), 1.0, 1.0);
        mockActivity.setActivityId(activityId);
        when(activityRepository.getActivity(activityId)).thenReturn(mockActivity);

        // Act
        ActivityModel result = activityController.getActivityById(activityId);

        // Assert
        verify(activityRepository).getActivity(activityId);
        Assertions.assertEquals(activityId, result.getActivityId());
        Assertions.assertEquals("Test", result.getName());
    }

    @Test
    void testGetAllActivitys() {
        // Arrange
        ActivityModel activity1 = new ActivityModel("Test1", 1.0, new Date(), 1.0, 1.0);
        activity1.setActivityId(new ObjectId());
        ActivityModel activity2 = new ActivityModel("Test2", 1.0, new Date(), 1.0, 1.0);
        activity2.setActivityId(new ObjectId());
        List<ActivityModel> mockActivitys = Arrays.asList(activity1, activity2);
        when(activityRepository.getAllActivities()).thenReturn(mockActivitys);

        // Act
        List<ActivityModel> result = activityController.getAllActivities();

        // Assert
        verify(activityRepository).getAllActivities();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Test1", result.get(0).getName());
        Assertions.assertEquals("Test2", result.get(1).getName());
    }

    private static Stream<Arguments> provideDataForSaveUpdateActivity() {
        return Stream.of(
                Arguments.of("Test", 1.0, new Date(), 3.0, false, 0),
                Arguments.of(null, 1.0, new Date(), 3.0, true, 1),
                Arguments.of("Test", 0.0, new Date(), 3.0, true, 1),
                Arguments.of("Test", 1.0, null, 3.0, true, 1),
                Arguments.of("Test", 1.0, new Date(), -1.0, true, 1),
                Arguments.of(
                        "0123456890123456789012345678901234567890123456789012345678901234568901234567890123456789012345678901234567890123456789",
                        1.0, new Date(),
                        3.0, true, 1),
                Arguments.of(null, 0.0, null, -1.0, true, 4));
    }

    @ParameterizedTest
    @MethodSource("provideDataForSaveUpdateActivity")
    void testSaveActivity(String name, Double duration, Date date, Double rpe, boolean shouldThrow,
            int exceptionsSize) {
        // Arrange
        ActivityModel activity = new ActivityModel(name, duration, date, rpe);

        // Act & Assert
        if (shouldThrow) {
            ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> {
                activityController.saveActivity(activity);
            });
            Assertions.assertEquals(exception.getValidationErrors().size(), exceptionsSize);
        } else {
            Assertions.assertDoesNotThrow(() -> {
                activityController.saveActivity(activity);
            });
        }
    }

    @ParameterizedTest
    @MethodSource("provideDataForSaveUpdateActivity")
    void testUpdateActivity(String name, Double duration, Date date, Double rpe, boolean shouldThrow,
            int exceptionsSize) {
        // Arrange
        ActivityModel activity = new ActivityModel(name, duration, date, rpe);

        // Act & Assert
        if (shouldThrow) {
            ActivityValidationException exception = assertThrows(ActivityValidationException.class, () -> {
                activityController.saveActivity(activity);
            });
            Assertions.assertEquals(exception.getValidationErrors().size(), exceptionsSize);
        } else {
            Assertions.assertDoesNotThrow(() -> {
                activityController.saveActivity(activity);
            });
        }
    }

    private static Stream<Arguments> provideDataForDeleteActivity() {
        return Stream.of(
                Arguments.of(1, false),
                Arguments.of(2, false),
                Arguments.of(0, true));
    }

    @ParameterizedTest
    @MethodSource("provideDataForDeleteActivity")
    void testDeleteActivity(long numberOfActivitiesWithActivityId, boolean shouldThrow) {
        // Arrange
        ObjectId activityToDeleteId = new ObjectId();
        List<ActivityModel> activities = new ArrayList<>();
        for (int i = 0; i < numberOfActivitiesWithActivityId; i++) {
            ActivityModel activityToAdd = new ActivityModel("Test", 1.0, new Date(), 1.0, 1.0);
            activityToAdd.setActivityId(activityToDeleteId);
            activities.add(activityToAdd);
        }
        ActivityModel activity2 = new ActivityModel("Activity test 2", 0.1, new Date(), 0.1, 0.1);
        activity2.setActivityId(new ObjectId());
        activities.add(activity2);
        List<ActivityModel> activitiesBeforeDeletion = List.copyOf(activities);
        activities.removeIf(activity -> activity.getActivityId().equals(activityToDeleteId));
        List<ActivityModel> activitiesAfterDeletion = List.copyOf(activities);

        when(activityRepository.getAllActivities())
                .thenReturn(activitiesBeforeDeletion)
                .thenReturn(activitiesAfterDeletion);

        // Act & Assert
        if (shouldThrow) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                activityController.deleteActivity(activityToDeleteId);
            });
            Assertions.assertEquals(exception.getMessage(), "No activity matches the id : " + activityToDeleteId);
        } else {
            Assertions.assertDoesNotThrow(() -> {
                activityController.deleteActivity(activityToDeleteId);
            });
        }
    }

    private static Stream<Arguments> provideDataForGetActivitiesBetweenDates() {
        return Stream.of(
                Arguments.of(toDate(LocalDate.now()), toDate(LocalDate.now().plusDays(1)), 2),
                Arguments.of(toDate(LocalDate.now().minusDays(2)), toDate(LocalDate.now().plusDays(2)), 4),
                Arguments.of(toDate(LocalDate.now()), toDate(LocalDate.now()), 1),
                Arguments.of(toDate(LocalDate.now().minusDays(7)), toDate(LocalDate.now().minusDays(2)), 0));
    }

    @ParameterizedTest
    @MethodSource("provideDataForGetActivitiesBetweenDates")
    void testGetActivitiesBetweenDates(Date startDate, Date endDate, int numberOfActivitiesBetween) {
        // Arrange
        List<ActivityModel> activities = new ArrayList<>();
        activities.add(new ActivityModel("Activity 1", 10.0, toDate(LocalDate.now().minusDays(1)), 1.0));
        activities.add(new ActivityModel("Activity 2", 10.0, toDate(LocalDate.now()), 1.0));
        activities.add(new ActivityModel("Activity 3", 10.0, toDate(LocalDate.now().plusDays(1)), 1.0));
        activities.add(new ActivityModel("Activity 4", 10.0, toDate(LocalDate.now().plusDays(2)), 1.0));

        // Act
        List<ActivityModel> result = activityController.getActivitiesBetweenDates(activities, startDate, endDate);

        // Assert
        Assertions.assertEquals(numberOfActivitiesBetween, result.size());
    }

    private static Stream<Arguments> provideDataForGetActivitiesFromPastWeeks() {
        return Stream.of(
                Arguments.of(1, false),
                Arguments.of(2, false),
                Arguments.of(4, true));
    }

    @ParameterizedTest
    @MethodSource("provideDataForGetActivitiesFromPastWeeks")
    void getActivitiesFromPastWeeks(int numberOfWeeks, boolean shouldThrow) {
        // Arrange
        List<ActivityModel> activities = new ArrayList<>();
        activities.add(new ActivityModel("Activity 1", 10.0, toDate(LocalDate.now().minusDays(21)), 1.0));
        activities.add(new ActivityModel("Activity 2", 10.0, toDate(LocalDate.now().minusDays(14)), 1.0));
        activities.add(new ActivityModel("Activity 3", 10.0, toDate(LocalDate.now().minusDays(7)), 1.0));
        activities.add(new ActivityModel("Activity 4", 10.0, toDate(LocalDate.now()), 1.0));

        // Act && Assert
        if (shouldThrow) {
            Assertions.assertThrows(NotEnoughActivitiesException.class, () -> {
                activityController.getActivitiesFromPastWeeks(Collections.emptyList(), numberOfWeeks);
            });
        } else {
            List<ActivityModel> result = Assertions.assertDoesNotThrow(() -> {
                return activityController.getActivitiesFromPastWeeks(activities, numberOfWeeks);
            });

            Assertions.assertEquals(numberOfWeeks, result.size());
        }
    }

    private static Date toDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    @Test
    void calculateLoad() {
        // Arrange
        double duration = 1.0;
        double rpe = 5.5;

        // Act
        double result = activityController.calculateLoad(duration, rpe);

        // Assert
        Assertions.assertEquals(duration * rpe, result);
    }

    @Test
    void calculateSumOfLoadsOfActivities() {
        // Arrange
        List<ActivityModel> activities = new ArrayList<>();
        activities.add(new ActivityModel("Activity 1", 10.0, new Date(), 1.0, 10.0));
        activities.add(new ActivityModel("Activity 2", 10.0, new Date(), 1.0, 10.0));
        activities.add(new ActivityModel("Activity 3", 10.0, new Date(), 1.0, 10.0));
        activities.add(new ActivityModel("Activity 4", 10.0, new Date(), 1.0, 10.0));

        // Act
        double result = activityController.calculateSumOfLoadsOfActivities(activities);

        // Assert
        Assertions.assertEquals(40.0, result);
    }
}