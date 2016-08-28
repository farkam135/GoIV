package com.kamron.pogoiv.updater;


import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AppUpdate implements Parcelable {

    private String assetUrl;
    private String version;
    private String changelog;
    private int status;

    public static final int UPDATE_AVAILABLE = 1234;
    public static final int UP_TO_DATE = 1235;
    public static final int ERROR = 1236;

    private AppUpdate(Parcel in) {
        assetUrl = in.readString();
        version = in.readString();
        changelog = in.readString();
        status = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(assetUrl);
        out.writeString(version);
        out.writeString(changelog);
        out.writeInt(status);
    }

    public static final Parcelable.Creator<AppUpdate> CREATOR = new Parcelable.Creator<AppUpdate>() {
        public AppUpdate createFromParcel(Parcel in) {
            return new AppUpdate(in);
        }

        public AppUpdate[] newArray(int size) {
            return new AppUpdate[size];
        }
    };
}
