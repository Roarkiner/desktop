import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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

import Controller.AthleteController;
import Enum.SexEnum;
import Model.AthleteModel;
import Repository.AthleteRepository;
import exceptions.AthleteValidationException;

class AthleteControllerTest {

    @Mock
    private AthleteRepository athleteRepository;

    private AthleteController athleteController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        athleteController = new AthleteController(athleteRepository);
    }

    @Test
    void testGetAthleteById() {
        // Arrange
        ObjectId athleteId = new ObjectId();
        AthleteModel mockAthlete = new AthleteModel("Michel", "Polnaref", new Date(), SexEnum.MALE);
        mockAthlete.setId(athleteId);
        when(athleteRepository.getAthlete(athleteId)).thenReturn(mockAthlete);

        // Act
        AthleteModel result = athleteController.getAthleteById(athleteId);

        // Assert
        verify(athleteRepository).getAthlete(athleteId);
        Assertions.assertEquals(athleteId, result.getId());
        Assertions.assertEquals("Michel", result.getLastName());
        Assertions.assertEquals("Polnaref", result.getFirstName());
    }

    @Test
    void testGetAllAthletes() {
        // Arrange
        AthleteModel athlete1 = new AthleteModel("Brigitte", "Mythra", new Date(), SexEnum.FEMALE);
        athlete1.setId(new ObjectId());
        AthleteModel athlete2 = new AthleteModel("Helicoptere", "Decombat", new Date(), SexEnum.UNDEFINED);
        athlete2.setId(new ObjectId());
        List<AthleteModel> mockAthletes = Arrays.asList(athlete1, athlete2);
        when(athleteRepository.getAllAthletes()).thenReturn(mockAthletes);

        // Act
        List<AthleteModel> result = athleteController.getAllActivities();

        // Assert
        verify(athleteRepository).getAllAthletes();
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Brigitte", result.get(0).getLastName());
        Assertions.assertEquals("Mythra", result.get(0).getFirstName());
        Assertions.assertEquals("Helicoptere", result.get(1).getLastName());
        Assertions.assertEquals("Decombat", result.get(1).getFirstName());
    }

    private static Stream<Arguments> provideDataForSaveUpdateAthlete() {
        return Stream.of(
                Arguments.of("Michel", "Polnaref", new Date(), SexEnum.MALE, false, 0),
                Arguments.of(null, "Polnaref", new Date(), SexEnum.MALE, true, 1),
                Arguments.of("Michel", null, new Date(), SexEnum.MALE, true, 1),
                Arguments.of("Michel", "Polnaref", null, SexEnum.MALE, true, 1),
                Arguments.of("Michel", "Polnaref", new Date(), null, true, 1),
                Arguments.of("01234568901234567890123456789012345678901234567890123456789", "Polnaref", new Date(),
                        SexEnum.MALE, true, 1),
                Arguments.of(null, "01234568901234567890123456789012345678901234567890123456789", null, null, true, 4));
    }

    @ParameterizedTest
    @MethodSource("provideDataForSaveUpdateAthlete")
    void testSaveAthlete(String lastName, String firstname, Date birthDate, SexEnum sex, boolean shouldThrow,
            int exceptionsSize) {
        AthleteModel athlete = new AthleteModel(lastName, firstname, birthDate, sex);

        if (shouldThrow) {
            AthleteValidationException exception = assertThrows(AthleteValidationException.class, () -> {
                athleteController.saveAthlete(athlete);
            });
            Assertions.assertEquals(exception.getValidationErrors().size(), exceptionsSize);
        } else {
            Assertions.assertDoesNotThrow(() -> {
                athleteController.saveAthlete(athlete);
            });
        }
    }
    
    @ParameterizedTest
    @MethodSource("provideDataForSaveUpdateAthlete")
    void testUpdateAthlete(String lastName, String firstname, Date birthDate, SexEnum sex, boolean shouldThrow,
            int exceptionsSize) {
        // Arrange
        AthleteModel athlete = new AthleteModel(lastName, firstname, birthDate, sex);

        // Act & Assert
        if (shouldThrow) {
            AthleteValidationException exception = assertThrows(AthleteValidationException.class, () -> {
                athleteController.saveAthlete(athlete);
            });
            Assertions.assertEquals(exception.getValidationErrors().size(), exceptionsSize);
        } else {
            Assertions.assertDoesNotThrow(() -> {
                athleteController.saveAthlete(athlete);
            });
        }
    }

    private static Stream<Arguments> provideDataForDeleteAthlete() {
        return Stream.of(
            Arguments.of(1l, false),
            Arguments.of(2l, false),
            Arguments.of(0l, true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForDeleteAthlete")
    void testDeleteAthlete(long numberOfAtleteDeleted, boolean shouldThrow) {
        // Arrange
        ObjectId athleteId = new ObjectId();
        when(athleteRepository.deleteAthlete(athleteId)).thenReturn(numberOfAtleteDeleted);

        // Act & Assert
        if (shouldThrow) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                athleteController.deleteAthlete(athleteId);
            });
            Assertions.assertEquals(exception.getMessage(), "No athlete matches the id : " + athleteId);
        } else {
            Assertions.assertDoesNotThrow(() -> {
                athleteController.deleteAthlete(athleteId);
            });
        }
    }
}