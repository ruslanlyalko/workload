package com.pettersonapps.wl.data.models;

import android.os.Parcel;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class Project extends BaseModel {

    private String title;
    private Date createdAt;

    public Project() {
        createdAt = new Date();
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
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        if (!super.equals(o)) return false;
        Project project = (Project) o;
        return Objects.equals(getTitle(), project.getTitle()) &&
                Objects.equals(createdAt, project.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle(), createdAt);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected Project(Parcel in) {
        super(in);
        this.title = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel source) {return new Project(source);}

        @Override
        public Project[] newArray(int size) {return new Project[size];}
    };
}
