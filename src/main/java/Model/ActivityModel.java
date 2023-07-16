package model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class ActivityModel {
    @BsonId
    private ObjectId activityId;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 cractères")
    private String name;

    @NotNull(message = "La durée est obligatoire")
    @DecimalMin(value = "1", message = "La durée doit être entre 1 et 1440 minutes")
    @DecimalMax(value = "1440", message = "La durée doit être entre 1 et 1440 minutes")
    private Double duration;

    @NotNull(message = "La date est obligatoire")
    @PastOrPresent(message = "La date doit être passée ou aujourd'hui")
    private Date date;

    @NotNull(message = "Le RPE est obligatoire")
    @DecimalMin(value = "0", message = "Le RPE doit être compris entre 1 et 10")
    @DecimalMax(value = "10", message = "Le RPE doit être compris entre 1 et 10")
    private Double rpe;

    @NotNull(message = "La charge est obligatoire")
    private Double load;

    @BsonCreator
    public ActivityModel(
            @BsonProperty("name") String name,
            @BsonProperty("duration") double duration,
            @BsonProperty("date") Date date,
            @BsonProperty("rpe") double rpe,
            @BsonProperty("load") double load) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.rpe = rpe;
        this.load = load;
    }

    public ActivityModel(
            String name,
            double duration,
            Date date,
            double rpe) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.rpe = rpe;
    }

    public ObjectId getActivityId() {
        return activityId;
    }

    public void setActivityId(ObjectId activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getRpe() {
        return rpe;
    }

    public void setRpe(double rpe) {
        this.rpe = rpe;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }
}
