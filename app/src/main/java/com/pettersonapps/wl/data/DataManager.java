package com.pettersonapps.wl.data;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.pettersonapps.wl.data.models.AppSettings;
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

    MutableLiveData<List<User>> getAllUsers();

    MutableLiveData<List<User>> getAllUsersWithoutReports(List<User> users, Date date);

    Task<Void> changePassword(String newPassword);

    void updateToken();

    void updateRemindMeAt(final String remindMeAt);

    void updateDefaultWorkingTime(final int defaultWorkingTime);

    void updateVersion();

    void updateNightMode(final boolean isNightMode);

    void logout();

    void clearCache();
    // Holidays

    MutableLiveData<List<Holiday>> getAllHolidays();

    Task<Void> saveHoliday(Holiday holiday);

    Task<Void> deleteHoliday(Holiday holiday);
    //projects

    Task<ProjectInfo> getProjectInfo(String project, Date from, Date to);

    Task<Void> saveProject(Project project);

    Task<Void> deleteProject(Project project);

    MutableLiveData<List<Project>> getAllProjects();
    //reports

    Task<Void> saveReport(Report newReport);

    Task<Void> removeReport(Report report);

    MutableLiveData<List<Report>> getAllMyReports();

    MutableLiveData<List<Report>> getReportsFilter(Date from, Date to);

    MutableLiveData<List<Report>> getAllWrongReports(Date date);

    MutableLiveData<List<Report>> getVacationReports(final User user);

    MutableLiveData<AppSettings> getSettings();

    Task<Void> setSettings(AppSettings settings);
}
