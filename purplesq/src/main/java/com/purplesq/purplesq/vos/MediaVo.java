package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class MediaVo {
    private String _id;
    private String type;
    private String name;
    private String subtype;
    private String url;
    private DescriptionVo description;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DescriptionVo getDescription() {
        return description;
    }

    public void setDescription(DescriptionVo description) {
        this.description = description;
    }
}
