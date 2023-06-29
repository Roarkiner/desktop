package Model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import Enum.SexEnum;

public class AthleteModel {
    @BsonId
    private ObjectId id;
    private String lastName;
    private String firstName;
    private Date birthDate;
    private SexEnum sex;

    @BsonCreator
    public AthleteModel(
        @BsonProperty("lastName") String lastName,
        @BsonProperty("firstName")String firstName,
        @BsonProperty("birthDate") Date birthDate,
        @BsonProperty("sex") SexEnum sex
    ){
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.sex = sex;
    }

    public ObjectId getId() {
        return id;
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
}
