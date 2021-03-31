package com.uratio.demop.scanbank;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.ml.KNearest;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uratio.demop.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;

public class ScanOpenCVActivity extends AppCompatActivity {

    //手机相册or手机相机相关参数
    private static int RESULT_LOAD_IMAGE = 1;

    int hehe = 0;

    int jisuan = 0;
    int error = 0;
    Button btnPic;
    Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap tempBitmap;
    ImageView imgHuaishi;
    private static boolean flag = false;
    private static boolean isFirst = true;
    private static final String TAG = "MyLogcat";

    int numX1 = 0, numX2 = 0;
    int aa[], bb[];
    int bankX[];//19个银行卡号的位置信息----x
    int bankNum[];//19个银行卡号
    int bankNumCount = 19;
    int classes = 10, train_samples = 1, K = 1;
    int new_width = 32;
    int new_height = 32;
    KNearest knn;
    Mat trainData, trainClasses;

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_open_cv);

        initUI();
        //procSrc2Gray();
        pretreatment();//测试用
        btnProcess.setOnClickListener(new ProcessClickListener());
        btnPic.setOnClickListener(new ProcessPicClickListener());
    }


    public void initUI() {
        btnProcess = (Button) findViewById(R.id.btn_gray_process);
        btnPic = (Button) findViewById(R.id.btn_pic);
        imgHuaishi = (ImageView) findViewById(R.id.img_huaishi);
        Log.i(TAG, "initUI sucess...");
        aa = new int[bankNumCount];
        bb = new int[bankNumCount];
        for (int i = 0; i < bankNumCount; ++i) {
            aa[i] = 0;
            bb[i] = 0;
        }
        bankX = new int[bankNumCount];
        bankNum = new int[bankNumCount];
        tempBitmap = getRes("shuai");
        this.imgHuaishi.setImageBitmap(tempBitmap);
    }

    public Bitmap getRes(String name) {//获得res文件夹下的图片，得到bmp图片
        ApplicationInfo appInfo = getApplicationInfo();
        int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
        return BitmapFactory.decodeResource(getResources(), resID);
    }

    void getData() {//训练集的生成与保存
        Mat src_image = new Mat();
        Mat prs_image = new Mat(new Size(new_width, new_height), CvType.CV_8UC1);
        //Mat prs_image = new Mat();
        Mat row = new Mat(), data = new Mat(), rowb = new Mat(), datab = new Mat();
        int x, y;
        int c = 0;
        int cc = 0;
        int i, j, k = 0;
        int m = 0, n = 0;
        for (m = 0; m < classes; m++) {
            for (n = 0; n < train_samples; n++) {
                String name = "a" + String.valueOf(m) + String.valueOf(n);
                System.out.println("ImageName："+name);
                Bitmap bmp = getRes(name);
                if (bmp == null) {
                    System.out.println("NULL Image");
                }
                Utils.bitmapToMat(bmp, src_image);
                 /*src_image = Highgui.imread("/assets/a00.bmp");
                 if (src_image == null)
                 {
                     System.out.println("NULL Image");
                 }*/
                //System.out.println(String.valueOf(src_image.width()));
                //System.out.println(String.valueOf(src_image.channels()));
                Mat grayMat = new Mat();  //灰度图
                Mat binaryMat = new Mat(); //二值化图
                Mat mytemp = new Mat(new Size(new_width, new_height), CvType.CV_8U);
                Imgproc.cvtColor(src_image, grayMat, Imgproc.COLOR_RGBA2GRAY); //rgbMat to gray grayMat
                Imgproc.threshold(grayMat, binaryMat, 100, 255, Imgproc.THRESH_BINARY);//二值化
                //binaryMat = changeMat(binaryMat);
                binaryMat.copyTo(prs_image);
                Imgproc.resize(prs_image, mytemp, new Size(new_width, new_height));
                float imgData[] = new float[1];
                imgData[0] = (float) m;
                trainClasses.put(m * train_samples + n, 0, imgData);//trainClasses存入数据,注意trainClasses是32位浮点数
                Mat img = new Mat(new Size(mytemp.width(), mytemp.height()), CvType.CV_32FC1);
                mytemp.convertTo(img, CvType.CV_32FC1, 0.0039215);
                //prs_image.copyTo(img);
                img = img.reshape(0, 1);
                //Mat2IntArr(img);
                float tempData[] = new float[img.cols()];
                img.get(0, 0, tempData);
                //Mat2IntArr(binaryMat);
                trainData.put(m * train_samples + n, 0, tempData);
                //grayBitmap = Bitmap.createBitmap(prs_image.width(), prs_image.height(), Config.RGB_565);
                //grayBitmap.setPixels(result, 0, w, 0, 0, w, h);
                //Utils.matToBitmap(prs_image, grayBitmap);  //convert mat to bitmap
            }
        }
        //Mat2IntArr(trainData);
    }


    float do_ocr(Mat mat) {
        Mat pimage = new Mat();
        Mat data = new Mat();
        Imgproc.resize(mat, pimage, new Size(new_width, new_height));
        Mat image = new Mat(new Size(pimage.width(), pimage.height()), CvType.CV_32FC1);
        pimage.convertTo(image, CvType.CV_32FC1, 0.0039215);
        //Mat2IntArr(image);
        data = image.reshape(0, 1);
        Mat nearest = new Mat(new Size(K, 1), CvType.CV_32FC1);
        Mat results = new Mat();
        Mat dists = new Mat();
        //Mat2IntArr(data);
        float res = knn.findNearest(data, K, results, nearest, dists);
        return res;
    }


    void sortBankNum()//对银行卡进行排序
    {
        int i, j, temp;
        for (j = 0; j <= 17; j++) {
            for (i = 0; i < 17 - j; i++)
                if (bankX[i] > bankX[i + 1]) {
                    temp = bankX[i];
                    bankX[i] = bankX[i + 1];
                    bankX[i + 1] = temp;
                    temp = bankNum[i];
                    bankNum[i] = bankNum[i + 1];
                    bankNum[i + 1] = temp;
                }
        }
        /*bankNum[0] = 6;
        bankNum[1] = 2;
        bankNum[2] = 2;
        bankNum[3] = 8;
        bankNum[4] = 4;
        bankNum[5] = 8;*/
    }


    Mat Filter(Mat imgSrc, Mat src, int t2) {//过滤图像
        int a = 0, b = 0;//保存有效行号
        int h = 0;
        int state = 0;//标志位，0则表示还未到有效行，1则表示到了有效行,2表示搜寻完毕
        for (int y = 0; y < imgSrc.height(); y++) {
            int count = 0;
            for (int x = 0; x < imgSrc.width(); x++) {
                //System.out.println("ok");
                byte[] data = new byte[1];
                imgSrc.get(y, x, data);
                //System.out.println("ok2");
                if (data[0] == 0)
                    count = count + 1;
            }
            if (state == 0)//还未到有效行
            {
                if (count >= 10)//找到了有效行
                {//有效行允许十个像素点的噪声
                    a = y;
                    state = 1;
                }
            } else if (state == 1) {
                if (count <= 10)//找到了有效行
                {//有效行允许十个像素点的噪声
                    b = y;
                    state = 2;
                }
            }
        }
        numX1 = a;
        numX2 = b;
        System.out.println(Integer.toString(a));
        System.out.println(Integer.toString(b));

        if (b - a > 10) {
            Rect roi = new Rect(0, a, src.width(), b - a);
            //Mat res = cvCreateImage(cvSize(roi.width, roi.height), 8, 1);
            Mat res = new Mat(new Size(roi.width, roi.height), CvType.CV_8UC1);
            //IplImage *orig = cvCreateImage(cvSize(roi.width, roi.height), 8, 1);
            Mat orig = new Mat(new Size(roi.width, roi.height), CvType.CV_8UC1);
            //cvSetImageROI(src, roi);
            byte[] data = new byte[roi.width * roi.height];
            src.get(a, 0, data);
            orig.put(0, 0, data);
            Imgproc.threshold(orig, res, t2, 255, Imgproc.THRESH_BINARY);//二值化 55
                /*Size size = new Size(3, 3);
                Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
                Imgproc.erode(res, res, element);//腐蚀图像
                */
            int newW = (int) (res.width() / 3);
            int newW2 = (int) (2 * res.width() / 3);
            Mat temp = new Mat(new Size(newW2, res.height()), CvType.CV_8UC1);
            byte[] Tempdata = new byte[newW2];
            for (int i = 0; i < res.height(); ++i) {
                res.get(i, newW, Tempdata);
                temp.put(i, 0, Tempdata);
            }
            System.out.println("矩阵赋值成功了！");
            System.out.println(String.valueOf(temp.cols()) + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + String.valueOf(temp.rows()));
            tempBitmap = Bitmap.createBitmap(temp.cols(), temp.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(temp, tempBitmap);
            //this.imgHuaishi.setImageBitmap(tempBitmap);
            //return res;
            return temp;
        } else
            return null;
    }


    void coutIntArr(int[] temp, int[] temp2) {
        for (int i = 0; i < temp.length; ++i) {
            System.out.println(String.valueOf(temp[i]) + "+" + String.valueOf(temp2[i]));
        }
    }

    public Mat cutMat(Mat imgSrc) {
        int a = 0, b = 0;//保存有效行号
        int h = 0;
        int state = 0;//标志位，0则表示还未到有效行，1则表示到了有效行,2表示搜寻完毕
        for (int y = 0; y < imgSrc.height(); y++) {
            int count = 0;
            for (int x = 0; x < imgSrc.width(); x++) {
                //System.out.println("ok");
                byte[] data = new byte[1];
                imgSrc.get(y, x, data);
                //System.out.println("ok2");
                if (data[0] == 0)
                    count = count + 1;
            }
            if (state == 0)//还未到有效行
            {
                if (count >= 10)//找到了有效行
                {//有效行允许十个像素点的噪声
                    a = y;
                    state = 1;
                }
            } else if (state == 1) {
                if (count <= 3)//找到了有效行
                {//有效行允许十个像素点的噪声
                    b = y;
                    state = 2;
                    break;
                }
            }
        }
        Log.i("MyLogcat", "开始截取图片" + String.valueOf(imgSrc.width()) + "+" + String.valueOf(b - a));
        Rect roi = new Rect(0, a, imgSrc.width(), b - a);
        Mat res = new Mat(new Size(roi.width, roi.height), CvType.CV_8UC1);
        res = imgSrc.submat(roi);
        Log.i("MyLogcat", "截取成功");
        if (hehe == 5) {
            //tempBitmap = Bitmap.createBitmap(res.cols(), res.rows(), Config.RGB_565);
            //Utils.matToBitmap(res, tempBitmap);
            //this.imgHuaishi.setImageBitmap(tempBitmap);
        }
        return res;
    }


    public int procSrc2Gray(int t, int t2) {
        Size size1 = new Size(589, 374);

        Mat rgbMat = new Mat(); //原图
        Mat grayMat = new Mat();  //灰度图
        Mat binaryMat = new Mat(); //二值化图
        Mat erode = new Mat();
        Mat last = new Mat();

        //srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a2);
        //srcBitmap = getRes("a00");

        //grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat); //convert original bitmap to Mat, R G B.
        Bitmap bitP = srcBitmap;
        Imgproc.resize(rgbMat, rgbMat, size1);
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY); //rgbMat to gray grayMat
        //Mat2IntArr(grayMat);
        //继续预处理
        Imgproc.threshold(grayMat, binaryMat, t, 255, Imgproc.THRESH_BINARY);//二值化

        //Mat2IntArr(binaryMat);
        Imgproc.medianBlur(binaryMat, binaryMat, 3);
        Size size = new Size(3, 3);
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size);
        System.out.println("腐蚀图像之前"+Integer.toString(erode.channels()));
        Imgproc.erode(binaryMat, erode, element);//腐蚀图像
        Log.i("MyLogcat", "进入Filter");
        last = Filter(erode, grayMat, t2);
        Log.i("MyLogcat", "完成Filter");

        //Mat2IntArr(res);
        boolean a1;
        if (last != null) {
            Log.i("MyLogcat", "进入findRect");
            a1 = findRect(last);
        } else {
            a1 = false;
        }
        if (a1) {
            Log.i("MyLogcat", "完成findRect1111");
            //System.out.println(String.valueOf(aa.length)+" "+String.valueOf(bb.length));
            /*for (int a = 0; a < 19; a++)
            {
                System.out.println(String.valueOf(aa[a])+" "+String.valueOf(bb[a]));
            }*/

            //Imgproc.medianBlur(last, last, 3);

            //************将mat转换成int[]，送给c++进行处理*************
            /*int h = last.height();
            int w = last.width();
            int temp[] = new int[h*w];
            int result[] = new int[h*w];
            temp = Mat2IntArr(last);
            result = LibImgFun.ImgFun(temp, w, h);
            System.out.println(String.valueOf(result[0]));
            */
            /*String path = "bank";
            Bitmap bmp = getRes(path);
            if (bmp == null)
            {
                System.out.println("NULL Image");
            }
            System.out.println(String.valueOf(bmp.getHeight()) + " " + String.valueOf(srcBitmap.getHeight()));
            imgHuaishi.setImageBitmap(bmp);*/

            trainData = new Mat(new Size(new_width * new_height, classes * train_samples), CvType.CV_32FC1);
            trainClasses = new Mat(new Size(1, classes * train_samples), CvType.CV_32FC1);

            getData();//得到训练数据
            knn = KNearest.create();
            Mat sampleIdx = new Mat();
            //boolean a = knn.train(trainData, trainClasses, sampleIdx, false, K, false);
//            boolean a = knn.train(trainData, trainClasses);

            //float ret = do_ocr(binaryMat);
            //System.out.println(String.valueOf(ret));

            //分割得到数字
            Mat orig = new Mat();
            last.copyTo(orig);
            Mat bininv_img = new Mat();
            int c = 0;
            hehe = 0;
            error = 0;
            for (int i = 0; i < bankNumCount; i++) {
                c++;
                hehe++;
                if (bb[i] - aa[i] < 5) {
                    Log.i("MyLogcat", "忽略");
                    bankX[i] = 0;
                    bankNum[i] = 0;
                    error++;
                    continue;
                }
                Rect roi = new Rect(aa[i], 0, bb[i] - aa[i], numX2 - numX1);
                bankX[i] = aa[i];//保存位置信息
                Mat res = new Mat();
                res = orig.submat(roi);
                if (c == 7) {
                    //grayBitmap = Bitmap.createBitmap(res.width(), res.height(), Config.RGB_565);
                    //Utils.matToBitmap(res, grayBitmap);  //convert mat to bitmap
                    //Mat2IntArr(res);
                }

                float ret = do_ocr(res);
                bankNum[i] = (int) ret;
            }
            Log.i("MyLogcat", "本次识别失败数是：" + String.valueOf(error));
            Log.i("MyLogcat", "进入sortBankNum");
            coutIntArr(bankNum, bankX);

            sortBankNum();
            coutIntArr(bankNum, bankX);
            Log.i("MyLogcat", "完成sortBankNum");

            String x = new String();
            x = "622848";
            int finish = 0;
            for (int i = 0; i < 18; ++i) {
                if (i + error > 17) {
                    break;
                }
                if (bankX[i + error] != 0) {
                    x = x + String.valueOf(bankNum[i + error]);
                    Log.i("MyLogcat", String.valueOf(bankNum[i + error]) + "+" + String.valueOf(i + error));
                    finish++;
                    if (finish >= 13) {
                        break;
                    }
                }
            }

            this.imgHuaishi.setImageBitmap(bitP);
            Log.i("MyLogcat", "准备放置结果了！");
            Toast.makeText(ScanOpenCVActivity.this, "卡号已经复制到剪切板！！！", Toast.LENGTH_LONG).show();
            TextView text = (TextView) findViewById(R.id.mttext);
            //x = "6228481099312404479";
            text.setText(x);
            ClipboardManager myClipboard;
            myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip;
            myClip = ClipData.newPlainText("text", x);
            myClipboard.setPrimaryClip(myClip);

          /* grayBitmap = Bitmap.createBitmap(last.width(), last.height(), Config.RGB_565);
           //grayBitmap.setPixels(result, 0, w, 0, 0, w, h);
           Utils.matToBitmap(last, grayBitmap);  //convert mat to bitmap*/
            Log.i("MyLogcat", "procSrc2Gray sucess...");
            return error;
        } else {
            TextView text = (TextView) findViewById(R.id.mttext);
            text.setText("*******************");
            tempBitmap = getRes("shuai");
            this.imgHuaishi.setImageBitmap(tempBitmap);
            Log.i("MyLogcat", "完成findRect2222");
            System.out.println("?????????????????????????????????");
            Toast.makeText(ScanOpenCVActivity.this, "本次识别失败，请在光线较亮的地方再次尝试！！", Toast.LENGTH_LONG).show();
            srcBitmap.recycle();   //回收图片所占的内存
            return 1;
        }
    }


    boolean findRect(Mat srcA) {//将银行数字分割，黑点为前景点，白点为背景点。
        int count = 0;
        int judgeA = 0;//0代表当前要判断有效列，1代表当前要判断无效列
        int i = 0;
        Mat src = new Mat();
        if (srcA == null) {
            return false;
        }
        Imgproc.medianBlur(srcA, src, 3);
        //srcA.copyTo(src);
        //cvSmooth(srcA, src, CV_MEDIAN);
        for (int x = 0; x < src.width(); x++) {
            count = 0;
            for (int y = 0; y < src.height(); y++) {
                byte[] data = new byte[1];
                src.get(y, x, data);
                if (data[0] == 0)//遇到前景点就加1
                {
                    count++;
                }
            }
            if (judgeA == 0) {
                if (count >= 5)//有效行到了
                {
                    judgeA = 1;
                    if (i > 17) {
                        i = 17;
                    }
                    aa[i] = x;
                }
            } else if (judgeA == 1) {
                if (count < 5) {
                    judgeA = 0;
                    if (i > 17) {
                        i = 17;
                    }
                    bb[i] = x;
                    i++;
                }
            }
        }
        //coutIntArr(bb, aa);
        /*
        for (int ii = 0; ii < 19; ++ii)
        {
            if (bb[ii] - aa[ii] < 5)
            {
                return false;
            }
        }*/
        return true;

        /*for (int m = 0; m < i; m++)
        {

            cvLine(src, cvPoint(aa[m], 0), cvPoint(aa[m], src->height), cvScalar(0, 0, 0));
            cvLine(src, cvPoint(bb[m], 0), cvPoint(bb[m], src->height), cvScalar(0, 0, 0));
        }*/

         /*cvNamedWindow("img");
        cvShowImage("img", src);
        cvWaitKey(0);
            */
    }


    public float[] Mat2IntArr(Mat mat) {
        int h = mat.height();
        int w = mat.width();
        float result[] = new float[h * w];
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                float[] data = new float[1];
                mat.get(i, j, data);
                if (data[0] == 0)
                    result[w * i + j] = 0;
                else
                    result[w * i + j] = 255;
                System.out.println("(" + String.valueOf(i) + "," + String.valueOf(j) + ")" + String.valueOf((float) data[0]));
            }
        }
        System.out.println(String.valueOf(h) + "," + String.valueOf(w));
        return result;
    }

    public Mat changeMat(Mat mat) {
        int h = mat.height();
        int w = mat.width();
        //int result[] = new int[h*w];
        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                byte[] data = new byte[1];
                mat.get(i, j, data);
                if (data[0] != 0) {
                    data[0] = (byte) 255;
                    mat.put(i, j, data);
                }
            }
        }
        return mat;
    }


    public void pretreatment() {//读入原始图片
            /*Mat src = Highgui.imread("bank.jpg");
            if (src == null)
            {
                 Log.i(TAG,  "LOAD FAILED ..." );
                 return;
            }*/
        Bitmap bm1 = BitmapFactory.decodeFile("/drawable-hdpi/bank.jpg");
        if (bm1 == null) {
            //Toast.makeText(MainActivity.this, "===========图片读取失败！============！！", 3).show();
            return;
        }
        imgHuaishi.setImageBitmap(bm1);

    }


    private class ProcessClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            /* if (isFirst)
            {
                procSrc2Gray();
                isFirst =  false ;
            }
             if (flag){
                imgHuaishi.setImageBitmap(grayBitmap);
                btnProcess.setText( "查看原图" );
                flag =  false ;
            }
             else {
                imgHuaishi.setImageBitmap(srcBitmap);
                btnProcess.setText( "灰度化" );
                flag =  true ;
            }  */
            /* Intent i = new Intent(
                     Intent.ACTION_PICK,
                     android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             startActivityForResult(i, RESULT_LOAD_IMAGE);*/
            if (ContextCompat.checkSelfPermission(ScanOpenCVActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(ScanOpenCVActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                            .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanOpenCVActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, 1001);
            } else {
                camera();
            }
        }
    }

    private class ProcessPicClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (ContextCompat.checkSelfPermission(ScanOpenCVActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                    .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScanOpenCVActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1002);
            } else {
                choosePic();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera();
            } else {
                Toast.makeText(ScanOpenCVActivity.this, "同意权限后才能进行拍照！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePic();
            } else {
                Toast.makeText(ScanOpenCVActivity.this, "同意权限后才能选择照片！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void choosePic() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    /* @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         System.out.println("图片选择成功");
         if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
             Uri selectedImage = data.getData();
             String[] filePathColumn = { MediaStore.Images.Media.DATA };

             Cursor cursor = getContentResolver().query(selectedImage,
                     filePathColumn, null, null, null);
             cursor.moveToFirst();

             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
             String picturePath = cursor.getString(columnIndex);
             cursor.close();
             System.out.println("是不是这里的问题？");
             imgHuaishi.setImageBitmap(BitmapFactory.decodeFile(picturePath));
             srcBitmap = BitmapFactory.decodeFile(picturePath);
             System.out.println("进入主函数");
             System.out.println(String.valueOf(srcBitmap.getWidth()) + " and " + String.valueOf(srcBitmap.getHeight()));
             procSrc2Gray();
             new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //procSrc2Gray();
                             System.out.println("线程进来了");
                        }
                    });
                }
            }).start();
         }

     }*/


    /*
     * 从相机获取
     */

    private static final int PHOTO_REQUEST_CAREMA = 1;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    /**
     * 判断手机是否有SD卡。
     *
     * @return 有SD卡返回true，没有返回false。
     */
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSDCard()) {
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    PHOTO_FILE_NAME);
            // 从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    private Uri mUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            System.out.println("是不是这里的问题？");
            imgHuaishi.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            srcBitmap = BitmapFactory.decodeFile(picturePath);
            System.out.println("进入主函数");
            System.out.println(String.valueOf(srcBitmap.getWidth()) + " and " + String.valueOf(srcBitmap.getHeight()));
            procSrc2Gray(28, 30);
        } else if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                mUri = data.getData();
                crop(mUri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSDCard()) {
                mUri = Uri.fromFile(tempFile);
                crop(mUri);
            } else {
                Toast.makeText(ScanOpenCVActivity.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
//                Bitmap bitmap = data.getParcelableExtra("data");
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri));
                } catch (FileNotFoundException e) {
                    Log.i("MyLogcat", "FileNotFoundException : e="+e.getMessage());
                    e.printStackTrace();
                }
                //this.imgHuaishi.setImageBitmap(bitmap);
                srcBitmap = bitmap;
                Log.i("MyLogcat", "procSrc2Gray 进来了！");
                //int i = 40;
                //int judge = 1;
                //while(judge > 0)
                //{//错误大于0则继续
                procSrc2Gray(45, 55);//43
                //i++;
                //if (i >= 45)
                //{
                //System.out.println("?????????????????????????????????");
                //Toast.makeText(MainActivity.this, "本次识别失败，请在光线较亮的地方再次尝试！！", 3).show();
                //break;
                //}
                //}
            }
            try {
                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 8);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 100);

        int maxSize = 600;
//        intent.putExtra("aspectX", width);
//        intent.putExtra("aspectY", height);
//        intent.putExtra("outputX", width > height ? maxSize : maxSize * width / height);
//        intent.putExtra("outputY", width > height ? maxSize * height / width : maxSize);

//        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
//        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //load OpenCV engine and init OpenCV library
        if (!OpenCVLoader.initDebug()){// 默认加载opencv_java.so库
        }
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, getApplicationContext(), mLoaderCallback);
        Log.i(TAG, "onResume sucess load OpenCV...");
//      new Handler().postDelayed(new Runnable(){
//
//          @Override
//          public void run() {
//              // TODO Auto-generated method stub
//              procSrc2Gray();
//          }
//
//      }, 1000);

    }
}
