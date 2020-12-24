package com.ilivedata.customer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.STORAGE_STATS_SERVICE;

public class CustomerData {
    private static CustomerData instance = null;
    public Map<String, Map<String, FAQUnit.FAQInfo>> faqMap;
    public FAQUnit.LanagePrompt lanagePrompt;
    public File FAQfile;
    public Map<String,String> FAQFileMap;
    public String   m_manualBaseURL;
    public String   m_manualURL;
    public String   m_unreadURL;
    public String   m_faqURL;

    private  String m_FAQURLtail = "/api/v1/jarvis/faqs";
    private String  m_robotURL = "https://livehelp-edith.ilivedata.com/edith/chat.html";
//    private String  m_robotURL = "https://livehelp-edith.ilivedata.com/edith/conversation";
    private String  m_manualURLtail = "/jarvis/conversation";
    private String  m_unreadURLtail = "/api/v1/jarvis/config";
    public String   m_manualBaseTail = ".livehelp.ilivedata.com";

    private  String m_uId;
    private  String m_greeting="";//欢迎语 第一次拉
    private  String m_userName;
    private  String m_gameId;
    private  String m_gameVersion;
    private  String m_Lang;
    private  String m_appKey;
    private  String m_networkType;
    private  String deviceToken;
    private  boolean m_isShow;
    private  int m_appId;
    private  String m_serverId;
    private  String[] m_tags;
    private  int m_vipLevel;
    private  Context appContext;
    private Map<String, String> m_NonceMap = new HashMap<String, String>();
    private Map<String, String> m_customData;
    private String m_session;
    private String version = "1.0.0";

    private CustomerData() {
        m_uId = "";
        m_userName = "";
        m_gameId = "";
        m_gameVersion = "";
        m_Lang = "en";
        m_appKey = "";
        m_isShow = false;
        m_appId = 0;
        m_vipLevel = 0;
        m_session = "";
        faqMap = new HashMap<>();
        FAQFileMap = new HashMap<>();
    }

    public static CustomerData getInstance() {
        if (instance == null) {
            synchronized (CustomerData.class) {
                if (instance == null) {
                    instance = new CustomerData();
                }
            }
        }
        return instance;
    }

    public void writeObject() {
        try {
            FileOutputStream outStream = new FileOutputStream(FAQfile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);

            objectOutputStream.writeObject(FAQFileMap);
            outStream.close();
        } catch (FileNotFoundException e) {
            Log.e("customsdk", "writeObject error " + e.getMessage());
        } catch (IOException e) {
            Log.e("customsdk", "writeObject error " + e.getMessage());
        }
    }

    void webSeting(WebView viewset){
        viewset.setHorizontalScrollBarEnabled(false);//水平不显示
        viewset.setVerticalScrollBarEnabled(false); //垂直不显示
        viewset.getSettings().setJavaScriptEnabled(true);
        viewset.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        viewset.getSettings().setSupportZoom(true);
        viewset.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    public void readObject(){
        FileInputStream freader;
        try {
            freader = new FileInputStream(FAQfile);
            ObjectInputStream objectInputStream = new ObjectInputStream(freader);
            FAQFileMap = (HashMap <String, String >)objectInputStream.readObject();
            objectInputStream.close();
        }
        catch (FileNotFoundException e) {
            Log.e("customsdk", "readObject error " + e.getMessage());
        } catch (IOException e) {
            Log.e("customsdk", "readObject error " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("customsdk", "readObject error " + e.getMessage());
        }
    }

    public  void faqShow(Activity activity){
        FAQUnit.show(activity);
    }

    public  void robotShow(Activity activity){
        RobotActivity.show(activity, true);
    }

    public void getUnreadMsg(final IUnreadCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                try {
                    String realUrl = m_unreadURL + "?appId=" + m_appId + "&deviceToken=" + deviceToken + "&language=" + m_Lang + "&platform=ANDROID" +  "&userId=" + m_uId;

                    URL urlObject = new URL(realUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                    connection.setDoOutput(false);
                    // 设置是否从httpUrlConnection读入
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(20 * 1000);
                    connection.setReadTimeout(20 * 1000);
                    String time = getTime();
                    connection.setRequestProperty("Authorization", getAuth(realUrl, time, "","GET"));
                    connection.setRequestProperty("X-TimeStamp", time);
                    connection.connect();
                    int code = connection.getResponseCode();
                    StringBuilder builder = new StringBuilder();

                    if (code == 200) { // 正常响应
                        // 从流中读取响应信息
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) { // 循环从流中读取
                            builder.append(line);
                        }
                        reader.close();
                        JSONObject respondsDta = new JSONObject(builder.toString());
                        if (respondsDta.has("data")){
                            JSONObject responds1 =  respondsDta.getJSONObject("data");
                            if (responds1.has("unread")){
                                flag = responds1.getBoolean("unread");
                            }
                        }
                    }
                    else
                    {
                        Log.e("customsdk", "getUnreadMsg http return error " + code);
                    }
                } catch (Exception ex) {
                    Log.e("customsdk", "getUnreadMsg error " + ex.getMessage());
                }
                callback.getMsg(flag);
            }
        }).start();
    }

    void getUnread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String realUrl = m_unreadURL + "?appId=" + m_appId + "&deviceToken=" + deviceToken + "&language=" + m_Lang + "&platform=ANDROID" +  "&userId=" + m_uId;

                    URL urlObject = new URL(realUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                    connection.setDoOutput(false);
                    // 设置是否从httpUrlConnection读入
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(20 * 1000);
                    connection.setReadTimeout(20 * 1000);
                    String time = getTime();
                    connection.setRequestProperty("Authorization", getAuth(realUrl, time, "","GET"));
                    connection.setRequestProperty("X-TimeStamp", time);
                    connection.connect();
                    int code = connection.getResponseCode();
                    StringBuilder builder = new StringBuilder();

                    if (code == 200) { // 正常响应
                        // 从流中读取响应信息
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) { // 循环从流中读取
                            builder.append(line);
                        }
                        reader.close();
                        JSONObject respondsDta = new JSONObject(builder.toString());
                        if (respondsDta.has("data")){
                            JSONObject responds1 =  respondsDta.getJSONObject("data");
                            if (responds1.has("greeting")){
                                m_greeting = responds1.getString("greeting");
                            }
                        }
                    }
                    else {
                        Log.e("customsdk", "getUnread http return error " + code);
                    }
                } catch (Exception ex) {
                    Log.e("customsdk", "getUnread error " + ex.getMessage());
                }
            }
        }).start();
    }

    public static Map convertDictionaryToMap(Dictionary dict) {
        Map result = new HashMap();
        for (Enumeration e = dict.keys(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            Object value = dict.get(key);
            result.put(key, String.valueOf(value));
        }
        return result;
    }

    /**
     * @context    应用的appliaction(必传)
     * @param _domain  后台配置的项目域名(必传)
     * @param projectId    项目id(必传)
     * @param projectKey    项目key(必传)
     * @param userId    用户id(必传)
     * @param userName  用户名称
     * @param gameLanguage  控制台配置的语言(必传)
     * @param gameId    //游戏应用商店ID
     * @param serverId  //当前区服ID
     * @param networkType   网络类型
     * @param vipLevel  vip等级
     * @param tags      客诉标签(来自控制台标签)
     * @param customData 自定义参数
     * @param _deviceToken 推送token
     */
    public synchronized boolean init(Context context, String _domain, int projectId, String projectKey, String userId, String userName, String gameLanguage, String gameId, String serverId, String networkType, int vipLevel, String[] tags, Map<String,String> customData,String _deviceToken) {
        if (context == null || _domain.isEmpty() || projectId == 0 || gameLanguage.isEmpty() || projectKey.isEmpty() || userId.isEmpty() ) {
            Log.e("customsdk","param init error");
            return false;
        }
        appContext = context;
        m_appId = projectId;
        m_appKey = projectKey;
        m_uId = userId;
        m_Lang = gameLanguage;
        m_vipLevel = vipLevel;
        m_gameId = gameId;
        m_userName = userName;
        m_serverId = serverId;
        m_networkType = networkType;
        m_tags = tags;
        deviceToken = _deviceToken;
        m_customData = customData;
        m_manualBaseURL = "https://" + _domain + m_manualBaseTail;
        m_manualURL = m_manualBaseURL + m_manualURLtail;
        m_unreadURL = m_manualBaseURL + m_unreadURLtail;
        m_faqURL = m_manualBaseURL + m_FAQURLtail;
        getUnread();
        return true;
    }

    private  static byte[] sign(byte[] data, byte[] key, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (Exception e) {
            Log.e("customsdk","Unable to calculate a signature: " + e.getMessage());
        }
        return null;
    }

    private  static String signAndBase64Encode(String data, String key, String algorithm) {
        try {
            byte[] signature = sign(data.getBytes("UTF-8"), key.getBytes("UTF-8"), algorithm);
            return Base64.encodeToString(signature, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("customsdk","Unable to calculate a request signature: " + e.getMessage());
        }
        return "";
    }

    static String getAuth(String url, String time, String body, String connectType)
    {
        int index = url.indexOf("//");
        if(index == -1)
        {
            return "";
        }
        int index1 = url.indexOf("/", index + 2);
        if(index1 == -1)
        {
            return "";
        }
        String host = url.substring(index + 2, index1);
        int index2 = host.indexOf(":");
        if(index2 != -1)
        {
            host = host.substring(0, index2 );
        }

        String path = "";
        String param = "";
        int index3 = url.indexOf("?", index1);
        if(index3 == -1)
        {
            path = url.substring(index1);
        }
        else
        {
            path = url.substring(index1, index3);
            param = url.substring(index3 + 1);
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder data = new StringBuilder();
            data.append(connectType).append("\n");
            data.append(host).append("\n");
            data.append(path).append("\n");
            data.append(param).append("\n");
            if (connectType.equals("POST"))
                data.append(bytesToHex(digest.digest(body.getBytes("UTF-8")))).append("\n");
            data.append("X-TimeStamp:").append(time);
            String authToken = signAndBase64Encode(data.toString(), CustomerData.getInstance().getAppKey(), "HmacSHA256");
            return authToken;
        }
        catch(Exception e)
        {
            Log.e("customsdk","Unable to calculate a request signature: " + e.getMessage());
        }
        return "";
    }

    static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * dp转px
     */
    static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    void adjustViewHeight(Context context, View  view, boolean isRobot) {
        int statusBarHeight = getStatusBarHeight(context);
        int availableHeight = getScreenInfo(context).heightPixels;
        int realHeight = getRealScreenInfo(context).heightPixels;
        int navagarHeight = getNavigationBarHeight(context);
        boolean navagarShow = false;
        ConstraintLayout.LayoutParams jj = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        int heigh = availableHeight - dp2px(context,45) - dp2px(context, 15); //返回控件高度
        if (!isRobot)
            heigh = availableHeight - dp2px(context,45); //返回控件高度

        if (navagarHeight == 0 || realHeight - availableHeight == statusBarHeight) //没有虚拟按键
        {

        }
//        else if (realHeight - availableHeight == navagarHeight) {//华为平板
//            heigh =
//        }
        jj.height = heigh;
        view.setLayoutParams(jj);
    }

    synchronized String getManualURL() {
        return  m_manualURL;
    }

    synchronized String getRobotURL() {
        return  m_robotURL;
    }

    synchronized String getFAQURL() {
        String realUrl = m_faqURL + "?appId=" + m_appId + "&language=" + m_Lang;
        return  realUrl;
    }

    synchronized String getHelpfulURL(String id, String sectionId) {
        String realUrl = m_faqURL + "?appId=" + m_appId + "&id=" + id + "&language=" + m_Lang +  "&platform=ANDROID"+ "&sectionId=" + sectionId;
        return realUrl;
    }

    synchronized String getPostHelpfullBody(String id, String sectionId,String helpful) {
        String realUrl = m_faqURL + "?appId=" + m_appId + "&helpful=" + helpful + "&id=" + id + "&language=" + m_Lang +  "&platform=ANDROID"+ "&sectionId=" + sectionId;
        return  realUrl;
    }

    synchronized String getPostBody(String id, String sectionId) {
        String realUrl = "id=" + id + "&sectionId=" + sectionId + "&appId=" + m_appId +  "&language=" + m_Lang +"&platform=ANDROID";;
        return  realUrl;
    }

    //全面屏没有设置虚拟按键的情况下 我的手机底部会有个黑边 这个高度 设置虚拟按键 等于黑边+实际虚拟按键高度
    int getNavigationBarHeight(Context context) {
        int result = 0;
        if (checkDeviceHasNavigationBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    //获取真实高度 带虚拟按键
    static DisplayMetrics getScreenInfo(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(outMetrics);
        }
        return outMetrics;
    }

    static DisplayMetrics getRealScreenInfo(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getRealMetrics(outMetrics);
        }
        return outMetrics;
    }


    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    static String getTime()
    {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        String s = DateFormat.format("yyyy'-'MM'-'dd'T'kk':'mm':'ss'Z'", cal).toString();
        return s;
    }

    public synchronized void setLang(String gLang) {
        m_Lang = gLang;
    }

    public synchronized void setAppid(int appid) {
        m_appId = appid;
    }

    public  synchronized void setVipLevel(int level) {
        m_vipLevel = level;
    }

    public synchronized void setGameId(String id) { m_gameId = id; }

    public synchronized void setGameVersion(String version) {
        m_gameVersion = version;
    }

    public synchronized void setUid(String id) { m_uId = id; }

    public synchronized void setUserName(String userName) {
        m_userName = userName;
    }

    public  synchronized int getAppid() {
        return m_appId;
    }

    public synchronized String getAppKey() {
        return m_appKey;
    }

    public synchronized String getLang() {
        return m_Lang;
    }

    public synchronized String getSession() {
        return m_session;
    }

    public synchronized Context getContext() { return appContext; }

    public synchronized boolean getShow() {
        boolean flag = m_isShow;
        m_isShow = true;
        return flag;
    }

    public synchronized void setShow(boolean show) { m_isShow = show; }


    //获取app包名
     String getAppProcessName() {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    /**
     * 获取程序的版本号
     * @param packname
     * @return
     */
    public String getAppVersion(String packname){
        //包管理操作管理类
        PackageManager pm = appContext.getPackageManager();
        PackageInfo packinfo = null;
        try {
            packinfo = pm.getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packinfo.versionName;
    }


    /**
     * 获取程序的名字
     * @param packname
     * @return
     */
    public String getAppName(String packname){
        //包管理操作管理类
        PackageManager pm = appContext.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = pm.getApplicationInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.loadLabel(pm).toString();
    }

    public long getAvailableInternalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public String getDataTotalSize(){
        StatFs sf = new StatFs(appContext.getCacheDir().getAbsolutePath());
        long blockSize = sf.getBlockSize();
        long totalBlocks = sf.getBlockCount();
        return Formatter.formatFileSize(appContext, blockSize*totalBlocks);
    }

    synchronized JSONObject sendUserDataToJS_infoeditor()
    {
        JSONObject  obt = new JSONObject();;
        try {
            JSONObject appJson = new JSONObject();
            JSONObject hardwareJson = new JSONObject();
            JSONObject otherJson = new JSONObject();
            JSONArray tagArray = new JSONArray();
            if (m_tags != null)
                tagArray = new JSONArray(m_tags);

            String appPackageName = getAppProcessName();
            String appName = getAppName(appPackageName);
            String appVersion = getAppVersion(appPackageName);
            appJson.put("appId", m_appId);
            appJson.put("appIdentifier", appPackageName);
            appJson.put("appName", appName);
            appJson.put("appVersion", appVersion);
            appJson.put("greeting", m_greeting);
            appJson.put("language", m_Lang);
            appJson.put("userId", m_uId);
            appJson.put("userName", m_userName);
            appJson.put("serverId", m_serverId);


            String batteryLevel = "100%";
            String sbettryStatus = "FULL";
            int bettrystatus = 0;
            BatteryManager batteryManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                batteryManager = appContext.getSystemService(BatteryManager.class);
            }
            else{
                batteryManager = (BatteryManager)appContext.getSystemService(Context.BATTERY_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int batLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                batteryLevel = batLevel + "%";
                bettrystatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
            }

            switch(bettrystatus){
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    sbettryStatus = "UNKNOWN";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    sbettryStatus = "CHARGING";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    sbettryStatus = "DISCHARGING";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    sbettryStatus = "FULL";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    sbettryStatus = "NOT_CHARGING";
                    break;
            }

            String freeSize = String.valueOf(getAvailableInternalSize());
            String totalSize = getDataTotalSize();
            hardwareJson.put("deviceModel",getSystemModel());
            hardwareJson.put("batteryLevel",batteryLevel);
            hardwareJson.put("batteryStatus",sbettryStatus);
            hardwareJson.put("freeSpace",freeSize);
            hardwareJson.put("totalSpace",totalSize);

            otherJson.put("countryCode",getCountryZipCode());
//            otherJson.put("fullPrivacyMode",0);
            otherJson.put("host",getLocalIpAddress());
            otherJson.put("osVersion",getVersion());
            otherJson.put("networkType",m_networkType);
            otherJson.put("platform","ANDROID");
            otherJson.put("sdkVersion","1.0.0");
            Locale locale = appContext.getResources().getConfiguration().locale;
            String language = locale.getLanguage();
            if (!locale.getCountry() .equals(""))
                language = language + "_" + locale.getCountry();

            otherJson.put("sysLanguage",language);
//            otherJson.put("yearClass",2018);

            obt.put("application",appJson);
            obt.put("hardware",hardwareJson);
            obt.put("other",otherJson);
            obt.put("tags",tagArray);
            if (m_customData != null)
                obt.put("custom",m_customData.toString());
        }
        catch (JSONException e)
        {
            Log.e("customsdk", "sendUserDataToJS_infoeditor error " + e.getMessage());
        }
        return obt;
    }

    synchronized String sendUserDataToJS()
    {
        JSONObject ret = sendUserDataToJS_infoeditor();
        try {
            JSONObject userinfo = new JSONObject();
            userinfo.put("appId", m_appId);
            userinfo.put("userId", m_uId);
            userinfo.put("userName", m_userName);
            userinfo.put("gameId", m_gameId);
            userinfo.put("gameVersion", m_gameVersion);
            userinfo.put("gameLang", m_Lang);
            userinfo.put("vipLevel", m_vipLevel);
            userinfo.put("deviceId", getDeviceId());
            userinfo.put("sysLang", getSystemLanguage());
            userinfo.put("model", getSystemModel());
            userinfo.put("manufacturer", getDeviceManufacturer());
            userinfo.put("sysVersion", getVersion());
            userinfo.put("os", "Android");
            userinfo.put("countryCode", getCountryZipCode());
            userinfo.put("host", getLocalIpAddress());
            ret.put("userInfo", userinfo);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return ret.toString();
    }

    synchronized void setUrlInfo(String nonce, String body, String session)
    {
        m_NonceMap.put(nonce, body);
        m_session = session;
    }

    synchronized String getUrlBody(String nonce)
    {
        if (m_NonceMap.containsKey(nonce))
        {
            return m_NonceMap.remove(nonce);
        }
        return "";
    }

    //获取Android发布的版本
    private  String getVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    //获取SDK的API Level
    private  int getSDK() {
        return android.os.Build.VERSION.SDK_INT;
    }

    static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /*
     * 获取国家码
     * */
    String getCountryZipCode()
    {
        String  CountryZipCode = "";
        CountryZipCode = Locale.getDefault().getCountry();

        return CountryZipCode;
    }

    // 获取手机厂商
    private String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }


    //获取手机型号
    private String getSystemModel() {
        return android.os.Build.MODEL;
    }

    //获取当前系统的语言
    private String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    // 获取设备id
    private String getDeviceId() {
        return Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}