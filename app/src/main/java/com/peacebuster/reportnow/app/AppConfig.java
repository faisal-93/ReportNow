package com.peacebuster.reportnow.app;

/**
 * Created by Sunny on 10/29/2016.
 */

public class AppConfig {
    // Server user login url
    public static String URL_LOGIN = "http://192.168.1.54:8081/android_login_api/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://192.168.1.54:8081/android_login_api/register.php";

    // Server user update url
    public static String URL_UPDATE = "http://192.168.1.54:8081/android_login_api/update.php";

    // Server user posting url
    public static String URL_POST = "http://192.168.1.54:8081/android_login_api/post.php";

    // Server get user posts url
    public String UrlGetAllPost(String location, String category, String from_date, String to_date){

        String url = String.format("http://192.168.0.102:8080/android_login_api/getPost.php?location=%1$s&category=%2$s&from_date=%3$s&to_date=%4$s", location, category, from_date, to_date);
        return url;
    }

    public String UrlGetUserPost(String uid){
        String url = String.format("http://192.168.0.102:8080/android_login_api/getPost.php?user_id=%1$s", uid);
        return url;
    }

    public String FilterPost(String location, String date_from, String date_to){
        String url = String.format("http://192.168.0.102:8080/android_login_api/test.php?location=%1$s&from_date=%2$s&to_date=%3$s", location, date_from, date_to);
        return url;
    }
}