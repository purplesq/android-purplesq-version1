package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class EventItinerariesVo {
    private String _id;
    private String title;
    private String type;
    private String image;
    private String location;
    private String description;
    private EventItinerariesScheduleVo schedule;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventItinerariesScheduleVo getItinerariesSchedule() {
        return schedule;
    }

    public void setItinerariesSchedule(EventItinerariesScheduleVo schedule) {
        this.schedule = schedule;
    }
}
