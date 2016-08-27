package com.kamron.pogoiv.updater;


import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppUpdate implements Parcelable {

    private String assetUrl;
    private String version;
    private String changelog;

    private AppUpdate(Parcel in) {
        assetUrl = in.readString();
        version = in.readString();
        changelog = in.readString();
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
