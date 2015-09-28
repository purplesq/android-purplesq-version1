package com.purplesq.purplesq.vos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nishant on 28/09/15.
 */
public class VersionVo implements Parcelable {
    private int currentVersion;
    private int forcedVersions;
    private String currentVersionName;
    private List<WhatsNewVo> whatsnew;

    public VersionVo() {
    }

    public VersionVo(int currentVersion, int forcedVersions, String currentVersionName, List<WhatsNewVo> whatsnew) {
        this.currentVersion = currentVersion;
        this.forcedVersions = forcedVersions;
        this.currentVersionName = currentVersionName;
        this.whatsnew = whatsnew;
    }

    protected VersionVo(Parcel in) {
        currentVersion = in.readInt();
        forcedVersions = in.readInt();
        currentVersionName = in.readString();
        if (in.readByte() == 0x01) {
            whatsnew = new ArrayList<WhatsNewVo>();
            in.readList(whatsnew, WhatsNewVo.class.getClassLoader());
        } else {
            whatsnew = null;
        }
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public int getForcedVersions() {
        return forcedVersions;
    }

    public void setForcedVersions(int forcedVersions) {
        this.forcedVersions = forcedVersions;
    }

    public String getCurrentVersionName() {
        return currentVersionName;
    }

    public void setCurrentVersionName(String currentVersionName) {
        this.currentVersionName = currentVersionName;
    }

    public List<WhatsNewVo> getWhatsnew() {
        return whatsnew;
    }

    public void setWhatsnew(List<WhatsNewVo> whatsnew) {
        this.whatsnew = whatsnew;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(currentVersion);
        dest.writeInt(forcedVersions);
        dest.writeString(currentVersionName);
        if (whatsnew == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(whatsnew);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<VersionVo> CREATOR = new Parcelable.Creator<VersionVo>() {
        @Override
        public VersionVo createFromParcel(Parcel in) {
            return new VersionVo(in);
        }

        @Override
        public VersionVo[] newArray(int size) {
            return new VersionVo[size];
        }
    };
}
