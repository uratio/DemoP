package com.uratio.demop.file;

import java.io.Serializable;

/**
 * @author lang
 * @data 2021/9/22
 */
public class FileBean implements Serializable {
    public String videoType;
    public String videoPath;
    public String videoCompressedPath;
    public String videoSize;

    @Override
    public String toString() {
        return "FileBean{" +
                "videoType='" + videoType + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", videoCompressedPath='" + videoCompressedPath + '\'' +
                ", videoSize='" + videoSize + '\'' +
                '}';
    }
}
