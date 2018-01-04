package com.yuevision.sample.utils;

import android.content.Context;

import java.io.File;

import mops.fsidemo.base.app.Constants;

/**
 * 缓存文件 工具类
 */

public class FSFileUtils {
    public static String basePath = null;// 保存的根目录
    public static String registFilePath = null;//注册文件路径（assets下的.lic .model文件路径）
    public static String saveRegPicPath = null;//注册图片路径
    public static String saveRegVoicePath = null;//注册音频路径
    public static String savePicPath = null;//图片路径
    public static String saveVoicePath = null;//音频路径

    /**
     * application中需要初始化
     */
    public static void initSavePath(Context context) {
        String path;
        //文件根路径在 私有目录中
        File dataFile = context.getApplicationContext().getExternalFilesDir(null);
        if (dataFile != null) {
            path = dataFile.getAbsolutePath();
        } else {
            //正常的手机，这一部分是永远不会执行 这里是内部存储的路径，
            dataFile = context.getApplicationContext().getFilesDir();
            path = dataFile.getAbsolutePath();
        }

        basePath = path;
        registFilePath = path+ File.separator + Constants.RESIST_FILE+ File.separator;
        savePicPath = basePath + File.separator + Constants.PIC_FILE+ File.separator;
        saveVoicePath = basePath + File.separator + Constants.VOICE_FILE+ File.separator;

        saveRegPicPath = basePath + File.separator + Constants.REG_PIC_FILE+ File.separator;
        saveRegVoicePath = basePath + File.separator + Constants.REG_VOICE_FILE+ File.separator;



        mkDir(registFilePath);

        mkDir(saveRegPicPath);
        mkDir(saveRegVoicePath);

        mkDir(savePicPath);
        mkDir(saveVoicePath);
    }

    /**
     * 创建路径
     */
    public static boolean mkDir(String dirPath) {
        boolean isExist;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            isExist = dirFile.mkdir();
        } else {
            isExist = true;
        }

        return isExist;
    }

    /**
     * 清除缓存
     */
    public static void clearFiles() {
        File baseFile = new File(basePath);
        File[] fileList;
        if (baseFile.exists() && baseFile.isDirectory()) {
            fileList = baseFile.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isFile()) {
                    fileList[i].delete();
                    fileList[i].exists();
                }
            }
        }
    }

}
