package com.uratio.demop.gaussian;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.uratio.demop.R;

/**
 * @author lang
 * @data 2021/11/23
 */
public class GaussianBlurDialog {
    private Dialog dialog;
    private Activity activity;

    public GaussianBlurDialog(Activity activity) {
        this.activity = activity;

        dialog = new Dialog(activity, R.style.DialogStyle);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_gaussian_blur, null);
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        view.setMinimumWidth(display.getWidth());
        view.setMinimumWidth(display.getHeight());
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.LEFT);
    }

    public void show() {
        if (activity == null || activity.isFinishing()) return;
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (activity == null || activity.isFinishing()) return;
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
