package com.pettersonapps.wl.data.models;

import android.os.Parcel;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Holiday extends BaseModel {

    private String title;
    private Date date;

    public Holiday() {
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected Holiday(Parcel in) {
        super(in);
        this.title = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<Holiday> CREATOR = new Creator<Holiday>() {
        @Override
        public Holiday createFromParcel(Parcel source) {return new Holiday(source);}

        @Override
        public Holiday[] newArray(int size) {return new Holiday[size];}
    };
}
