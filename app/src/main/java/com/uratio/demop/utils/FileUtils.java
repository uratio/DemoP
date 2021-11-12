package com.uratio.demop.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

/**
 * @author lang
 * @data 2021/9/22
 */
public class FileUtils {

    /**
     * 文件存储路径
     * 目录：Android/data/包名/apk文件夹下
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static String getPath(Context mContext, String packageName) {
        File dir = mContext.getExternalFilesDir(null);
        if (dir == null) {
            return "";
        }

        File file = new File(dir.getAbsolutePath() + "/" + packageName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 删除目录下所有文件
     * @param dir 目录文件
     * @param deleteItself 是否删除自身
     */
    public static void deleteAllFiles(File dir, boolean deleteItself) {
        try {
            if (!dir.exists() || !dir.isDirectory())
                return;

            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    file.delete(); // 删除所有文件
                } else if (file.isDirectory()) {
                    deleteAllFiles(file, deleteItself); // 递规的方式删除文件夹
                }
            }

            if (deleteItself) dir.delete();// 删除目录本身
        } catch (Exception e) {
            LogUtils.e("FileUtils", e.getMessage());
        }
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize, sizeType);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    public static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case 1:// B
                fileSizeLong = Double.parseDouble(df.format((double) fileS));
                break;
            case 2:// KB
                fileSizeLong = Double.parseDouble(df.format((double) fileS / 1024));
                break;
            case 3:// MB
                fileSizeLong = Double.parseDouble(df.format((double) fileS / 1048576));
                break;
            case 4:// GB
                fileSizeLong = Double.parseDouble(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 复制单个文件到指定目录
     */
    public static String copyFile(String oldPath, String newPath) {
        try {
            File oldFile = new File(oldPath);
            if (!oldFile.exists()) {
                LogUtils.e("--Method--", "copyFile:  oldFile not exist.");
                return "";
            } else if (!oldFile.isFile()) {
                LogUtils.e("--Method--", "copyFile:  oldFile not file.");
                return "";
            } else if (!oldFile.canRead()) {
                LogUtils.e("--Method--", "copyFile:  oldFile cannot read.");
                return "";
            }

            String name = oldFile.getName();
            String newPathName = newPath + "/" + name;
            FileInputStream fileInputStream = new FileInputStream(oldFile);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPathName);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return newPathName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
