package com.pettersonapps.wl.data;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface DataManager {

    //Users
    Task<Void> saveUser(User user);

    MutableLiveData<User> getMyUser();

    MutableLiveData<User> getUser(String key);

    MutableLiveData<List<User>> getUsers();

    MutableLiveData<List<User>> getAllUsersWithoutReports(Date date);

    Task<Void> changePassword(String newPassword);

    void updateToken();

    void logout();
    // Holidays

    MutableLiveData<List<Holiday>> getHolidays();

    Task<Void> saveHoliday(Holiday holiday);
    //projects

    Task<ProjectInfo> getProjectInfo(String project, Date from, Date to);

    Task<Void> saveProject(Project project);

    MutableLiveData<List<Project>> getProjects();
    //reports

    Task<Void> saveReport(Report newReport);

    Task<Void> removeReport(Report report);

    MutableLiveData<List<Report>> getAllMyReports();

    MutableLiveData<List<Report>> getReportsFilter(Date from, Date to, String project, String user, final String status);

    MutableLiveData<List<Report>> getAllWrongReports(Date date);

    MutableLiveData<List<Report>> getVacationReports(final User user);
}
