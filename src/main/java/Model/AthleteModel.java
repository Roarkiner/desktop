package Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import Enum.SexEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public class AthleteModel {
    @BsonId
    private ObjectId id;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 100 characters")
    private String lastName;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 100 characters")
    private String firstName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private Date birthDate;

    @NotNull(message = "Sex is required")
    private SexEnum sex;

    private List<ActivityModel> activities;

    @BsonCreator
    public AthleteModel(
            @BsonProperty("lastName") String lastName,
            @BsonProperty("firstName") String firstName,
            @BsonProperty("birthDate") Date birthDate,
            @BsonProperty("sex") SexEnum sex,
            @BsonProperty("activities") List<ActivityModel> activities) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.sex = sex;
        this.activities = activities;
    }

    public AthleteModel(
            String lastName,
            String firstName,
            Date birthDate,
            SexEnum sex) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.sex = sex;
        this.activities = new ArrayList<>();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }

    public List<ActivityModel> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityModel> activities) {
        this.activities = activities;
    }
}
