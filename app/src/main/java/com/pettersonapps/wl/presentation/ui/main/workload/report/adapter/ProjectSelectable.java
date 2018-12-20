package com.pettersonapps.wl.presentation.ui.main.workload.report.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ruslan Lyalko
 * on 22.09.2018.
 */
public class ProjectSelectable implements Parcelable {

    private String title;
    private float spent = 8;

    public ProjectSelectable() {
    }

    public ProjectSelectable(final String title) {
        this.title = title;
    }

    public ProjectSelectable(final String title, final float spent) {
        this.title = title;
        this.spent = spent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public float getSpent() {
        return spent;
    }

    public void setSpent(final float spent) {
        this.spent = spent;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeFloat(this.spent);
    }

    protected ProjectSelectable(Parcel in) {
        this.title = in.readString();
        this.spent = in.readFloat();
    }

    public static final Creator<ProjectSelectable> CREATOR = new Creator<ProjectSelectable>() {
        @Override
        public ProjectSelectable createFromParcel(Parcel source) {return new ProjectSelectable(source);}

        @Override
        public ProjectSelectable[] newArray(int size) {return new ProjectSelectable[size];}
    };
}
