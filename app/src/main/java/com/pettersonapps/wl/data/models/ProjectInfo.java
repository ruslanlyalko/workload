package com.pettersonapps.wl.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class ProjectInfo implements Parcelable {

    private int iOS;
    private int Android;
    private int Backend;
    private int Design;
    private int PM;
    private int QA;
    private int Other;

    public ProjectInfo() {
    }

    @Override
    public String toString() {
        return "iOS = " + iOS + "h;   Android = " + Android + "h" +
                "\nBackend = " + Backend + "h;   Design = " + Design + "h" +
                "\nPM = " + PM + "h;   QA = " + QA + "h;   Other = " + Other + "h";
    }

    public int getiOS() {
        return iOS;
    }

    public void setiOS(final int iOS) {
        this.iOS = iOS;
    }

    public int getAndroid() {
        return Android;
    }

    public void setAndroid(final int android) {
        Android = android;
    }

    public int getBackend() {
        return Backend;
    }

    public void setBackend(final int backend) {
        Backend = backend;
    }

    public int getDesign() {
        return Design;
    }

    public void setDesign(final int design) {
        Design = design;
    }

    public int getPM() {
        return PM;
    }

    public void setPM(final int PM) {
        this.PM = PM;
    }

    public int getQA() {
        return QA;
    }

    public void setQA(final int QA) {
        this.QA = QA;
    }

    public int getOther() {
        return Other;
    }

    public void setOther(final int other) {
        Other = other;
    }

    public ProjectInfo(Object object) {
        if(object instanceof HashMap) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
            iOS = (Integer) hashMap.get("iOS");
            Android = (Integer) hashMap.get("Android");
            Backend = (Integer) hashMap.get("Backend");
            Design = (Integer) hashMap.get("Design");
            PM = (Integer) hashMap.get("PM");
            QA = (Integer) hashMap.get("QA");
            Other = (Integer) hashMap.get("Other");
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.iOS);
        dest.writeInt(this.Android);
        dest.writeInt(this.Backend);
        dest.writeInt(this.Design);
        dest.writeInt(this.PM);
        dest.writeInt(this.QA);
        dest.writeInt(this.Other);
    }

    protected ProjectInfo(Parcel in) {
        this.iOS = in.readInt();
        this.Android = in.readInt();
        this.Backend = in.readInt();
        this.Design = in.readInt();
        this.PM = in.readInt();
        this.QA = in.readInt();
        this.Other = in.readInt();
    }

    public static final Creator<ProjectInfo> CREATOR = new Creator<ProjectInfo>() {
        @Override
        public ProjectInfo createFromParcel(Parcel source) {return new ProjectInfo(source);}

        @Override
        public ProjectInfo[] newArray(int size) {return new ProjectInfo[size];}
    };
}
