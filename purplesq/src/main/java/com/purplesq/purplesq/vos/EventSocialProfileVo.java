package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class EventSocialProfileVo {
    private String _id;
    private String link;
    private String type;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
