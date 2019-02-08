package com.pettersonapps.wl.data;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pettersonapps.wl.BuildConfig;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.CheckBlocked;
import com.pettersonapps.wl.data.models.CheckDate;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pettersonapps.wl.data.Config.DB_HOLIDAYS;
import static com.pettersonapps.wl.data.Config.DB_PROJECTS;
import static com.pettersonapps.wl.data.Config.DB_REPORTS;
import static com.pettersonapps.wl.data.Config.DB_SETTINGS;
import static com.pettersonapps.wl.data.Config.DB_USERS;
import static com.pettersonapps.wl.data.Config.FIELD_DATE_TIME;
import static com.pettersonapps.wl.data.Config.FIELD_DEFAULT_WORKING_TIME;
import static com.pettersonapps.wl.data.Config.FIELD_IS_NIGHT_MODE;
import static com.pettersonapps.wl.data.Config.FIELD_IS_OLD_STYLE_CALENDAR;
import static com.pettersonapps.wl.data.Config.FIELD_NAME;
import static com.pettersonapps.wl.data.Config.FIELD_P1;
import static com.pettersonapps.wl.data.Config.FIELD_P2;
import static com.pettersonapps.wl.data.Config.FIELD_P3;
import static com.pettersonapps.wl.data.Config.FIELD_P4;
import static com.pettersonapps.wl.data.Config.FIELD_P5;
import static com.pettersonapps.wl.data.Config.FIELD_P6;
import static com.pettersonapps.wl.data.Config.FIELD_REMIND_ME_AT;
import static com.pettersonapps.wl.data.Config.FIELD_TITLE;
import static com.pettersonapps.wl.data.Config.FIELD_TOKEN;
import static com.pettersonapps.wl.data.Config.FIELD_TOKENS;
import static com.pettersonapps.wl.data.Config.FIELD_USER_ID;
import static com.pettersonapps.wl.data.Config.FIELD_VERSION;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class DataManagerImpl implements DataManager {

    private static final String TAG = "DataManager";
    private static DataManagerImpl mInstance;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseFunctions mFunctions;
    private MutableLiveData<User> mCurrentUserLiveData;
    private MutableLiveData<List<Report>> mAllMyReportsListMutableLiveData;
    private MutableLiveData<List<User>> mAllUsersListLiveData;
    private MutableLiveData<List<Project>> mAllProjectsListMutableLiveData;
    private MutableLiveData<List<Holiday>> mHolidaysListMutableLiveData;
    private MutableLiveData<AppSettings> mSettingsLiveData;

    private DataManagerImpl() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
    }

    public static DataManager newInstance() {
        if(mInstance == null)
            mInstance = new DataManagerImpl();
        return mInstance;
    }

    @Override
    public Task<Void> saveUser(final User user) {
        if(user.getKey() == null) {
            throw new RuntimeException("user can't be empty");
        }
        return mDatabase.getReference(DB_USERS)
                .child(user.getKey())
                .updateChildren(user.toMap());
    }

    @Override
    public MutableLiveData<User> getMyUser() {
        if(mCurrentUserLiveData != null) return mCurrentUserLiveData;
        String key = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if(TextUtils.isEmpty(key)) {
            Log.w(TAG, "getMyUser: user is not logged in");
            return mCurrentUserLiveData;
        }
        mCurrentUserLiveData = new MutableLiveData<>();
        mDatabase.getReference(DB_USERS)
                .child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snap) {
                        Log.d(TAG, "getMyUser:onDataChange, Key:" + key);
                        if(mCurrentUserLiveData != null) {
                            User user = snap.getValue(User.class);
                            if(user != null) {
                                long tokensCount = snap.child(FIELD_TOKENS).getChildrenCount();
                                if(tokensCount > 0)
                                    user.setToken(String.valueOf(tokensCount));
                                mCurrentUserLiveData.postValue(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mCurrentUserLiveData;
    }

    @Override
    public MutableLiveData<User> getUser(final String key) {
        final MutableLiveData<User> userLiveData = new MutableLiveData<>();
        if(TextUtils.isEmpty(key)) {
            Log.w(TAG, "getUser has wrong argument");
            return userLiveData;
        }
        mDatabase.getReference(DB_USERS)
                .child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snap) {
                        Log.d(TAG, "getUser:onDataChange, Key:" + key);
                        User user = snap.getValue(User.class);
                        if(user != null) {
                            long tokensCount = snap.child(FIELD_TOKENS).getChildrenCount();
                            if(tokensCount > 0)
                                user.setToken(String.valueOf(tokensCount));
                            userLiveData.postValue(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return userLiveData;
    }

    @Override
    public MutableLiveData<List<User>> getAllUsers() {
        if(mAllUsersListLiveData != null) return mAllUsersListLiveData;
        mAllUsersListLiveData = new MutableLiveData<>();
        mDatabase.getReference(DB_USERS)
                .orderByChild(FIELD_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllUsers:onDataChange");
                        List<User> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            try {
                                User user = snap.getValue(User.class);
                                if(user != null) {
                                    long tokensCount = snap.child(FIELD_TOKENS).getChildrenCount();
                                    if(tokensCount > 0)
                                        user.setToken(String.valueOf(tokensCount));
                                    list.add(user);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("DB ERROR USER ", snap.getKey());
                            }
                        }
                        mAllUsersListLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mAllUsersListLiveData;
    }

    @Override
    public Task<Void> changePassword(final String newPassword) {
        if(mAuth.getCurrentUser() == null) return null;
        return mAuth.getCurrentUser().updatePassword(newPassword);
    }

    @Override
    public void updateRemindMeAt(final String remindMeAt) {
        if(mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_REMIND_ME_AT)
                .setValue(remindMeAt);
    }

    @Override
    public void updateDefaultWorkingTime(final int defaultWorkingTime) {
        if(mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_DEFAULT_WORKING_TIME)
                .setValue(defaultWorkingTime);
    }

    @Override
    public void updateOldStyleCalendar(final boolean isEnabled) {
        if(mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_IS_OLD_STYLE_CALENDAR)
                .setValue(isEnabled);
    }

    @Override
    public void updateVersion() {
        if(mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_VERSION)
                .setValue(BuildConfig.VERSION_NAME);
    }

    @Override
    public void updateNightMode(final boolean isNightMode) {
        if(mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_IS_NIGHT_MODE)
                .setValue(isNightMode);
    }

    @Override
    public void updateToken() {
        if(mAuth.getCurrentUser() == null) return;
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token != null && !TextUtils.isEmpty(token))
            mDatabase.getReference(DB_USERS)
                    .child(mAuth.getCurrentUser().getUid())
                    .child(FIELD_TOKENS)
                    .child(token)
                    .setValue(true);
    }

    @Override
    public void logout() {
        if(mAuth.getCurrentUser() == null) return;
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token != null && !TextUtils.isEmpty(token))
            mDatabase.getReference(DB_USERS)
                    .child(mAuth.getCurrentUser().getUid())
                    .child(FIELD_TOKENS)
                    .child(token)
                    .removeValue();
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(FIELD_TOKEN)
                .removeValue();
        mCurrentUserLiveData = null;
        mAllMyReportsListMutableLiveData = null;
        mAuth.signOut();
    }

    @Override
    public void clearCache() {
        mCurrentUserLiveData = null;
        mAllMyReportsListMutableLiveData = null;
    }

    @Override
    public MutableLiveData<List<Holiday>> getAllHolidays() {
        if(mHolidaysListMutableLiveData != null) return mHolidaysListMutableLiveData;
        mHolidaysListMutableLiveData = new MutableLiveData<>();
        mDatabase.getReference(DB_HOLIDAYS)
                .orderByChild(FIELD_DATE_TIME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllHolidays:onDataChange");
                        List<Holiday> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            list.add(0, snap.getValue(Holiday.class));
                        }
                        mHolidaysListMutableLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mHolidaysListMutableLiveData;
    }

    @Override
    public Task<Void> saveHoliday(final Holiday holiday) {
        if(!TextUtils.isEmpty(holiday.getKey())) {
            mDatabase.getReference(DB_HOLIDAYS)
                    .child(holiday.getKey())
                    .removeValue();
        }
        holiday.setKey(DateUtils.toStringHolidayKey(holiday.getDate()));
        return mDatabase.getReference(DB_HOLIDAYS)
                .child(holiday.getKey())
                .setValue(holiday);
    }

    @Override
    public Task<Void> deleteHoliday(final Holiday holiday) {
        return mDatabase.getReference(DB_HOLIDAYS)
                .child(holiday.getKey())
                .removeValue();
    }

    @Override
    public Task<ProjectInfo> getProjectInfo(final String project, final Date from, final Date to) {
        Map<String, Object> data = new HashMap<>();
        data.put("project", project);
        data.put("from", DateUtils.getStart(from).getTime());
        data.put("to", DateUtils.getEnd(to).getTime());
        return mFunctions
                .getHttpsCallable("getProjectInfo")
                .call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        try {
                            return new ProjectInfo(task.getResult().getData());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                });
    }

    @Override
    public Task<CheckBlocked> isBlocked() {
        Map<String, Object> data = new HashMap<>();
        return mFunctions
                .getHttpsCallable("isBlocked")
                .call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        try {
                            return new CheckBlocked(task.getResult().getData());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                });
    }

    @Override
    public Task<CheckDate> isRightDate() {
        Map<String, Object> data = new HashMap<>();
        data.put("date", new Date().getTime());
        return mFunctions
                .getHttpsCallable("isRightDate")
                .call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if(task.isSuccessful()) {
                        try {
                            return new CheckDate(task.getResult().getData());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    return null;
                });
    }

    @Override
    public Task<Void> saveProject(final Project project) {
        if(project.getKey() == null) {
            project.setKey(mDatabase.getReference(DB_PROJECTS).push().getKey());
        }
        return mDatabase.getReference(DB_PROJECTS)
                .child(project.getKey())
                .setValue(project);
    }

    @Override
    public Task<Void> deleteProject(final Project project) {
        return mDatabase.getReference(DB_PROJECTS)
                .child(project.getKey())
                .removeValue();
    }

    @Override
    public MutableLiveData<List<Project>> getAllProjects() {
        if(mAllProjectsListMutableLiveData != null) return mAllProjectsListMutableLiveData;
        mAllProjectsListMutableLiveData = new MutableLiveData<>();
        mDatabase.getReference(DB_PROJECTS)
                .orderByChild(FIELD_TITLE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllProjects:onDataChange");
                        List<Project> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            list.add(snap.getValue(Project.class));
                        }
                        mAllProjectsListMutableLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mAllProjectsListMutableLiveData;
    }

    @Override
    public Task<Void> saveReport(final Report newReport) {
        return mDatabase.getReference(DB_REPORTS)
                .child(newReport.getKey())
                .setValue(newReport);
    }

    @Override
    public Task<Void> removeReport(final Report report) {
        return mDatabase.getReference(DB_REPORTS)
                .child(report.getKey())
                .removeValue();
    }

    @Override
    public MutableLiveData<List<Report>> getAllMyReports() {
        if(mAllMyReportsListMutableLiveData != null) return mAllMyReportsListMutableLiveData;
        String userId = mAuth.getUid();
        mAllMyReportsListMutableLiveData = new MutableLiveData<>();
        if(TextUtils.isEmpty(userId)) {
            Log.w(TAG, "getAllMyReports user is not logged in");
            return mAllMyReportsListMutableLiveData;
        }
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_USER_ID)
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllMyReports:onDataChange, userId:" + userId);
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Report report = snap.getValue(Report.class);
                            if(report != null)
                                list.add(report);
                        }
                        if(mAllMyReportsListMutableLiveData != null)
                            mAllMyReportsListMutableLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mAllMyReportsListMutableLiveData;
    }

    @Override
    public MutableLiveData<List<Report>> getAllReports(final String report) {
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        final List<Report> resultList = new ArrayList<>();
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_P1)
                .equalTo(report)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllReports:p1:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        resultList.addAll(list);
                        result.postValue(resultList);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_P2)
                .equalTo(report)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllReports:p2:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        resultList.addAll(list);
                        result.postValue(resultList);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_P3)
                .equalTo(report)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllReports:p1:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        resultList.addAll(list);
                        result.postValue(resultList);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_P4)
                .equalTo(report)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllReports:p2:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        resultList.addAll(list);
                        result.postValue(resultList);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_P5)
                .equalTo(report)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllReports:p1:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        resultList.addAll(list);
                        result.postValue(resultList);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_P6)
                .equalTo(report)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllReports:p2:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        resultList.addAll(list);
                        result.postValue(resultList);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return result;
    }

    @Override
    public MutableLiveData<List<Report>> getReportsFilter(final Date from, final Date to) {
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_DATE_TIME)
                .startAt(DateUtils.getEnd(DateUtils.get1DaysAgo(from)).getTime())
                .endAt(DateUtils.getEnd(to).getTime())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getReportsFilter:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) return;
                            list.add(report);
                        }
                        result.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return result;
    }

    @Override
    public MutableLiveData<List<Report>> getUserReports(final User user) {
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        mDatabase.getReference(DB_REPORTS)
                .orderByChild(FIELD_USER_ID)
                .equalTo(user.getKey())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getUserReports:onDataChange");
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if(report == null) continue;
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o1.getDateConverted().compareTo(o2.getDateConverted()));
                        result.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return result;
    }

    @Override
    public MutableLiveData<AppSettings> getSettings() {
        if(mSettingsLiveData != null) return mSettingsLiveData;
        mSettingsLiveData = new MutableLiveData<>();
        mDatabase.getReference(DB_SETTINGS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getSettings:onDataChange");
                        AppSettings value = dataSnapshot.getValue(AppSettings.class);
                        if(value == null)
                            value = new AppSettings();
                        if(mSettingsLiveData != null)
                            mSettingsLiveData.postValue(value);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return mSettingsLiveData;
    }

    @Override
    public Task<Void> setSettings(final AppSettings settings) {
        return mDatabase.getReference(DB_SETTINGS).setValue(settings);
    }

    private float getTotalHoursSpent(final Report report) {
        return report.getT1() + report.getT2() + report.getT3() + report.getT4();
    }
}
