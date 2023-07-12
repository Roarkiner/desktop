package Model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class ActivityModel {
    @BsonId
    private ObjectId activityId;
    private String name;
    private double duration;
    private Date date;
    private double rpe;
    private double load;

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
