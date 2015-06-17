
package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class ItinerariesVo {
    private String _id;
    private String title;
    private String type;
    private String image;
    private String location;
    private DescriptionVo description;
    private IconVo icon;
    private ItinerariesScheduleVo schedule;

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

    public DescriptionVo getDescription() {
        return description;
    }

    public void setDescription(DescriptionVo description) {
        this.description = description;
    }

    public IconVo getIcon() {
        return icon;
    }

    public void setIcon(IconVo icon) {
        this.icon = icon;
    }

    public ItinerariesScheduleVo getItinerariesSchedule() {
        return schedule;
    }

    public void setItinerariesSchedule(ItinerariesScheduleVo schedule) {
        this.schedule = schedule;
    }
}
