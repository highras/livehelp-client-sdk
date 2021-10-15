package com.livehelp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class RobotActivity extends Activity {
    CustomerData instan = CustomerData.INSTANCE;
//    private boolean landscape;
    private ConstraintLayout layoutMain;
    private ImageView imageButton;
    private ImageView contactManual;
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
        mUploadMessage = null;
        mFilePathCallback = null;
        m_webView = null;
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //隐藏虚拟按键
//        Window _window = getWindow();
//        WindowManager.LayoutParams params = _window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
//        _window.setAttributes(params);

        super.onCreate(savedInstanceState);
        if (instan.m_Lang.equals(instan.specailLan)){
            setContentView(R.layout.activity_robot_ar);
        }
        else
            setContentView(R.layout.activity_robot);

        //        getSupportActionBar().hide();
        instan.setcolor(this);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setFinishOnTouchOutside(false);
        initViews();
//        AndroidBug5497Workaround();
        loadData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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



    private void setPicAttr(WebView view){
        view.setWebChromeClient(new WebChromeClient() {

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

    }


    private void showPostResponseJs(final WebView view, final String content, final int errcode){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String script = "javascript:ldChat.platform.response(" + content + "," + errcode + ")";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//sdk>19才有用
                        view.evaluateJavascript(script, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String Str) {
//                        Toast.makeText(getApplicationContext(), "我是android调用js方法,返回结果是" + Str, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        view.loadUrl(script);
                    }
                }
                catch (Exception ex){

                }
            }
//                m_webView.loadData(content,"text/html", "utf-8");
        });
    }


    private void addWebview(String url){
        WebView jj  = new WebView(this);
        jj.setLayoutParams(m_webView.getLayoutParams());
        jj.getSettings().setJavaScriptEnabled(true);
        jj.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        jj.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        jj.getSettings().setDomStorageEnabled(false);
        jj.setWebViewClient(new WebViewClient() {
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    WebView.HitTestResult hitTestResult = view.getHitTestResult();
                                    if (hitTestResult == null) {
                                        return false;
                                    }
                                    if (hitTestResult.getType() == WebView.HitTestResult.UNKNOWN_TYPE) {
                                        return false;
                                    }
                                    addWebview(url);
                                    return true;
                                }});
        setPicAttr(jj);
        layoutMain.addView(jj,++webviewCount);
        layoutMain.getChildAt(layoutMain.getChildCount() -2).setVisibility(View.GONE);
        jj.loadUrl(url);
    }




    //初始化WebView
    private void initViews() {
        layoutMain = findViewById(R.id.manrobot);
        imageButton = findViewById(R.id.back);

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
        contactManual = findViewById(R.id.gotoManual);
        contactManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RobotActivity.this, ManualActivity.class);
                startActivity(intent);
            }
        });

        m_webView = findViewById(R.id.robotwebview);

        instan.webSeting(m_webView);
        m_webView.addJavascriptInterface(new WebAppInterface(), "edithData");


//        Log.i("sdktest","enablejavascript " + m_webView.getSettings().getJavaScriptEnabled());

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
//                view.loadUrl(url);
                if (url.indexOf("arvis.ilivedata.com/jarvis/conversation") != -1 || url.indexOf("livehelp.ilivedata.com/jarvis/conversation") != -1){
                    Intent intent = new Intent(RobotActivity.this, ManualActivity.class);
                    startActivity(intent);
                }
                else {
                    addWebview(url);
                }
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if(url.indexOf("/api/v1/edith") != -1)
                {
                    try {
                        String nonce = "";
                        String newUrl = url;
                        int index3 = url.indexOf("?");
                        if(index3 != -1)
                        {
                            newUrl = url.substring(index3 +1);
                            String[] array = newUrl.split("&");
                            newUrl = "";
                            Arrays.sort(array);
                            for (String value: array){
                                newUrl = newUrl + value + "&";
                                int nonceIndex = value.indexOf("=");
                                if (nonceIndex != -1){
                                    if (value.substring(0,nonceIndex).equals("nonce")){
                                        nonce = value.substring(nonceIndex+1);
                                    }
                                }
                            }
                            String tt = newUrl.substring(0,newUrl.length()-1);
                            newUrl = url.substring(0,index3) + "?" + tt;
                        }

                        URL urlR= new URL(newUrl);

                        HttpURLConnection connection= (HttpURLConnection) urlR.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setRequestMethod("POST");

                        String time = instan.getTime();
                        String body = instan.getUrlBody(nonce);
//                        String body = "{}";
                        String session = instan.getSession();
                        if(session.length() != 0)
                        {
                            connection.setRequestProperty("X-Session-ID", session);
                        }
                        connection.setRequestProperty("Authorization", instan.getAuth(newUrl, time, body,true));
                        connection.setRequestProperty("X-TimeStamp", time);
                        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                        connection.connect();
                        if(body.length() != 0)
                        {
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                            writer.write(body);
                            writer.close();
                        }

                        int responseCode = connection.getResponseCode();
                        if(responseCode == HttpURLConnection.HTTP_OK){
                            return new WebResourceResponse("text/html", "utf-8",connection.getInputStream());
                        }
                        else {
                            String str = instan.inputStreamToString(connection.getErrorStream());
                            instan.errorRecord.recordError("robot init view error:" +  +responseCode + "-" + str);
                            instan.alertDialog(RobotActivity.this, "robot init view error : " +responseCode + "-" + str + "\nrequest body " + body);
                        }
                    } catch (Exception e) {
                        String msg = "post robot error :" + e.getMessage();
                        instan.errorRecord.recordError(msg);
                        instan.alertDialog(RobotActivity.this, "robot init view error :\n " +msg);
                    }
                }
                return null;
            }

        });

        m_webView.setWebChromeClient(new WebChromeClient() {

//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                String message = consoleMessage.message();
//                int lineNumber = consoleMessage.lineNumber();
//                String sourceID = consoleMessage.sourceId();
//                String messageLevel = consoleMessage.message();
//
//                Log.i("[sdktest]", String.format("robot [%s] sourceID: %s lineNumber: %n message: %s",
//                        messageLevel, sourceID, lineNumber, message));
//
//                return super.onConsoleMessage(consoleMessage);
//            }

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
        String webViewUrl = instan.getRobotURL();
        if (!TextUtils.isEmpty(webViewUrl) && m_webView != null)
            m_webView.loadUrl(webViewUrl);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    // 设置横竖屏
    @Override
    protected void onResume() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onResume();
        m_webView.onResume();
        m_webView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        //暂停WebView在后台的所有活动
//        m_webView.onPause();
//        //暂停WebView在后台的JS活动
//        m_webView.pauseTimers();
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
//            m_webView = null;
        }
        instan.setShow(false);
        super.onDestroy();
    }

    private class WebAppInterface {

        public WebAppInterface() {

        }

        @JavascriptInterface
        public String getString() {
            return instan.sendUserDataToJS();
        }

        @JavascriptInterface
        public void setUrlBody(String nonce, String body, String session) {
            instan.setUrlInfo(nonce, body, session);
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