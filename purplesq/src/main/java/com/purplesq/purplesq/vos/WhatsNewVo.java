package com.purplesq.purplesq.vos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nishant on 28/09/15.
 */
public class WhatsNewVo implements Parcelable {
    private int code;
    private String name;
    private String changelog;

    public WhatsNewVo() {
    }

    public WhatsNewVo(int code, String name, String changelog) {
        this.code = code;
        this.name = name;
        this.changelog = changelog;
    }

    protected WhatsNewVo(Parcel in) {
        code = in.readInt();
        name = in.readString();
        changelog = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(name);
        dest.writeString(changelog);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WhatsNewVo> CREATOR = new Parcelable.Creator<WhatsNewVo>() {
        @Override
        public WhatsNewVo createFromParcel(Parcel in) {
            return new WhatsNewVo(in);
        }

        @Override
        public WhatsNewVo[] newArray(int size) {
            return new WhatsNewVo[size];
        }
    };

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }
}