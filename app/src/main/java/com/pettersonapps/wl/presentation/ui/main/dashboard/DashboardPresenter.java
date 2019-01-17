package com.pettersonapps.wl.presentation.ui.main.dashboard;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class DashboardPresenter extends BasePresenter<DashboardView> {

    public static final String KEY_USER = "User";
    public static final String KEY_PROJECT = "Project";
    private static final String KEY_STATUS = "Status";
    private List<Report> mReports = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private Date mDate = new Date();
    private String mProject = "Project";
    private String mUser = "User";
    private String mStatus = "Status";
    private ArrayList<User> mFilteredUsers = new ArrayList<>();
    private List<String> mLoadedMonths = new ArrayList<>();

    DashboardPresenter() {
    }

    public void onViewReady() {
        getView().showSpinnerProjectsData(getDataManager().getAllProjects());
        getView().showSpinnerUsersData(getDataManager().getAllUsers());
        fetchReportsForMonth(DateUtils.getFirstDateOfMonth(new Date()), DateUtils.getLastDateOfMonth(new Date()));
    }

    public void fetchReportsForMonth(final Date from, final Date to) {
        String month = DateUtils.toString(from, "MMyyyy");
        if (mLoadedMonths.contains(month)) {
            showFilteredReports();
            return;
        }
        mLoadedMonths.add(month);
        getView().showReports(getDataManager().getReportsFilter(from, to));
    }

    public void setReports(final List<Report> reports) {
        mReports.addAll(reports);
        showFilteredReports();
    }

    public void fetchReports(final Date date) {
        mDate = date;
        showFilteredReports();
    }

    public void setFilter(final String project, final String user, final String status) {
        boolean needReload = false;
        if (!mProject.equals(project)) {
            mProject = project;
            needReload = true;
        }
        if (!mUser.equals(user)) {
            mUser = user;
            needReload = true;
        }
        if (!mStatus.equals(status)) {
            mStatus = status;
            needReload = true;
        }
        if (needReload)
            showFilteredReports();
    }

    private void showFilteredReports() {
        Observable.fromCallable(this::filterReportsBackground)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<FilterResult>() {

                    @Override
                    public void onNext(final FilterResult result) {
                        if (getView() == null) return;
                        getView().showReportsOnCalendar(result.allReports);
                        getView().showReportsOnList(result.todayReports);
                        getView().showUsersWithoutReports(result.todayUsers);
                    }

                    @Override
                    public void onError(final Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private FilterResult filterReportsBackground() {
        Date from = DateUtils.getStart(mDate);
        Date to = DateUtils.getEnd(mDate);
        List<Report> allFilteredReports = new ArrayList<>();
        List<Report> todayFilteredReports = new ArrayList<>();
        List<Report> todayReports = new ArrayList<>();
        List<User> usersWithoutAnyReports = new ArrayList<>(mFilteredUsers);
        for (Report report : mReports) {
            if (report.getDate().after(from) && report.getDate().before(to))
                todayReports.add(report);
            if (!mUser.startsWith(KEY_USER) && !mUser.equalsIgnoreCase(report.getUserName())) {
                continue;
            }
            if (!mStatus.startsWith(KEY_STATUS) && !mStatus.equalsIgnoreCase(report.getStatus())) {
                continue;
            }
            if (!mProject.startsWith(KEY_PROJECT)
                    && !((mProject.equalsIgnoreCase(report.getP1()) && report.getT1() > 0)
                    || ((mProject.equalsIgnoreCase(report.getP2()) && report.getT2() > 0))
                    || ((mProject.equalsIgnoreCase(report.getP3()) && report.getT3() > 0))
                    || ((mProject.equalsIgnoreCase(report.getP4()) && report.getT4() > 0))
                    || ((mProject.equalsIgnoreCase(report.getP5()) && report.getT5() > 0))
                    || ((mProject.equalsIgnoreCase(report.getP6())) && report.getT6() > 0))) {
                continue;
            }
            allFilteredReports.add(report);
            if (report.getDate().after(from) && report.getDate().before(to))
                todayFilteredReports.add(report);
        }
        for (Report report : todayReports) {
            for (int i = 0; i < usersWithoutAnyReports.size(); i++) {
                if (usersWithoutAnyReports.get(i).getKey().equals(report.getUserId())) {
                    usersWithoutAnyReports.remove(usersWithoutAnyReports.get(i));
                }
            }
        }
        return new FilterResult(allFilteredReports, todayFilteredReports, usersWithoutAnyReports);
    }

    public void onReportClicked(final String userId) {
        for (User user : mUsers) {
            if (user.getKey().equalsIgnoreCase(userId)) {
                getView().showUserDetails(user);
                break;
            }
        }
    }

    public void setUsers(final List<User> users) {
        mUsers = users;
        mFilteredUsers = new ArrayList<>();
        for (User user : mUsers) {
            if (user != null && !user.getIsAdmin() && !user.getIsBlocked() && !user.getIsVip())
                mFilteredUsers.add(user);
        }
    }

    class FilterResult {

        public List<Report> allReports;
        public List<Report> todayReports;
        public List<User> todayUsers;

        public FilterResult() {
        }

        FilterResult(final List<Report> allReports, final List<Report> todayReports, final List<User> todayUsers) {
            this.allReports = allReports;
            this.todayReports = todayReports;
            this.todayUsers = todayUsers;
        }
    }
}
