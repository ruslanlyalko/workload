package com.pettersonapps.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class AppSettings implements Parcelable {

    private String notificationEmail;
    private boolean isSnowig;

    public boolean getIsSnowig() {
        return isSnowig;
    }

    public void setIsSnowig(final boolean snowig) {
        isSnowig = snowig;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(final String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppSettings that = (AppSettings) o;
        return isSnowig == that.isSnowig &&
                Objects.equals(notificationEmail, that.notificationEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationEmail, isSnowig);
    }

    public AppSettings() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.notificationEmail);
        dest.writeByte(this.isSnowig ? (byte) 1 : (byte) 0);
    }

    protected AppSettings(Parcel in) {
        this.notificationEmail = in.readString();
        this.isSnowig = in.readByte() != 0;
    }

    public static final Creator<AppSettings> CREATOR = new Creator<AppSettings>() {
        @Override
        public AppSettings createFromParcel(Parcel source) {return new AppSettings(source);}

        @Override
        public AppSettings[] newArray(int size) {return new AppSettings[size];}
    };
}
