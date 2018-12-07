package com.pettersonapps.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Report extends BaseModel implements Parcelable {

    private String userId;
    private String userName;
    private String userDepartment;
    private Date date;
    private String status;
    private String p1;
    private int t1;
    private String p2;
    private int t2;
    private String p3;
    private int t3;
    private String p4;
    private int t4;
    private Date updatedAt;

    public Report(Report reportToCopy) {
        date = new Date();
        updatedAt = new Date();
        userId = reportToCopy.userId;
        userName = reportToCopy.userName;
        userDepartment = reportToCopy.userDepartment;
        status = reportToCopy.status;
        p1 = reportToCopy.p1;
        t1 = reportToCopy.t1;
        p2 = reportToCopy.p2;
        t2 = reportToCopy.t2;
        p3 = reportToCopy.p3;
        t3 = reportToCopy.t3;
        p4 = reportToCopy.p4;
        t4 = reportToCopy.t4;
    }

    public Report() {
        date = new Date();
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(final String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 8);
        this.date = c.getTime();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(final String p1) {
        this.p1 = p1;
    }

    public int getT1() {
        return t1;
    }

    public void setT1(final int t1) {
        this.t1 = t1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(final String p2) {
        this.p2 = p2;
    }

    public int getT2() {
        return t2;
    }

    public void setT2(final int t2) {
        this.t2 = t2;
    }

    public String getP3() {
        return p3;
    }

    public void setP3(final String p3) {
        this.p3 = p3;
    }

    public int getT3() {
        return t3;
    }

    public void setT3(final int t3) {
        this.t3 = t3;
    }

    public String getP4() {
        return p4;
    }

    public void setP4(final String p4) {
        this.p4 = p4;
    }

    public int getT4() {
        return t4;
    }

    public void setT4(final int t4) {
        this.t4 = t4;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Report report = (Report) o;
        return t1 == report.t1 &&
                t2 == report.t2 &&
                t3 == report.t3 &&
                t4 == report.t4 &&
                Objects.equals(userId, report.userId) &&
                Objects.equals(userName, report.userName) &&
                Objects.equals(userDepartment, report.userDepartment) &&
                Objects.equals(date, report.date) &&
                Objects.equals(status, report.status) &&
                Objects.equals(p1, report.p1) &&
                Objects.equals(p2, report.p2) &&
                Objects.equals(p3, report.p3) &&
                Objects.equals(p4, report.p4) &&
                Objects.equals(updatedAt, report.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, userName, userDepartment, date, status, p1, t1, p2, t2, p3, t3, p4, t4, updatedAt);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.userDepartment);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeString(this.status);
        dest.writeString(this.p1);
        dest.writeInt(this.t1);
        dest.writeString(this.p2);
        dest.writeInt(this.t2);
        dest.writeString(this.p3);
        dest.writeInt(this.t3);
        dest.writeString(this.p4);
        dest.writeInt(this.t4);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected Report(Parcel in) {
        super(in);
        this.userId = in.readString();
        this.userName = in.readString();
        this.userDepartment = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.status = in.readString();
        this.p1 = in.readString();
        this.t1 = in.readInt();
        this.p2 = in.readString();
        this.t2 = in.readInt();
        this.p3 = in.readString();
        this.t3 = in.readInt();
        this.p4 = in.readString();
        this.t4 = in.readInt();
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel source) {return new Report(source);}

        @Override
        public Report[] newArray(int size) {return new Report[size];}
    };
}
