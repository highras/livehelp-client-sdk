package com.ilivedata.customer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class ManualActivity extends Activity {
    CustomerData instan;
    private boolean landscape;
    private ConstraintLayout layoutMain;
    private ImageView imageButton;
    //两个处理文件选择后回调给web的变量
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private WebView m_webView;
    int webviewCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instan = CustomerData.getInstance();
        setContentView(R.layout.activity_manual);
        //隐藏虚拟按键
//        Window _window = getWindow();
//        WindowManager.LayoutParams params = _window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
//        _window.setAttributes(params);
        mUploadMessage = null;
        mFilePathCallback = null;
        m_webView = null;
        super.onCreate(savedInstanceState);
        getData();
        setFinishOnTouchOutside(false);
        initViews();
        AndroidBug5497Workaround();
        loadData();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layoutMain.getChildCount() > 2){
                layoutMain.removeViewAt(layoutMain.getChildCount() - 1);
                layoutMain.getChildAt(layoutMain.getChildCount() -1).setVisibility(View.VISIBLE);
                webviewCount--;
                return true;
            }
            else if(m_webView.canGoBack()) {
                m_webView.goBack(); //goBack()表示返回WebView的上一页面
                return true;
            }
        }
        this.finish();
        return false;
    }

    private void addWebview(String url){
        WebView jj  = new WebView(this);
        jj.setLayoutParams(m_webView.getLayoutParams());
        jj.getSettings().setJavaScriptEnabled(true);
        jj.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        jj.getSettings().setDomStorageEnabled(true);
        jj.setWebChromeClient(new WebChromeClient() {

            //针对 Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null)
                {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                openFileChooseProcess5(filePathCallback, fileChooserParams);
                return true;
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                openFileChooseProcess(uploadMsg);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });
        layoutMain.addView(jj,++webviewCount);
        layoutMain.getChildAt(layoutMain.getChildCount() -2).setVisibility(View.GONE);
        jj.loadUrl(url);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (m_webView != null){
//            instan.adjustViewHeight(this,m_webView,false);
            instan.adjustHeighMarge(this,m_webView);
        }
    }

    //方法描述：接收数据
    private void getData() {
        landscape = getIntent().getBooleanExtra("webview_landscape", true);
    }

    private void showPostResponseJs(final String content, final int errcode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String script = "javascript:ldChat.platform.response(" + content+ "," + errcode + ")";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//sdk>19才有用

                    m_webView.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String Str) {
//                        Toast.makeText(getApplicationContext(), "我是android调用js方法,返回结果是" + Str, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    m_webView.loadUrl(script);
                }
            }
//                m_webView.loadData(content,"text/html", "utf-8");
        });
    }

    private void postQuest(final String url, String body)
    {
        try {
            URL urlR= new URL(url);
            HttpURLConnection connection= (HttpURLConnection) urlR.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");

            String time = instan.getTime();
            connection.setRequestProperty("Authorization", instan.getAuth(url, time, body, "POST"));
            connection.setRequestProperty("X-TimeStamp", time);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            connection.connect();
            if(body.length() != 0)
            {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(body);
                writer.close();
            }

            int responseCode;
            try
            {
                responseCode = connection.getResponseCode();
            }
            catch (IOException e)
            {
                responseCode = connection.getResponseCode();
            }

            StringBuffer sb = new StringBuffer();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String readLine = new String();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine);
                }
                responseReader.close();
            }
            showPostResponseJs(sb.toString(), responseCode);

        } catch (Exception e) {
            Log.e("customsdk", "postQuest error " + e.getMessage());
        }
    }
    //初始化WebView
    private void initViews() {
        layoutMain = findViewById(R.id.manualMain);

        imageButton = findViewById(R.id.goback);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutMain.getChildCount() > 2){
                    layoutMain.removeViewAt(layoutMain.getChildCount() - 1);
                    layoutMain.getChildAt(layoutMain.getChildCount() -1).setVisibility(View.VISIBLE);
                    webviewCount--;
                    return ;
                }
                else if(m_webView.canGoBack()) {
                    m_webView.goBack(); //goBack()表示返回WebView的上一页面
                    return;
                }
                finish();
            }
        });

        m_webView = findViewById(R.id.showwebview);

        instan.adjustHeighMarge(this,m_webView);
//        instan.adjustViewHeight(this,m_webView,false);

        m_webView.setHorizontalScrollBarEnabled(false);//水平不显示
        m_webView.setVerticalScrollBarEnabled(false); //垂直不显示
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        m_webView.getSettings().setSupportZoom(true);
        m_webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        m_webView.addJavascriptInterface(new WebAppInterface(), "infoData");

        // 设置支持https
        m_webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //判断重定向的方式一
                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                if (hitTestResult == null) {
                    return false;
                }
                if (hitTestResult.getType() == WebView.HitTestResult.UNKNOWN_TYPE) {
                    return false;
                }
                addWebview(url);
                return true;
            }
        });

        m_webView.setWebChromeClient(new WebChromeClient() {

            //针对 Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null)
                {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                openFileChooseProcess5(filePathCallback, fileChooserParams);
                return true;
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                openFileChooseProcess(uploadMsg);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });
        if (webviewCount == 0)
            webviewCount++;
    }

    static final int PICK_CONTACT_REQUEST = 1;
    /**
     * android 5.0(含) 系统自带的图片选择
     */
    private void openFileChooseProcess5(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        this.startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    /**
     * 5.0以下
     */
    private void openFileChooseProcess(ValueCallback<Uri> uploadMsg) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        this.startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    //选择文件后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (null != data) {
                handleCallback(data);
            } else {
                // 取消了照片选取的时候调用
                handleCallback(data);
            }
        } else {
            // 取消了照片选取的时候调用
            handleCallback(data);
        }

    }
    /**
     * 处理WebView的回调
     *
     * @param data
     */
    private void handleCallback(Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mFilePathCallback != null) {
                if (data != null) {
                    onActivityResultAboveL(data);
                } else {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = null;
            }
        } else {
            if (mUploadMessage != null) {
                if (data != null) {
                    Uri uri = data.getData();
                    String url = getFilePathFromContentUri(uri, getContentResolver());
                    Uri u = Uri.fromFile(new File(url));

                    mUploadMessage.onReceiveValue(u);
                } else {
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(Intent intent) {
        Uri[] results = null;
        if (intent != null) {
            ClipData clipData = intent.getClipData();
            String dataString = intent.getDataString();
            if (clipData != null) {
                results = new Uri[clipData.getItemCount()];
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    results[i] = item.getUri();
                }
            }
            else if (dataString != null) {
                results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mFilePathCallback.onReceiveValue(results);
    }

    public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    //方法描述：加载数据
    private void loadData() {
        String webUrl = instan.getManualURL();
        if (!TextUtils.isEmpty(webUrl) && m_webView != null)
            m_webView.loadUrl(webUrl);
    }

    /**
     * dp转px
     */
    static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    // 设置横竖屏
    @Override
    protected void onResume() {
        if (landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (m_webView != null) {
            m_webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            m_webView.clearHistory();
            ViewParent parent = m_webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(m_webView);
            }
            m_webView.destroy();
            m_webView = null;
        }
        instan.setShow(false);
        super.onDestroy();
    }

    private class WebAppInterface {

        public WebAppInterface() {

        }

        @JavascriptInterface
        public String getString() {
            return instan.sendUserDataToJS_infoeditor().toString();
        }


        @JavascriptInterface
        public void setUrlBody(String url, String body) {
            postQuest(instan.m_manualBaseURL + url, body);
        }
    }

    private void AndroidBug5497Workaround() {
        FrameLayout content = (FrameLayout)this.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);// 全屏模式下： return r.bottom
    }

}