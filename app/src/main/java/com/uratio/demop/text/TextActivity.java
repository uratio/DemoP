package com.uratio.demop.text;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uratio.demop.R;

public class TextActivity extends AppCompatActivity {
    private static final String TAG = "TextActivity";
    private TextView textView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        textView = findViewById(R.id.tv_text);
        editText = findViewById(R.id.editText);

        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            private Menu mMenu;
            //创建菜单
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.select_menu,menu);
                return true;
            }
            //这个相当于是菜单创建好以后的操作
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                this.mMenu=menu;
                return true;
            }
            //菜单中item的点击处理
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.it_all:
                        //全选
//                        textView.selectAll();
                        Toast.makeText(TextActivity.this, "完成全选", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.it_copy:
                        String selectText = getSelectText(SelectMode.COPY);
                        //setText(selectText)是为了后面的this.mMenu.close()起作用
                        textView.setText(selectText);
                        Toast.makeText(TextActivity.this, "选中的内容已复制到剪切板", Toast.LENGTH_SHORT).show();
                        this.mMenu.close();
                        break;
                    case R.id.it_cut:
                        //剪切
                        String txt = getSelectText(SelectMode.CUT);
                        textView.setText(txt);
                        Toast.makeText(TextActivity.this, "选中的内容已剪切到剪切板", Toast.LENGTH_SHORT).show();
                        this.mMenu.close();
                        break;

                    case R.id.it_paste:
                        //获取剪切班管理者
                        ClipboardManager cbs = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        if (cbs.hasPrimaryClip()){
                            textView.setText(cbs.getPrimaryClip().getItemAt(0).getText());
                        }
                        this.mMenu.close();
                        break;
                }
                return true;
            }
            //菜单销毁操作
            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            private Menu mMenu;
            //创建菜单
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.select_menu,menu);
                return true;
            }
            //这个相当于是菜单创建好以后的操作
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                this.mMenu=menu;
                return true;
            }
            //菜单中item的点击处理
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.it_all:
                        //全选
//                        textView.selectAll();
                        Toast.makeText(TextActivity.this, "完成全选", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.it_copy:
                        String selectText = getSelectText(SelectMode.COPY);
                        //setText(selectText)是为了后面的this.mMenu.close()起作用
                        textView.setText(selectText);
                        Toast.makeText(TextActivity.this, "选中的内容已复制到剪切板", Toast.LENGTH_SHORT).show();
                        this.mMenu.close();
                        break;
                    case R.id.it_cut:
                        //剪切
                        String txt = getSelectText(SelectMode.CUT);
                        textView.setText(txt);
                        Toast.makeText(TextActivity.this, "选中的内容已剪切到剪切板", Toast.LENGTH_SHORT).show();
                        this.mMenu.close();
                        break;

                    case R.id.it_paste:
                        //获取剪切班管理者
                        ClipboardManager cbs = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        if (cbs.hasPrimaryClip()){
                            textView.setText(cbs.getPrimaryClip().getItemAt(0).getText());
                        }
                        this.mMenu.close();
                        break;
                }
                return true;
            }
            //菜单销毁操作
            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setCustomSelectionActionModeCallback(new ActionMode.Callback2() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }*/
    }

    /**
     *  统一处理复制和剪切的操作
     * @param mode 用来区别是复制还是剪切
     * @return
     */
    private String getSelectText(SelectMode mode) {
        //获取剪切班管理者
        ClipboardManager cbs = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        //获取选中的起始位置
        int selectionStart = textView.getSelectionStart();
        int selectionEnd = textView.getSelectionEnd();
        Log.i(TAG,"selectionStart="+selectionStart+",selectionEnd="+selectionEnd);
        //截取选中的文本
        String txt = textView.getText().toString();
        String substring = txt.substring(selectionStart, selectionEnd);
        Log.i(TAG,"substring="+substring);
        //将选中的文本放到剪切板
        cbs.setPrimaryClip(ClipData.newPlainText(null,substring));
        //如果是复制就不往下操作了
        if (mode==SelectMode.COPY)
            return txt;
        //把剪切后的数据替换""
        txt = txt.replace(substring, "");
        return txt;
    }

    /**
     * 用枚举来区分是复制还是剪切
     */
    public enum SelectMode{
        COPY,CUT
    }
}
