package com.yuevision.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yuevision.sample.base.MyApplication;

import java.util.ArrayList;
import java.util.List;


/**
 * SharedPreferences数据保存
 *
 * @author JackSong
 */
public class SPUtil {

    private static final String CONFIG = "identify_voice";//sp文件名

    private static final String IDENTIFY_TYPE = "identifyType";//证件类型
    private static final String IS_DEFAULT = "isDefault";//是否是默认用户
    private static final String IS_LOGIN = "isLogin";//是否登录
    private static final String USER_NAME = "userName";//证件号
    private static final String USER_PS = "password";//密码
    private static final String USER_IMG = "userimage";//图片
    private static final String TOKEN_TIME = "token_time";//tokentime

    /**
     * 获取SharedPreferences实例对象
     *
     * @param fileName
     */
    private static SharedPreferences getSharedPreference(String fileName) {
        return MyApplication.getInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * ==============================================================================
     * ======================================用户相关功能========================================
     * ==============================================================================
     */


    /**
     * 是否是本地用户
     *
     * 默认用户设置false时，所有关于该用户的信息清空
     *
     * @param isLogin boolean
     */
    public static void setDefaultUser(boolean isLogin) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putBoolean(IS_DEFAULT, isLogin).apply();
    }


    public static boolean isDefaultUser() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getBoolean(IS_DEFAULT, false);
    }

    /**
     * 由账号判断是否是默认账户
     * <p>
     * 登录按钮处使用
     * <p>
     * 本地存了一个默认账户，需要判断新账户是否是本地账户
     *
     * @param name
     * @return
     */
    public static boolean isDefaultUser(String name) {
        String defaultName = getDefaultUser();
        if (defaultName.isEmpty()) {
            MLog.e("无默认账户，程序代码逻辑bug,请修改");
            return false;
        }
        if (name.equals(defaultName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置默认账户
     *
     * @param name
     */
    public static void setDefaultUser(String name) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(IS_DEFAULT, name).apply();

        //若走这一步，则默认账户的判断也为ture
        setDefaultUser(true);
    }

    /**
     * 获取默认账户
     */
    public static String getDefaultUser() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(IS_DEFAULT, "");
    }


    /**
     * 是否注销
     *
     * @param isLogin
     */
    public static void setIsLogin(boolean isLogin) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putBoolean(IS_LOGIN, isLogin).apply();
    }

    /**
     * 是否注销
     */
    public static boolean isLogin() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getBoolean(IS_LOGIN, false);
    }

    /**
     * 证件号类型
     *
     * @param type
     */
    public static void setType(int type) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putInt(IDENTIFY_TYPE, type).apply();
    }

    /**
     * 证件号类型
     */
    public static int getType() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getInt(IDENTIFY_TYPE, 0);
    }

    /**
     * 证件号
     *
     * @param name String
     */
    public static void setUserNum(String name) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(USER_NAME, name).apply();
    }

    /**
     * 证件号
     */
    public static String getUserNum() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(USER_NAME, "");
    }

    //用户图片
    public static void setUserImage(String img) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(USER_IMG, img).apply();
    }

    public static String getUserImage() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(USER_IMG, "");

    }

    //token保存时间
    public static void setTokenTime(int time) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putInt(TOKEN_TIME, time).apply();
    }

    public static int getTokenTime() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getInt(TOKEN_TIME, 0);
    }


    //密码
    public static void setPassword(String psd) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(USER_PS, psd).apply();
    }

    public static String getPassword() {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(USER_PS, "");
    }

    //清除用户登录信息
    public static void clearUserMessge() {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(USER_NAME, "").apply();
        editor.putString(USER_PS, "").apply();
        editor.putBoolean(IS_LOGIN, false).apply();
    }

    /**
     * ==============================================================================
     * ======================================微信相关========================================
     * ==============================================================================
     */

    /**
     * ==============================================================================
     * ======================================通用功能========================================
     * ==============================================================================
     */
    /**
     * 保存一个String类型的值！
     */
    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putString(key, value).apply();
    }

    /**
     * 获取String的value
     */
    public static String getString(String key, String defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getString(key, defValue);
    }

    /**
     * 保存一个Boolean类型的值！
     */
    public static void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putBoolean(key, value).apply();
    }

    /**
     * 获取boolean的value
     */
    public static boolean getBoolean(String key, Boolean defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getBoolean(key, defValue);
    }

    /**
     * 保存一个int类型的值！
     */
    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putInt(key, value).apply();
    }

    /**
     * 获取int的value
     */
    public static int getInt(String key, int defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getInt(key, defValue);
    }

    /**
     * 保存一个float类型的值！
     */
    public static void putFloat(String fileName, String key, float value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putFloat(key, value).apply();
    }

    /**
     * 获取float的value
     */
    public static float getFloat(String key, Float defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getFloat(key, defValue);
    }

    /**
     * 保存一个long类型的值！
     */
    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.putLong(key, value).apply();
    }

    /**
     * 获取long的value
     */
    public static long getLong(String key, long defValue) {
        SharedPreferences sharedPreference = getSharedPreference(CONFIG);
        return sharedPreference.getLong(key, defValue);
    }

    /**
     * 取出List<String>
     *
     * @param key List<String> 对应的key
     * @return List<String>
     */
    public static List<String> getStrListValue(String key) {
        List<String> strList = new ArrayList<String>();
        int size = getInt(key + "size", 0);
        //Log.d("sp", "" + size);
        for (int i = 0; i < size; i++) {
            strList.add(getString(key + i, ""));
        }
        return strList;
    }

    /**
     * 存储List<String>
     *
     * @param key     List<String>对应的key
     * @param strList 对应需要存储的List<String>
     */
    public static void putStrListValue(String key, List<String> strList) {
        if (null == strList) {
            return;
        }
        // 保存之前先清理已经存在的数据，保证数据的唯一性
        removeStrList(key);
        int size = strList.size();
        putInt(key + "size", size);
        for (int i = 0; i < size; i++) {
            putString(key + i, strList.get(i));
        }
    }

    /**
     * 清空List<String>所有数据
     *
     * @param key List<String>对应的key
     */
    public static void removeStrList(String key) {
        int size = getInt(key + "size", 0);
        if (0 == size) {
            return;
        }
        remove(key + "size");
        for (int i = 0; i < size; i++) {
            remove(key + i);
        }
    }

    /**
     * 清空对应key数据
     */
    public static void remove(String key) {
        SharedPreferences.Editor editor = getSharedPreference(CONFIG).edit();
        editor.remove(key).apply();
    }
}


