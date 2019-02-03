package com.pettersonapps.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProjectInfo implements Parcelable {

    private float iOS;
    private float Android;
    private float Backend;
    private float Design;
    private float PM;
    private float QA;
    private float Other;
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
    public float getTotalCount() {
        return iOS + Android + Backend + Design + PM + QA + Other;
    }

    public List<UserStatistic> getUsers() {
        return users;
    }

    public void setUsers(final List<UserStatistic> users) {
        this.users = users;
    }

    public float getiOS() {
        return iOS;
    }

    public void setiOS(final float iOS) {
        this.iOS = iOS;
    }

    public float getAndroid() {
        return Android;
    }

    public void setAndroid(final float android) {
        Android = android;
    }

    public float getBackend() {
        return Backend;
    }

    public void setBackend(final float backend) {
        Backend = backend;
    }

    public float getDesign() {
        return Design;
    }

    public void setDesign(final float design) {
        Design = design;
    }

    public float getPM() {
        return PM;
    }

    public void setPM(final float PM) {
        this.PM = PM;
    }

    public float getQA() {
        return QA;
    }

    public void setQA(final float QA) {
        this.QA = QA;
    }

    public float getOther() {
        return Other;
    }

    public void setOther(final float other) {
        Other = other;
    }

    public ProjectInfo(Object object) {
        if(object instanceof HashMap) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            try {
                iOS = (Float) hashMap.get("iOS");
            } catch (Exception e) {
                iOS = (Integer) hashMap.get("iOS");
            }
            try {
                Android = (Float) hashMap.get("Android");
            } catch (Exception e) {
                Android = (Integer) hashMap.get("Android");
            }
            try {
                Backend = (Float) hashMap.get("Backend");
            } catch (Exception e) {
                Backend = (Integer) hashMap.get("Backend");
            }
            try {
                Design = (Float) hashMap.get("Design");
            } catch (Exception e) {
                Design = (Integer) hashMap.get("Design");
            }
            try {
                PM = (Float) hashMap.get("PM");
            } catch (Exception e) {
                PM = (Integer) hashMap.get("PM");
            }
            try {
                QA = (Float) hashMap.get("QA");
            } catch (Exception e) {
                QA = (Integer) hashMap.get("QA");
            }
            try {
                Other = (Float) hashMap.get("Other");
            } catch (Exception e) {
                Other = (Integer) hashMap.get("Other");
            }
            users = getUsersList((List<HashMap<String, Object>>) hashMap.get("Users"));
        }
    }

    private List<UserStatistic> getUsersList(final List<HashMap<String, Object>> users) {
        List<UserStatistic> list = new ArrayList<>();
        for (HashMap<String, Object> item : users) {
            float time;
            try {
                time = (Float) item.get("time");
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
        dest.writeFloat(this.iOS);
        dest.writeFloat(this.Android);
        dest.writeFloat(this.Backend);
        dest.writeFloat(this.Design);
        dest.writeFloat(this.PM);
        dest.writeFloat(this.QA);
        dest.writeFloat(this.Other);
        dest.writeTypedList(this.users);
    }

    protected ProjectInfo(Parcel in) {
        this.iOS = in.readFloat();
        this.Android = in.readFloat();
        this.Backend = in.readFloat();
        this.Design = in.readFloat();
        this.PM = in.readFloat();
        this.QA = in.readFloat();
        this.Other = in.readFloat();
        this.users = in.createTypedArrayList(UserStatistic.CREATOR);
    }

    public static final Creator<ProjectInfo> CREATOR = new Creator<ProjectInfo>() {
        @Override
        public ProjectInfo createFromParcel(Parcel source) {return new ProjectInfo(source);}

        @Override
        public ProjectInfo[] newArray(int size) {return new ProjectInfo[size];}
    };
}
