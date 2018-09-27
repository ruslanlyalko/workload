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
import static com.pettersonapps.wl.data.Config.DB_USERS;
import static com.pettersonapps.wl.data.Config.FIELD_NAME;

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

    private DataManagerImpl() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
    }

    public static DataManager newInstance() {
        if (mInstance == null)
            mInstance = new DataManagerImpl();
        return mInstance;
    }

    @Override
    public Task<Void> saveUser(final User user) {
        if (user.getKey() == null) {
            throw new RuntimeException("user can't be empty");
        }
        return mDatabase.getReference(DB_USERS)
                .child(user.getKey())
                .setValue(user);
    }

    @Override
    public MutableLiveData<User> getMyUser() {
        return getUser(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null);
    }

    @Override
    public MutableLiveData<User> getUser(final String key) {
        final MutableLiveData<User> userLiveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(key)) {
            Log.w(TAG, "getUser has wrong argument");
            return userLiveData;
        }
        mDatabase.getReference(DB_USERS)
                .child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getUser:onDataChange, Key:" + key);
                        userLiveData.postValue(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return userLiveData;
    }

    @Override
    public MutableLiveData<List<User>> getUsers() {
        final MutableLiveData<List<User>> userLiveData = new MutableLiveData<>();
        mDatabase.getReference(DB_USERS)
                .orderByChild(FIELD_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getUsers:onDataChange");
                        List<User> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            list.add(snap.getValue(User.class));
                        }
                        userLiveData.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return userLiveData;
    }

    @Override
    public MutableLiveData<List<User>> getAllUsersWithoutReports(final Date date) {
        final MutableLiveData<List<User>> result = new MutableLiveData<>();
        mDatabase.getReference(DB_USERS)
                .orderByChild(FIELD_NAME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getUsers:onDataChange");
                        List<User> listUsers = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            User user = snap.getValue(User.class);
                            if (user != null && !user.getIsAdmin())
                                listUsers.add(user);
                        }
                        mDatabase.getReference(Config.DB_REPORTS)
                                .orderByChild(Config.FIELD_DATE_TIME)
                                .startAt(DateUtils.getStart(date).getTime())
                                .endAt(DateUtils.getEnd(date).getTime())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                                            Report report = snapReport.getValue(Report.class);
                                            if (report == null) return;
                                            for (int i = 0; i < listUsers.size(); i++) {
                                                if (listUsers.get(i).getKey().equals(report.getUserId())) {
                                                    listUsers.remove(i);
                                                    break;
                                                }
                                            }
                                        }
                                        result.postValue(listUsers);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return result;
    }

    @Override
    public void changePassword(final String newPassword) {
        if (mAuth.getCurrentUser() == null) return;
        mAuth.getCurrentUser().updatePassword(newPassword);
    }

    @Override
    public void updateToken() {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(Config.FIELD_TOKEN)
                .setValue(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void logout() {
        if (mAuth.getCurrentUser() == null) return;
        mDatabase.getReference(DB_USERS)
                .child(mAuth.getCurrentUser().getUid())
                .child(Config.FIELD_TOKEN)
                .removeValue();
        mAuth.signOut();
    }

    @Override
    public MutableLiveData<List<Holiday>> getHolidays() {
        final MutableLiveData<List<Holiday>> result = new MutableLiveData<>();
        mDatabase.getReference(Config.DB_HOLIDAYS)
                .orderByChild(Config.FIELD_DATE_TIME)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getHolidays:onDataChange");
                        List<Holiday> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            list.add(0, snap.getValue(Holiday.class));
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
    public Task<Void> saveHoliday(final Holiday holiday) {
        if (!TextUtils.isEmpty(holiday.getKey())) {
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
    public Task<ProjectInfo> getProjectInfo(final String project, final Date from, final Date to) {
        Map<String, Object> data = new HashMap<>();
        data.put("project", project);
        data.put("from", DateUtils.getStart(from).getTime());
        data.put("to", DateUtils.getEnd(to).getTime());
        return mFunctions
                .getHttpsCallable("getProjectInfo")
                .call(data)
                .continueWith((Task<HttpsCallableResult> task) -> {
                    if (task.isSuccessful()) {
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
    public Task<Void> saveProject(final Project project) {
        if (project.getKey() == null) {
            project.setKey(mDatabase.getReference(Config.DB_PROJECTS).push().getKey());
        }
        return mDatabase.getReference(Config.DB_PROJECTS)
                .child(project.getKey())
                .setValue(project);
    }

    @Override
    public MutableLiveData<List<Project>> getProjects() {
        final MutableLiveData<List<Project>> result = new MutableLiveData<>();
        mDatabase.getReference(Config.DB_PROJECTS)
                .orderByChild(Config.FIELD_TITLE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getProjects:onDataChange");
                        List<Project> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            list.add(snap.getValue(Project.class));
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
    public Task<Void> saveReport(final Report newReport) {
        return mDatabase.getReference(Config.DB_REPORTS)
                .child(newReport.getKey())
                .setValue(newReport);
    }

    @Override
    public Task<Void> removeReport(final Report report) {
        return mDatabase.getReference(Config.DB_REPORTS)
                .child(report.getKey())
                .removeValue();
    }

    @Override
    public MutableLiveData<List<Report>> getAllMyReports() {
        String userId = mAuth.getUid();
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        if (TextUtils.isEmpty(userId)) {
            Log.w(TAG, "getAllMyReports has wrong argument");
            return result;
        }
        mDatabase.getReference(Config.DB_REPORTS)
                .orderByChild(Config.FIELD_USER_ID)
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        Log.d(TAG, "getAllMyReports:onDataChange, userId:" + userId);
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Report report = snap.getValue(Report.class);
                            if (report != null)
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
    public MutableLiveData<List<Report>> getReportsFilter(final Date from, final Date to, final String project, final String userName, final String status) {
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        mDatabase.getReference(Config.DB_REPORTS)
                .orderByChild(Config.FIELD_DATE_TIME)
                .startAt(DateUtils.getStart(from).getTime())
                .endAt(DateUtils.getEnd(to).getTime())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if (report == null) return;
                            if (!userName.equals("-") && !userName.equalsIgnoreCase(report.getUserName())) {
                                continue;
                            }
                            if (!status.equals("-") && !status.equalsIgnoreCase(report.getStatus())) {
                                continue;
                            }
                            if (!project.equals("-")
                                    && !((project.equalsIgnoreCase(report.getP1()) && report.getT1() > 0)
                                    || ((project.equalsIgnoreCase(report.getP2()) && report.getT2() > 0)
                                    || ((project.equalsIgnoreCase(report.getP3()) && report.getT2() > 0)
                                    || ((project.equalsIgnoreCase(report.getP4())) && report.getT2() > 0))))) {
                                continue;
                            }
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
    public MutableLiveData<List<Report>> getAllWrongReports(final Date date) {
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        mDatabase.getReference(Config.DB_REPORTS)
                .orderByChild(Config.FIELD_DATE_TIME)
                .startAt(DateUtils.getStart(date).getTime())
                .endAt(DateUtils.getEnd(date).getTime())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if (report == null) return;
                            if (report.getStatus().startsWith("Worked") && getTotalHoursSpent(report) == 8) {
                                continue;
                            }
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
    public MutableLiveData<List<Report>> getVacationReports(final User user) {
        final MutableLiveData<List<Report>> result = new MutableLiveData<>();
        mDatabase.getReference(Config.DB_REPORTS)
                .orderByChild(Config.FIELD_USER_ID)
                .equalTo(user.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        List<Report> list = new ArrayList<>();
                        for (DataSnapshot snapReport : dataSnapshot.getChildren()) {
                            Report report = snapReport.getValue(Report.class);
                            if (report == null) continue;
                            if (report.getStatus().startsWith("Worked")) {
                                continue;
                            }
                            if (report.getStatus().startsWith("No project")) {
                                continue;
                            }
                            if (report.getStatus().startsWith("Other")) {
                                continue;
                            }
                            list.add(report);
                        }
                        Collections.sort(list, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                        result.postValue(list);
                    }

                    @Override
                    public void onCancelled(@NonNull final DatabaseError databaseError) {
                    }
                });
        return result;
    }

    private int getTotalHoursSpent(final Report report) {
        return report.getT1() + report.getT2() + report.getT3() + report.getT4();
    }
}
