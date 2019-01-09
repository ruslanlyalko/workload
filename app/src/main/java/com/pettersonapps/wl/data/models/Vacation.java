package com.pettersonapps.wl.data.models;

import android.os.Parcel;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Vacation extends BaseModel {

    private String userDepartment;
    private String userName;
    private String userId;
    private String status;
    private Date from;
    private Date to;

    public Vacation(final String userDepartment, final String userName, final String userId, final String status, final Date from, final Date to) {
        this.userDepartment = userDepartment;
        this.userName = userName;
        this.userId = userId;
        this.status = status;
        this.from = from;
        this.to = to;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(final String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(final Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(final Date to) {
        this.to = to;
    }

    public Vacation() {
        from = new Date();
        to = new Date();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userDepartment);
        dest.writeString(this.userName);
        dest.writeString(this.userId);
        dest.writeString(this.status);
        dest.writeLong(this.from != null ? this.from.getTime() : -1);
        dest.writeLong(this.to != null ? this.to.getTime() : -1);
    }

    protected Vacation(Parcel in) {
        super(in);
        this.userDepartment = in.readString();
        this.userName = in.readString();
        this.userId = in.readString();
        this.status = in.readString();
        long tmpFrom = in.readLong();
        this.from = tmpFrom == -1 ? null : new Date(tmpFrom);
        long tmpTo = in.readLong();
        this.to = tmpTo == -1 ? null : new Date(tmpTo);
    }

    public static final Creator<Vacation> CREATOR = new Creator<Vacation>() {
        @Override
        public Vacation createFromParcel(Parcel source) {return new Vacation(source);}

        @Override
        public Vacation[] newArray(int size) {return new Vacation[size];}
    };

}
