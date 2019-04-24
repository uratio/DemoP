package com.uratio.demop.pdf;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.joanzapata.pdfview.util.FileUtils;
import com.uratio.demop.R;

import java.io.File;
import java.net.URL;

public class PDFViewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,OnPageErrorListener {
    private PDFView pdfView;
    private String path = "https://uplt.rrb365.com/pdf/SS201811281607169693.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);

        pdfView = findViewById(R.id.pdfView);

//        pdfView.

        /**
         * 检查读写权限
         */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE},
                    1000);
        } else {
            showPDF();
        }

    }

    private void showPDF() {
        /*try {
            pdfView.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/SS201811281607169693.pdf"))
                    .defaultPage(1)
                    .showMinimap(false)
                    .enableSwipe(true)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            float pageWidth = pdfView.getOptimalPageWidth();
                            float viewWidth = pdfView.getWidth();
                            pdfView.zoomTo(viewWidth / pageWidth);
                            pdfView.loadPages();
                        }
                    })
                    .onPageChange(this)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("PDFView", "onCreate: " + e.getMessage());
        }*/
//        pdfView.fromUri(Uri)
//        or
//        pdfView.fromFile(File)
//        or
//        pdfView.fromBytes(byte[])
//        or
//        pdfView.fromStream(InputStream) // stream is written to bytearray - native code cannot use Java Streams
//        or
//        pdfView.fromSource(DocumentSource)
//        or
//        pdfView.fromAsset(String)
        /*pdfView.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/SS201811281607169693.pdf"))
                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                // allows to draw something on the current page, usually visible in the middle of the screen
                .onDraw(null)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                .onDrawAll(null)
                .onLoad(null) // called after document is loaded and starts to be rendered
                .onPageChange(null)
                .onPageScroll(null)
                .onError(null)
                .onPageError(null)
                .onRender(null) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                .onTap(null)
//                .onLongPress(null)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
//                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//                .linkHandler(DefaultLinkHandler)
//                .pageFitPolicy(FitPolicy.WIDTH)
//                .pageSnap(true) // snap pages to screen boundaries
//                .pageFling(false) // make a fling change only a single page like ViewPager
//                .nightMode(false) // toggle night mode
                .load();*/

        pdfView.fromFile(new File(Environment.getExternalStorageDirectory() + "/Download/SS201811281607169693.pdf"))
                .defaultPage(0)
                .enableSwipe(false) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .onPageChange(this)
                .enableAnnotationRendering(false)
                .onLoad(this)
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        return false;
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    /*private void startOpenPdf(String path) {
        getPdfName(path);
        boolean isLoadComplete = SharedPreferencesUtils.getBooleanParam(this, IS_LOAD_COMPLETE, false);
        String pdfPath = checkApkExist();
        //若存在pdf文件...不存在则直接下载
        if (!TextUtils.isEmpty(pdfPath)) {
            if (isLoadComplete) {
                showPdf(pdfPath);
            } else {
                File file = new File(FileUtils.getRootFilePath() + APP_NAME, pdfName);
                FileUtils.deleteFile(file);
                task = new DownPdfFileTask().execute(path);
            }
        } else {
            task = new DownPdfFileTask().execute(path);
        }
    }

    private void getPdfName(String path) {
        try {
            URL url = new URL(path);
            String urlPath = url.getPath();
            pdfName = urlPath.substring(urlPath.lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String checkApkExist() {
        String path = FileUtils.getRootFilePath() + APP_NAME;
        File file = new File(path, pdfName);
        return file.exists() ? path + "/" + pdfName : "";
    }*/

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageError(int page, Throwable t) {

    }
}
