package com.xfzj.qqzoneass.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.xfzj.qqzoneass.model.UserInfo;
import com.xfzj.qqzoneass.model.Weather;
import com.xfzj.qqzoneass.utils.Constants;
import com.xfzj.qqzoneass.utils.Key;

import java.util.Set;

/**
 * Created by zj on 2015/6/29.
 */
public class Config {

    /**
     * 存放用户的信息
     *
     * @param context
     * @param number
     * @param password
     */
    public static void saveUserInfo(Context context, String number, String password, String cookie, String skey, String sid) {
        SharedPreferences sp = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null != number && !"".equals(number))
            editor.putString(Constants.NUMBER, number);
        if (null != password && !"".equals(password))
            editor.putString(Constants.PASSWORD, password);
        if (null != cookie && !"".equals(cookie))
            editor.putString(Constants.COOKIE, cookie);
        if (null != skey && !"".equals(skey))
            editor.putString(Constants.GTK, Key.g_tk(skey));
        if (null != sid && !"".equals(sid))
            editor.putString(Constants.SID, sid);
        editor.commit();

    }

    /**
     * 存放用户的信息
     *
     * @param context
     * @param userInfo
     */
    public static void saveUserInfo(Context context, UserInfo userInfo) {
        saveUserInfo(context, userInfo.number, userInfo.password, userInfo.cookie, userInfo.gtk, userInfo.sid);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static UserInfo getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        UserInfo userInfo = new UserInfo();

        userInfo.number = sp.getString(Constants.NUMBER, "");

        userInfo.password = sp.getString(Constants.PASSWORD, "");
        userInfo.cookie = sp.getString(Constants.COOKIE, "");
        userInfo.gtk = sp.getString(Constants.GTK, "");
        userInfo.sid = sp.getString(Constants.SID, "");
        return userInfo;
    }

    /**
     * 清除用户信息
     *
     * @param context
     */
    public static void clearUseInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 存储刷新空间的时间
     *
     * @param context
     * @param time
     */
    public static void saveLaunchTime(Context context, long time) {

        SharedPreferences sp = context.getSharedPreferences(Constants.LAUNCHTIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(Constants.LAUNCHTIME, time);
        editor.commit();

    }

    /**
     * 得到刷新空间的是时间,默认值为5（min）
     *
     * @param context
     * @return
     */
    public static long getLaunchTime(Context context) {


        SharedPreferences sp = context.getSharedPreferences(Constants.LAUNCHTIME, Context.MODE_PRIVATE);
        return sp.getLong(Constants.LAUNCHTIME, 0);


    }

    /**
     * 清除cookie
     *
     * @param context
     */
    public static void clearCookie(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.COOKIE, "");
        editor.commit();
    }

    /**
     * app是否第一次使用
     *
     * @param context
     */
    public static boolean isFirstUse(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.Config, Context.MODE_PRIVATE);
        return sp.getBoolean(Constants.ISFIRSTUSE, true);
    }

    /**
     * 当app第一次运行的时候，注册app
     *
     * @param context
     */
    public static void registerApp(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.Config, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(Constants.ISFIRSTUSE, false);
        editor.commit();
    }

    /**
     * app默认的各种手机类型
     *
     * @param context
     * @param phonetypes
     */
    public static void savePhoneTYpe(Context context, Set<String> phonetypes) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PHONETYPE, Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putStringSet(Constants.PHONETYPE, phonetypes);
        editor.commit();
    }

    /**
     * 返回app默认各种手机类型
     *
     * @param context
     * @return 无，返回null
     */
    public static Set<String> getPhoneTYpe(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.PHONETYPE, Context.MODE_APPEND);
        return sp.getStringSet(Constants.PHONETYPE, null);

    }

    /**
     * 保存当前选择的手机类型
     *
     * @param context
     * @param phonetypes
     */
    public static void saveMyPhoneTYpe(Context context, String phonetypes) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MY_PHONETYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.PHONETYPE, phonetypes);
        editor.commit();
    }

    /**
     * 返回当前选择的手机类型
     *
     * @param context
     * @return
     */
    public static String getMyPhoneTYpe(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.MY_PHONETYPE, Context.MODE_PRIVATE);
        return sp.getString(Constants.PHONETYPE, "");
    }

    /**
     * 存储当前的主页url
     *
     * @param context
     * @param url
     */
    public static void saveHomeUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(Constants.HOME_URL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.HOME_URL, url);
        editor.commit();
    }

    /**
     * 获取当前的主页url
     *
     * @param context
     * @return
     */
    public static String getHomeUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.HOME_URL, Context.MODE_PRIVATE);
        return sp.getString(Constants.HOME_URL, "");
    }

    /**
     * 存储天气信息
     *
     * @param context
     * @param weather
     */
    public static void saveWeatherInfo(Context context, Weather weather) {
        SharedPreferences sp = context.getSharedPreferences(Constants.WEATHER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null != weather.city && !"".equals(weather.city))
            editor.putString(Constants.WEATHER_INFO_CITY, weather.city);
        if (null != weather.date && !"".equals(weather.date))
            editor.putString(Constants.WEATHER_INFO_DATE, weather.date);
        if (null != weather.weather && !"".equals(weather.weather))
            editor.putString(Constants.WEATHER_INFO_WEATHER, weather.weather);
        if (null != weather.temp && !"".equals(weather.temp))
            editor.putString(Constants.WEATHER_INFO_TEMP, weather.temp);
        if (null != weather.name && !"".equals(weather.name))
            editor.putString(Constants.WEATHER_INFO_Name, weather.name);
        editor.commit();
    }

    /**
     * 获取天气信息
     *
     * @param context
     * @return
     */
    public static Weather getWeatherInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.WEATHER_INFO, Context.MODE_PRIVATE);
        String city = sp.getString(Constants.WEATHER_INFO_CITY, "");
        String date = sp.getString(Constants.WEATHER_INFO_DATE, "");
        String weather = sp.getString(Constants.WEATHER_INFO_WEATHER, "");
        String temp = sp.getString(Constants.WEATHER_INFO_TEMP, "");
        String name = sp.getString(Constants.WEATHER_INFO_Name, "");
        return new Weather(city, date, weather, temp, name);

    }


}
