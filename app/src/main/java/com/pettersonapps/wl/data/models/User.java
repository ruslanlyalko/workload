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

    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String department;
    private String skype;
    private Date birthday;
    private Date firstWorkingDate;
    private boolean isBlocked;
    private boolean isAdmin;
    private List<Project> projects;
    private List<UserPush> pushHistory;
    private String comments;
    private boolean isAllowEditPastReports;
    //
    private String remindMeAt;
    private String token;
    private String version;
    private boolean isNightMode;
    private boolean isVip;
    private int defaultWorkingTime;
    private boolean isOldStyleCalendar;

    public User() {
        firstWorkingDate = new Date();
        pushHistory = new ArrayList<>();
        birthday = new Date();
        remindMeAt = "18:00";
        skype = "";
        phone = "";
        version = "";
        comments = "";
        defaultWorkingTime = 8;
        projects = new ArrayList<>();
    }

    public List<UserPush> getPushHistory() {
        return pushHistory;
    }

    public void setPushHistory(final List<UserPush> pushHistory) {
        this.pushHistory = pushHistory;
    }

    public int getDefaultWorkingTime() {
        return defaultWorkingTime;
    }

    public void setDefaultWorkingTime(final int defaultWorkingTime) {
        this.defaultWorkingTime = defaultWorkingTime;
    }

    public String getRemindMeAt() {
        return remindMeAt;
    }

    public void setRemindMeAt(final String remindMeAt) {
        this.remindMeAt = remindMeAt;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public boolean getIsNightMode() {
        return isNightMode;
    }

    public void setIsNightMode(final boolean nightMode) {
        isNightMode = nightMode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(final String comments) {
        this.comments = comments;
    }

    public boolean getIsOldStyleCalendar() {
        return isOldStyleCalendar;
    }

    public void setIsOldStyleCalendar(final boolean oldStyleCalendar) {
        isOldStyleCalendar = oldStyleCalendar;
    }

    public boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(final boolean vip) {
        isVip = vip;
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
        dest.writeByte(this.isBlocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAdmin ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.projects);
        dest.writeTypedList(this.pushHistory);
        dest.writeString(this.comments);
        dest.writeByte(this.isAllowEditPastReports ? (byte) 1 : (byte) 0);
        dest.writeString(this.remindMeAt);
        dest.writeString(this.token);
        dest.writeString(this.version);
        dest.writeByte(this.isNightMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVip ? (byte) 1 : (byte) 0);
        dest.writeInt(this.defaultWorkingTime);
        dest.writeByte(this.isOldStyleCalendar ? (byte) 1 : (byte) 0);
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
        this.isBlocked = in.readByte() != 0;
        this.isAdmin = in.readByte() != 0;
        this.projects = in.createTypedArrayList(Project.CREATOR);
        this.pushHistory = in.createTypedArrayList(UserPush.CREATOR);
        this.comments = in.readString();
        this.isAllowEditPastReports = in.readByte() != 0;
        this.remindMeAt = in.readString();
        this.token = in.readString();
        this.version = in.readString();
        this.isNightMode = in.readByte() != 0;
        this.isVip = in.readByte() != 0;
        this.defaultWorkingTime = in.readInt();
        this.isOldStyleCalendar = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {return new User(source);}

        @Override
        public User[] newArray(int size) {return new User[size];}
    };
}
