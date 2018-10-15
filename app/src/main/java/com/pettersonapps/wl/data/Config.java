package com.pettersonapps.wl.data;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface Config {

    String DB_USERS = "USERS";
    String DB_REPORTS = "REPORTS";
    String DB_PROJECTS = "PROJECTS";
    String DB_HOLIDAYS = "HOLIDAYS";
    //
    String FIELD_TITLE = "title";
    String FIELD_NAME = "name";
    String FIELD_DATE_TIME = "date/time";
    String FIELD_USER_ID = "userId";
    String FIELD_TOKEN = "token";
    String FIELD_ALLOW_TO_EDIT = "isAllowEditPastReports";
    //
    String DATABASE_URL = "https://paworkload01.firebaseio.com/";
    String API_KEY = "AIzaSyD-WLO5002WFK0r1nlaBllaRteUr-Xum0U";
    String APP_ID = "paworkload01";
    String APP_NAME = "Workload2";
}
