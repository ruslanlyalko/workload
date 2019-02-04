package com.pettersonapps.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectInfo implements Parcelable {

    private double iOS;
    private double Android;
    private double Backend;
    private double Design;
    private double PM;
    private double QA;
    private double Other;
    private List<UserStatistic> users = new ArrayList<>();

    public ProjectInfo() {
    }

    @Override
    public String toString() {
        return "iOS = " + iOS + "h;   Android = " + Android + "h" +
                "\nBackend = " + Backend + "h;   Design = " + Design + "h" +
                "\nPM = " + PM + "h;   QA = " + QA + "h;   Other = " + Other + "h";
    }

    public String toLargeString() {
        return "iOS = " + iOS + "h;   \n\nAndroid = " + Android + "h" +
                "\n\nBackend = " + Backend + "h;   \n\nDesign = " + Design + "h" +
                "\n\nPM = " + PM + "h;   \n\nQA = " + QA + "h;   \n\nOther = " + Other + "h";
    }

    @Exclude
    public double getTotalCount() {
        return iOS + Android + Backend + Design + PM + QA + Other;
    }

    public List<UserStatistic> getUsers() {
        return users;
    }

    public void setUsers(final List<UserStatistic> users) {
        this.users = users;
    }

    public double getiOS() {
        return iOS;
    }

    public void setiOS(final double iOS) {
        this.iOS = iOS;
    }

    public double getAndroid() {
        return Android;
    }

    public void setAndroid(final double android) {
        Android = android;
    }

    public double getBackend() {
        return Backend;
    }

    public void setBackend(final double backend) {
        Backend = backend;
    }

    public double getDesign() {
        return Design;
    }

    public void setDesign(final double design) {
        Design = design;
    }

    public double getPM() {
        return PM;
    }

    public void setPM(final double PM) {
        this.PM = PM;
    }

    public double getQA() {
        return QA;
    }

    public void setQA(final double QA) {
        this.QA = QA;
    }

    public double getOther() {
        return Other;
    }

    public void setOther(final double other) {
        Other = other;
    }

    public ProjectInfo(Object object) {
        if(object instanceof HashMap) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            try {
                iOS = (Double) hashMap.get("iOS");
            } catch (Exception e) {
                iOS = (Integer) hashMap.get("iOS");
            }
            try {
                Android = (Double) hashMap.get("Android");
            } catch (Exception e) {
                Android = (Integer) hashMap.get("Android");
            }
            try {
                Backend = (Double) hashMap.get("Backend");
            } catch (Exception e) {
                Backend = (Integer) hashMap.get("Backend");
            }
            try {
                Design = (Double) hashMap.get("Design");
            } catch (Exception e) {
                Design = (Integer) hashMap.get("Design");
            }
            try {
                PM = (Double) hashMap.get("PM");
            } catch (Exception e) {
                PM = (Integer) hashMap.get("PM");
            }
            try {
                QA = (Double) hashMap.get("QA");
            } catch (Exception e) {
                QA = (Integer) hashMap.get("QA");
            }
            try {
                Other = (Double) hashMap.get("Other");
            } catch (Exception e) {
                Other = (Integer) hashMap.get("Other");
            }
            users = getUsersList((List<HashMap<String, Object>>) hashMap.get("Users"));
        }
    }

    private List<UserStatistic> getUsersList(final List<HashMap<String, Object>> users) {
        List<UserStatistic> list = new ArrayList<>();
        for (HashMap<String, Object> item : users) {
            double time;
            try {
                time = (Double) item.get("time");
            } catch (Exception e) {
                time = (Integer) item.get("time");
            }
            list.add(new UserStatistic((String) item.get("id"), (String) item.get("name"), (String) item.get("department"), time));
        }
        return list;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.iOS);
        dest.writeDouble(this.Android);
        dest.writeDouble(this.Backend);
        dest.writeDouble(this.Design);
        dest.writeDouble(this.PM);
        dest.writeDouble(this.QA);
        dest.writeDouble(this.Other);
        dest.writeTypedList(this.users);
    }

    protected ProjectInfo(Parcel in) {
        this.iOS = in.readDouble();
        this.Android = in.readDouble();
        this.Backend = in.readDouble();
        this.Design = in.readDouble();
        this.PM = in.readDouble();
        this.QA = in.readDouble();
        this.Other = in.readDouble();
        this.users = in.createTypedArrayList(UserStatistic.CREATOR);
    }

    public static final Creator<ProjectInfo> CREATOR = new Creator<ProjectInfo>() {
        @Override
        public ProjectInfo createFromParcel(Parcel source) {return new ProjectInfo(source);}

        @Override
        public ProjectInfo[] newArray(int size) {return new ProjectInfo[size];}
    };
}
