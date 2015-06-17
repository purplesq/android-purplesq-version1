
package com.purplesq.purplesq.vos;

/**
 * Created by nishant on 01/06/15.
 */
public class HighlightsVo {
    private String _id;
    private DescriptionVo description;
    private String image;
    private String name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public DescriptionVo getDescription() {
        return description;
    }

    public void setDescription(DescriptionVo description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
