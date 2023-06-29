package Model;

import java.util.Date;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public class ActivityModel {
    @BsonId
    private ObjectId id;
    private String name;
    private Double duration;
    private Date date;
    private Double rpe;
    private Double load;
    
    @BsonCreator
    public ActivityModel(
        @BsonProperty("name") String name,
        @BsonProperty("duration") Double duration,
        @BsonProperty("date") Date date,
        @BsonProperty("rpe") Double rpe,
        @BsonProperty("load") Double load
    ){
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.rpe = rpe;
        this.load = load;
    }

    public ObjectId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getRpe() {
        return rpe;
    }

    public void setRpe(Double rpe) {
        this.rpe = rpe;
    }

    public Double getLoad() {
        return load;
    }

    public void setLoad(Double load) {
        this.load = load;
    }
}
