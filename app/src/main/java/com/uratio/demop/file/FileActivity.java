package com.uratio.demop.file;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.uratio.demop.R;
import com.uratio.demop.utils.Constants;
import com.uratio.demop.utils.FileUtils;
import com.uratio.demop.utils.LogUtils;

import java.io.File;
import java.io.IOException;

public class FileActivity extends AppCompatActivity {
    private Activity activity;
    private FileBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        activity = this;

        bean = new FileBean();
        LogUtils.e("文件数据：" + bean.toString());
        bean.videoType = "2";
        bean.videoPath = "fdhsfd/fdsf/fds/fdsk.mp4";
        bean.videoCompressedPath = "fdhsfd/fdsf/fds/fdsk_compressed.mp4";
        LogUtils.e("给文件赋值数据：" + bean.toString());
    }

    public void onClickView(View view) {
        String outputPath = FileUtils.getPath(activity, Constants.FilePath.VIDEO_SHOOT);
        File file = null;
        switch (view.getId()) {
            case R.id.btn_create_file:
                //是否处理为隐藏文件(是：加 "." ; 不是 为 "")
                file = new File(outputPath, "." + System.nanoTime() + ".mp4");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                LogUtils.e("新建文件完成");
                break;
            case R.id.btn_create_file2:
                //是否处理为隐藏文件(是：加 "." ; 不是 为 "")
                file = new File(outputPath, System.nanoTime() + ".txt");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                LogUtils.e("新建文件完成");
                break;
            case R.id.btn_delete_all:
                file = new File(outputPath);
                FileUtils.deleteAllFiles(file, false);
                LogUtils.e("删除了文件夹下所有文件");
                break;
            case R.id.btn_delete_all2:
                file = new File(outputPath);
                FileUtils.deleteAllFiles(file, true);
                LogUtils.e("删除了文件夹及其目录下所有文件");
                break;
            case R.id.btn_clear_data:
                bean = null;
                bean = new FileBean();
                LogUtils.e("清空文件数据：" + bean.toString());
                break;
            case R.id.btn_get_size:
                double value = FileUtils.formatFileSize(30030730, 3);
                LogUtils.e("大小：" + value);
                break;
        }
    }
}