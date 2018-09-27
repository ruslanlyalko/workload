package com.pettersonapps.wl.data.models;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class User extends BaseModel {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {return new User(source);}

        @Override
        public User[] newArray(int size) {return new User[size];}
    };
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String department;
    private String skype;
    private Date birthday;
    private Date firstWorkingDate;
    private String notificationHour;
    private String token;
    private boolean isAllowEditPastReports;
    private boolean isBlocked;
    private boolean isAdmin;
    private List<Project> projects;

    public User() {
        firstWorkingDate = new Date();
        birthday = new Date();
        notificationHour = "18";
        skype = "";
        phone = "";
        projects = new ArrayList<>();
    }

    protected User(Parcel in) {
        super(in);
        this.name = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.avatar = in.readString();
        this.department = in.readString();
        this.skype = in.readString();
        long tmpBirthday = in.readLong();
        this.birthday = tmpBirthday == -1 ? null : new Date(tmpBirthday);
        long tmpFirstWorkingDate = in.readLong();
        this.firstWorkingDate = tmpFirstWorkingDate == -1 ? null : new Date(tmpFirstWorkingDate);
        this.notificationHour = in.readString();
        this.token = in.readString();
        this.isAllowEditPastReports = in.readByte() != 0;
        this.isBlocked = in.readByte() != 0;
        this.isAdmin = in.readByte() != 0;
        this.projects = in.createTypedArrayList(Project.CREATOR);
    }

    public boolean getIsAllowEditPastReports() {
        return isAllowEditPastReports;
    }

    public void setIsAllowEditPastReports(final boolean allowEditPastReports) {
        isAllowEditPastReports = allowEditPastReports;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(final String skype) {
        this.skype = skype;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(final List<Project> projects) {
        this.projects = projects;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getNotificationHour() {
        return notificationHour;
    }

    public void setNotificationHour(final String notificationHour) {
        this.notificationHour = notificationHour;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(final boolean blocked) {
        isBlocked = blocked;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(final boolean admin) {
        isAdmin = admin;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(final String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    public Date getFirstWorkingDate() {
        return firstWorkingDate;
    }

    public void setFirstWorkingDate(final Date firstWorkingDate) {
        this.firstWorkingDate = firstWorkingDate;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.avatar);
        dest.writeString(this.department);
        dest.writeString(this.skype);
        dest.writeLong(this.birthday != null ? this.birthday.getTime() : -1);
        dest.writeLong(this.firstWorkingDate != null ? this.firstWorkingDate.getTime() : -1);
        dest.writeString(this.notificationHour);
        dest.writeString(this.token);
        dest.writeByte(this.isAllowEditPastReports ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isBlocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.projects);
    }
}
