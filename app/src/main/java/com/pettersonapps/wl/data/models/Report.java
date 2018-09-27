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

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel source) {return new Report(source);}

        @Override
        public Report[] newArray(int size) {return new Report[size];}
    };
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
    private String updatedBy;

    public Report() {
        date = new Date();
    }

    protected Report(Parcel in) {
        super(in);
        this.userId = in.readString();
        this.userName = in.readString();
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
        this.updatedBy = in.readString();
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        Report report = (Report) o;
        return getT1() == report.getT1() &&
                getT2() == report.getT2() &&
                getT3() == report.getT3() &&
                getT4() == report.getT4() &&
                Objects.equals(getUserId(), report.getUserId()) &&
                Objects.equals(getUserName(), report.getUserName()) &&
                Objects.equals(getUserDepartment(), report.getUserDepartment()) &&
                Objects.equals(getDate(), report.getDate()) &&
                Objects.equals(getStatus(), report.getStatus()) &&
                Objects.equals(getP1(), report.getP1()) &&
                Objects.equals(getP2(), report.getP2()) &&
                Objects.equals(getP3(), report.getP3()) &&
                Objects.equals(getP4(), report.getP4()) &&
                Objects.equals(getUpdatedAt(), report.getUpdatedAt()) &&
                Objects.equals(getUpdatedBy(), report.getUpdatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getUserName(), getUserDepartment(), getDate(), getStatus(), getP1(), getT1(), getP2(), getT2(), getP3(), getT3(), getP4(), getT4(), getUpdatedAt(), getUpdatedBy());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
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
        dest.writeString(this.updatedBy);
    }
}
