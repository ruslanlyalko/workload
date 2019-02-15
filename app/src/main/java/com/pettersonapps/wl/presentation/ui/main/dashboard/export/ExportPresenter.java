package com.pettersonapps.wl.presentation.ui.main.dashboard.export;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ExportPresenter extends BasePresenter<ExportView> {

    public static final String KEY_USER = "User";
    public static final String KEY_PROJECT = "Project";
    private Date mFrom;
    private Date mTo;
    private boolean mOpen;
    private boolean mRepairReports;
    private String mProject = KEY_PROJECT;
    private String mUser = KEY_USER;

    ExportPresenter() {
    }

    public void onViewReady() {
        mFrom = DateUtils.getFirstDateOfMonth(new Date());
        mTo = DateUtils.getYesterday().getTime();
        getView().showFrom(mFrom);
        getView().showTo(mTo);
        getView().showSpinnerProjectsData(getDataManager().getAllProjects());
        getView().showSpinnerUsersData(getDataManager().getAllUsers());
    }

    void setRepairReports(final boolean repairReports) {
        mRepairReports = repairReports;
    }

    void onExportClicked(final boolean open) {
        mOpen = open;
        getView().showProgress();
        getView().showExportedData(getDataManager().getReportsFilter(mFrom, mTo));
    }

    void setExportedData(final List<Report> list) {
        List<Report> result = new ArrayList<>();
        for (Report report : list) {
            if(mRepairReports) {
                int h = Integer.parseInt(DateUtils.toString(report.getDateConverted(), "HH"));
                if(h < 4) {
                    report.setDateConverted(DateUtils.getDate(report.getDateConverted(), 10, 10));
                    report.setUpdatedAtConverted(new Date());
                    getDataManager().saveReport(report);
                }
                mRepairReports = false;
            }
            if(mProject.equals(KEY_PROJECT) || mProject.equals(report.getP1()) || mProject.equals(report.getP2()) || mProject.equals(report.getP3())
                    || mProject.equals(report.getP4()) || mProject.equals(report.getP5()) || mProject.equals(report.getP6())) {
                if(mUser.equals(KEY_USER) || mUser.equals(report.getUserName())) {
                    result.add(getNormalizedReport(report));
                }
            }
        }
        export(result);
    }

    private Report getNormalizedReport(final Report report) {
        if(mProject.equals(KEY_PROJECT)) return report;
        if(mProject.equals(report.getP1()))
            return report;
        Report result = new Report(report);
        result.setDate(report.getDate());
        result.setUpdatedAt(report.getUpdatedAt());
        if(mProject.equals(report.getP2())) {
            result.setP1(report.getP2());
            result.setT1(report.getT2());
            result.setP2(report.getP1());
            result.setT2(report.getT1());
        } else if(mProject.equals(report.getP3())) {
            result.setP1(report.getP3());
            result.setT1(report.getT3());
            result.setP3(report.getP1());
            result.setT3(report.getT1());
        } else if(mProject.equals(report.getP4())) {
            result.setP1(report.getP4());
            result.setT1(report.getT4());
            result.setP4(report.getP1());
            result.setT4(report.getT1());
        } else if(mProject.equals(report.getP5())) {
            result.setP1(report.getP5());
            result.setT1(report.getT5());
            result.setP5(report.getP1());
            result.setT5(report.getT1());
        } else if(mProject.equals(report.getP6())) {
            result.setP1(report.getP6());
            result.setT1(report.getT6());
            result.setP6(report.getP1());
            result.setT6(report.getT1());
        }
        return result;
    }

    private void export(final List<Report> list) {
        String fileName = "";
        if(!mProject.equals(KEY_PROJECT))
            fileName += mProject + "_";
        if(!mUser.equals(KEY_USER))
            fileName += mUser + "_";
        fileName += DateUtils.toString(mFrom, "yyMMdd_") + DateUtils.toString(mTo, "_yyMMdd");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(fileName);
        HSSFRow row0 = sheet.createRow(0);
        HSSFCell cell00_date = row0.createCell(0);
        HSSFCell cell01_user = row0.createCell(1);
        HSSFCell cell02_department = row0.createCell(2);
        HSSFCell cell03_status = row0.createCell(3);
        HSSFCell cell04_p1 = row0.createCell(4);
        HSSFCell cell05_t1 = row0.createCell(5);
        HSSFCell cell06_p2 = row0.createCell(6);
        HSSFCell cell07_t2 = row0.createCell(7);
        HSSFCell cell08_p3 = row0.createCell(8);
        HSSFCell cell09_t3 = row0.createCell(9);
        HSSFCell cell010_p4 = row0.createCell(10);
        HSSFCell cell011_t4 = row0.createCell(11);
        HSSFCell cell012_p5 = row0.createCell(12);
        HSSFCell cell013_t5 = row0.createCell(13);
        HSSFCell cell014_p6 = row0.createCell(14);
        HSSFCell cell015_t6 = row0.createCell(15);
        cell00_date.setCellValue("Date");
        cell01_user.setCellValue("User");
        cell02_department.setCellValue("Department");
        cell03_status.setCellValue("Status");
        cell04_p1.setCellValue("Project 1");
        cell05_t1.setCellValue("Time 1");
        cell06_p2.setCellValue("Project 2");
        cell07_t2.setCellValue("Time 2");
        cell08_p3.setCellValue("Project 3");
        cell09_t3.setCellValue("Time 3");
        cell010_p4.setCellValue("Project 4");
        cell011_t4.setCellValue("Time 4");
        cell012_p5.setCellValue("Project 5");
        cell013_t5.setCellValue("Time 5");
        cell014_p6.setCellValue("Project 6");
        cell015_t6.setCellValue("Time 6");
        //export
        for (int i = 0; i < list.size(); i++) {
            Report report = list.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(DateUtils.toString(report.getDateConverted(), "dd.MM.yyyy"));
            row.createCell(1).setCellValue(report.getUserName());
            row.createCell(2).setCellValue(report.getUserDepartment());
            row.createCell(3).setCellValue(report.getStatus());
            row.createCell(4).setCellValue(report.getP1());
            row.createCell(5).setCellValue(report.getT1());
            row.createCell(6).setCellValue(report.getP2());
            row.createCell(7).setCellValue(report.getT2());
            row.createCell(8).setCellValue(report.getP3());
            row.createCell(9).setCellValue(report.getT3());
            row.createCell(10).setCellValue(report.getP4());
            row.createCell(11).setCellValue(report.getT4());
            row.createCell(12).setCellValue(report.getP5());
            row.createCell(13).setCellValue(report.getT5());
            row.createCell(14).setCellValue(report.getP6());
            row.createCell(15).setCellValue(report.getT6());
        }
        saveToStorage(workbook, fileName, getJson(list));
    }

    private String getJson(final List<Report> list) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        Gson gson = new GsonBuilder().create();
        boolean addComma = false;
        String item;
        for (Report r : list) {
            if(addComma) {
                result.append(",\n");
            }
            addComma = true;
            item = "\"" + r.getKey() + "\" : " + gson.toJson(r);
            result.append(item);
        }
        return result + "}";
    }

    private void saveToStorage(final HSSFWorkbook workbook, String fileName, final String data) {
        String fullFileNameXls = fileName + ".xls";
        String fullFileNameJson = fileName + ".json";
        FileOutputStream fos = null;
        FileOutputStream fosJson = null;
        File file = null;
        File fileJson = null;
        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/Workload";
            File directory = new File(path);
            if(!directory.exists()) {
                if(!directory.mkdir()) {
                    throw new IOException("Can't create directory!");
                }
            }
            file = new File(path, fullFileNameXls);
            fos = new FileOutputStream(file);
            workbook.write(fos);
            //write json data
            fileJson = new File(path, fullFileNameJson);
            fosJson = new FileOutputStream(fileJson);
            fosJson.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fosJson != null) {
                try {
                    fosJson.flush();
                    fosJson.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            getView().hideProgress();
            getView().showMessage("Exported to " + "/Workload/" + fullFileNameXls);
            if(mOpen)
                getView().showFile(file);
        }
    }

    public Calendar getFrom() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mFrom);
        return calendar;
    }

    public void setFrom(final Date from) {
        mFrom = from;
        if(mTo.before(mFrom)) {
            mTo = mFrom;
            getView().showTo(mTo);
        }
    }

    public Calendar getTo() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTo);
        return calendar;
    }

    public void setTo(final Date to) {
        mTo = to;
        if(mTo.before(mFrom)) {
            mFrom = mTo;
            getView().showFrom(mFrom);
        }
    }

    void setFilter(final String project, final String user) {
        mProject = project;
        mUser = user;
    }
}
