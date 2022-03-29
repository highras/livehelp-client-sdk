 package com.livehelp;

 import android.app.Activity;
 import android.app.ActivityManager;
 import android.app.AlertDialog;
 import android.app.Application;
 import android.app.Dialog;
 import android.content.Context;
 import android.content.DialogInterface;
 import android.content.Intent;
 import android.content.pm.ApplicationInfo;
 import android.content.pm.PackageInfo;
 import android.content.pm.PackageManager;
 import android.graphics.Color;
 import android.os.BatteryManager;
 import android.os.Build;
 import android.os.Environment;
 import android.os.StatFs;
 import android.provider.Settings;
 import android.text.format.DateFormat;
 import android.text.format.Formatter;
 import android.util.Base64;
 import android.util.Log;
 import android.view.Window;
 import android.view.WindowManager;
 import android.webkit.WebSettings;
 import android.webkit.WebView;
 import android.widget.TextView;

 import androidx.constraintlayout.widget.ConstraintLayout;

 import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

 import java.io.BufferedReader;
 import java.io.BufferedWriter;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.OutputStreamWriter;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.net.HttpURLConnection;
 import java.net.Inet4Address;
 import java.net.InetAddress;
 import java.net.NetworkInterface;
 import java.net.URL;
 import java.nio.charset.StandardCharsets;
 import java.security.MessageDigest;
 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Dictionary;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.atomic.AtomicLong;

 import javax.crypto.Mac;
 import javax.crypto.spec.SecretKeySpec;

 import static android.view.Gravity.CENTER;

 public enum CustomerData {
     INSTANCE;
     volatile boolean hasinitOk = false;
//     Map<String, Map<String, FAQUnit.FAQInfo>> faqMap = new HashMap<>();
     ArrayList<FAQUnit.FAQInfo> showfaqList = new ArrayList<>();
     FAQUnit.LanagePrompt lanagePrompt;
     File FAQfile;
     HashMap<String,String> FAQFileMap = new HashMap<>();
     String m_manualBaseURL;
     String m_manualURL;
     String specailLan = "ar";
     String m_configURL;
     String m_unreadURL;
     String m_userinfoURL;
     String m_faqURL;
     String deviceId = "";
     String sysLanguage = Locale.getDefault().getLanguage();
     String model = Build.MODEL;
     String mANUFACTURER = Build.MANUFACTURER;
     String countryCode = getCountryZipCode();
     String sysVersion = Build.VERSION.RELEASE;
     String titileText = "";
     int   androidAPIVersion = Build.VERSION.SDK_INT;
     String backgroundcolor = "#49ADFF";
     public String SDKVerison = "1.5.0";
     AtomicLong diffTime = new AtomicLong(0);


     void setcolor(Activity activity){
         ConstraintLayout layout = activity.findViewById(R.id.mytitle);
         TextView view = activity.findViewById(R.id.centerTitle);
         int color = Color.parseColor(backgroundcolor);
         layout.setBackgroundColor(color);
         view.setGravity(CENTER);
         view.setTextSize(18);
         view.setTextColor(Color.parseColor("#FFFFFF"));
         view.setText(titileText);
         //5.0及以上
         Window window = activity.getWindow();
         if (window == null)
             return;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
             window.setStatusBarColor(color);
         } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             WindowManager.LayoutParams localLayoutParams = window.getAttributes();
             localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
         }
     }

     void clear(){
         hasinitOk = false;
         m_uId = "";
         m_userName = "";
         m_tags.clear();
         m_customData.clear();
         deviceToken= "";
//         if (faqMap != null)
//             faqMap.clear();
         if (showfaqList != null)
             showfaqList.clear();
     }

     ErrorRecord errorRecord = new ErrorRecord() {
         @Override
         public void recordError(String message) {
             Log.e("customservice",message);
         }
     };

     public String  robotURL = "https://livehelp-edith.ilivedata.com/edith/conversation";
     public String  manualBaseTail = ".livehelp.ilivedata.com";
     public String  serverTimeURL = "https://jarvis.ilivedata.com/timestamp";
//
//     public String  robotURL = "https://jarvis.ilivedata.com/edith/conversation";
//     public String  manualBaseTail = ".jarvis.ilivedata.com";
     private  String m_uId = "";
     String m_greeting=  "";
     private  String m_userName = "";
     private  String m_gameId =  "";
     private  String m_gameVersion = "";
     String m_Lang = "en";
     private  String m_appKey = "";
     private  String m_networkType = "";
     private  String deviceToken = "";
     private  boolean m_isShow = false;
     private  int m_appId =  0;
     private  String m_serverId = "";
     private  List<String>  m_tags = new ArrayList<String>();
     private  int m_vipLevel = 0;
     Context appContext;
     private Map<String, String> m_NonceMap = new HashMap<>();
     private Map<String, String> m_customData = new HashMap<>();
     private String m_session;
     private String version = "1.0.0";

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
         WebSettings settings = viewset.getSettings();
         settings.setUseWideViewPort(true);
         settings.setLoadWithOverviewMode(true);
         settings.setJavaScriptEnabled(true);
         settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
         settings.setTextZoom(100);
         settings.setSupportZoom(true);
         settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//         settings.setDomStorageEnabled(true);
     }

     public void readObject(){
         FileInputStream freader;
         try {
             freader = new FileInputStream(FAQfile);
             ObjectInputStream objectInputStream = new ObjectInputStream(freader);
             FAQFileMap = (HashMap <String, String >)objectInputStream.readObject();
             objectInputStream.close();
         }
         catch (Exception e) {
             errorRecord.recordError("readObject error " + e.getMessage());
         }
     }

     void faqShow(Activity activity){
         if (!hasinitOk) {
             alertDialog(activity,"please call setUserInfo successful first");
             return;
         }
 //        FAQUnit.show(activity);
         try {
             File dir = appContext.getFilesDir();
             if (FAQfile == null)
                 FAQfile = new File(dir.getAbsolutePath() + "/FAQfile");
             if (!FAQfile.exists())
                 FAQfile.createNewFile();
             readObject();

             Intent intent = new Intent(activity, FAQUnit.class);
             activity.startActivity(intent);
         }
         catch (Exception ex){
             errorRecord.recordError("faqShow error " + ex.getMessage());
             alertDialog(activity,"faqShow error " + ex.getMessage());
         }
     }



     void robotShow(final Activity activity){
         if (!hasinitOk) {
             alertDialog(activity,"please call setUserInfo successful first");
             return;
         }

//         if (true){
//             Intent intent = new Intent(activity, RobotActivity.class);
//             activity.startActivity(intent);
//             return;
//         }

         String unreadUrl = m_unreadURL + "?appId=" + m_appId + "&userId=" + m_uId;
         httpRequest(unreadUrl, false, "",new RequestCallback() {
             @Override
             public void onResult(int code, String errMsg, JSONObject respondsDta) {
                 boolean issueExist = false;
                 if (errMsg == null || errMsg.isEmpty()){
                     if (respondsDta.optInt("return_code", -1) == 0) {
                         if (respondsDta.has("data")) {
                             JSONObject responds1 = respondsDta.optJSONObject("data");
                             if (responds1 != null && responds1.has("issueExist")) {
                                 issueExist = responds1.optBoolean("issueExist");
                             }
                         }
                     }
                 }
                 if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                     return;
                 }
                 if (issueExist){
                     Intent intent = new Intent(activity, ManualActivity.class);
                     activity.startActivity(intent);
                 }
                 else {
                     Intent intent = new Intent(activity, RobotActivity.class);
                     activity.startActivity(intent);
                 }
             }
         });
     }

     void manualShow(Activity activity){
         if (!hasinitOk) {
             alertDialog(activity,"please call setUserInfo successful first");
             return;
         }
         Intent intent = new Intent(activity, ManualActivity.class);
         activity.startActivity(intent);
     }

     void getUnreadMsg(final UserInterface.IUnreadCallback callback){
         if (!hasinitOk) {
             callback.getMsg("please call setUserInfo successful first", 0);
             return;
         }
         String unreadUrl = m_unreadURL + "?appId=" + m_appId + "&userId=" + m_uId;
         httpRequest(unreadUrl, false, "",new RequestCallback() {
             @Override
             public void onResult(int code, String errMsg, JSONObject respondsDta) {
                 int unreadCount = 0;
                 String errmsg = "";
                 if (errMsg.isEmpty()){
                     if (respondsDta.optInt("return_code") == 0) {
                         if (respondsDta.has("data")) {
                             JSONObject responds1 = respondsDta.optJSONObject("data");
                             if (responds1 != null && responds1.has("unreadCount")) {
                                 unreadCount = responds1.optInt("unreadCount");
                             }
                         }
                     }
                 }
                 else {
                     errmsg = "getUnreadMsg error " + errMsg;
                 }
                 callback.getMsg(errmsg,unreadCount);
             }
         });
     }

     String inputStreamToString(InputStream ls) {
         String str = "";
         if (ls != null) {
             try {
                 ByteArrayOutputStream result = new ByteArrayOutputStream();
                 byte[] buffer = new byte[1024];
                 int length;
                 while ((length = ls.read(buffer)) != -1) {
                     result.write(buffer, 0, length);
                 }
                 str = result.toString(StandardCharsets.UTF_8.name());
             } catch (Exception e) {
             }
         }
         return str;
     }


     public void alertDialog(final Activity activity, final String str){
 //        Looper.prepare();
 //        new AlertDialog.Builder(activity).setMessage(str).setPositiveButton("确定", new DialogInterface.OnClickListener() {
 //            @Override
 //            public void onClick(DialogInterface dialog, int which) {
 //            }
 //        }).show();
 //        Looper.loop();
         if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
             return;
         }

         activity.runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                 builder.setMessage(str).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
 //                        if (activity.equals(M))
 //                        activity.finish();
                     }
                 });
                 if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                     return;
                 }
                 builder.show();
             }
         });
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

      void init(final Context applicationContext, String domain, int projectId, String projectKey, String lang) {
         appContext = applicationContext;
         m_Lang = lang;
         m_appId = projectId;
         m_appKey = projectKey;
         m_manualBaseURL = "https://" + domain + manualBaseTail;
         m_manualURL = m_manualBaseURL + "/jarvis/conversation";
         m_configURL = m_manualBaseURL + "/api/v1/jarvis/config";
         m_faqURL = m_manualBaseURL + "/api/v1/jarvis/faqs";
         m_unreadURL = m_manualBaseURL + "/api/v1/jarvis/unread";
         m_userinfoURL = m_manualBaseURL + "/api/v1/jarvis/user/info";
         deviceId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
         getConfig(null);
      }


     public  Activity getActivity() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
         Class activityThreadClass = Class.forName("android.app.ActivityThread");
         Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
         Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
         activitiesField.setAccessible(true);
         HashMap activities = (HashMap) activitiesField.get(activityThread);

         for (Object activityRecord : activities.values()) {
             Class activityRecordClass = activityRecord.getClass();
             Field pausedField = activityRecordClass.getDeclaredField("paused");
             pausedField.setAccessible(true);

             if (!pausedField.getBoolean(activityRecord)) {
                 Field activityField = activityRecordClass.getDeclaredField("activity");
                 activityField.setAccessible(true);
                 Activity activity = (Activity) activityField.get(activityRecord);
                 return activity;
             }
         }
         return null;
     }


      void flushTitle(){
          Activity tmp = null;
          try {
              tmp = getActivity();
          }catch (Exception e){
          }
          if (tmp == null)
              return;
          final Activity finalTmp = tmp;
          tmp.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  final TextView view = finalTmp.findViewById(R.id.centerTitle);
                  if (view != null)
                      view.setText(titileText);
              }
          });
        }


      void getConfig(final UserInterface.IUserCallback callback ){
          String configUrl = m_configURL + "?appId=" + m_appId + "&language=" + m_Lang + "&platform=ANDROID";
          httpRequest(configUrl, false, "",new RequestCallback(){
              @Override
              public void onResult(int code, String errMsg, JSONObject respondsDta) {
                  if (errMsg.isEmpty()) {
                      if (respondsDta.optInt("return_code") == 0) {
                          if (respondsDta.has("data")) {
                              JSONObject responds1 = respondsDta.optJSONObject("data");
                              if (responds1 != null && responds1.has("greeting")) {
                                  m_greeting = responds1.optString("greeting");
                              }
                              if (responds1.has("titleBar")) {
                                  JSONObject responds2 = responds1.optJSONObject("titleBar");
                                  if (responds2 != null) {
                                      titileText = responds2.optString("title");
                                      backgroundcolor = responds2.optString("bgColor");
                                      flushTitle();
                                  }
                              }
                          }
                      }
                  }
                  else {
                      errorRecord.recordError("getConfig error " + errMsg);
                  }
                  if (callback != null)
                      callback.onResult(errMsg);
              }
          });
      }

     void setUserInfo(String userId, String userName, String lang, String avatar, String email, List<String> tags, Map<String, String> customData,
                      String deviceToken,final UserInterface.IUserCallback callback){
//         clear();
         m_uId = userId;
         m_tags = tags;
         m_customData = new HashMap<>(customData);
         this.deviceToken = deviceToken;
         m_userName = userName;
         m_Lang = lang;

         final JSONObject postjson = new JSONObject();
         try {
             postjson.put("appId", m_appId);
             postjson.put("userId", m_uId);
             postjson.put("userName", userName);
             postjson.put("avatar", avatar);
             postjson.put("language", m_Lang);
             postjson.put("email", email);
             postjson.put("platform", "Android");
             postjson.put("deviceToken", deviceToken);

         }catch (JSONException ex){
             errorRecord.recordError("setUserInfo error "+  String.format("exception: %s", ex));
             callback.onResult("setUserInfo error "+  String.format("exception: %s", ex));
             return;
         }

         final long sendTime = System.currentTimeMillis();
         getConfig(null);
         httpRequest(serverTimeURL, false, "", new RequestCallback() {
             @Override
             public void onResult(int responseCode, String errMsg, JSONObject ret) {
                 long recieveTime = System.currentTimeMillis();
                 if (errMsg.isEmpty()) {
                     double tt= ret.optDouble("result");
                     long serverTime = (long) (tt*1000);
                     long addtime = recieveTime - (recieveTime-sendTime) / 2 - serverTime;
//                     Log.i("sdktest", "difftime is:" + addtime);
//                     diffTime.set(addtime);
                 }
                 httpRequest(m_userinfoURL + "?appId=" + m_appId, true, postjson.toString(), new RequestCallback() {
                     @Override
                     public void onResult(int responseCode, String errMsg, JSONObject ret) {
                         String msg = "";
                         if (!errMsg.isEmpty()){
                             msg = "post setUserInfo error " + errMsg + "postjson " + postjson.toString();
                             errorRecord.recordError(msg);
                         }
                         else{
                             hasinitOk = true;
                         }
                         callback.onResult(msg);
                     }
                 });
             }
         });
     }

     interface RequestCallback{
         void onResult(int responseCode, String errMsg, JSONObject ret);
     }

     void httpRequest(final String url, final boolean isPost, final String postBody, final RequestCallback callback){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 String errMsg = "";
                 int resultCode = -1;
                 JSONObject retObject = new JSONObject();
                 try {
                     URL sendurl = new URL(url);
                     HttpURLConnection conn = (HttpURLConnection) sendurl.openConnection();
                     conn.setConnectTimeout(15 * 1000);//超时时间
                     conn.setReadTimeout(15 * 1000);
                     if (isPost)
                         conn.setDoOutput(true);
                     conn.setDoInput(true);
                     conn.setUseCaches(false);
                     String currTime = getTime();
                     conn.setRequestProperty("Authorization", getAuth(url, currTime, postBody, isPost));
                     conn.setRequestProperty("X-TimeStamp", currTime);
                     conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                     conn.connect();

                     if (isPost && !postBody.isEmpty()){
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                         writer.write(postBody);
                         writer.close();
                     }
                     resultCode = conn.getResponseCode();
                     StringBuilder builder = new StringBuilder();
                     if (resultCode == HttpURLConnection.HTTP_OK) {
                         String readLine = null;
                         BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                         while ((readLine = responseReader.readLine()) != null) {
                             builder.append(readLine).append("\n");
                         }
                         responseReader.close();
                         retObject = new JSONObject(builder.toString());
                     } else {
                         errMsg = inputStreamToString(conn.getErrorStream());
                     }
                 }catch (Exception ex){
                     errMsg = "httpRequest Exception " + ex.getMessage();
                 }
                 callback.onResult(resultCode, errMsg, retObject);
             }
         }).start();
     }

     private  byte[] sign(byte[] data, byte[] key, String algorithm) {
         try {
             Mac mac = Mac.getInstance(algorithm);
             mac.init(new SecretKeySpec(key, algorithm));
             return mac.doFinal(data);
         } catch (Exception e) {
             errorRecord.recordError("Unable to calculate a signature: " + e.getMessage());
         }
         return null;
     }

     private  String signAndBase64Encode(String data, String key, String algorithm) {
         try {
             byte[] signature = sign(data.getBytes("UTF-8"), key.getBytes("UTF-8"), algorithm);
             return Base64.encodeToString(signature, Base64.DEFAULT);
         } catch (Exception e) {
             errorRecord.recordError("Unable to calculate a request signature: " + e.getMessage());
         }
         return "";
     }

     String getAuth(String url, String time, String body, boolean isPost)
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
         String newparam = "";
         if(index3 == -1)
         {
             path = url.substring(index1);
         }
         else
         {
             path = url.substring(index1, index3);
             param = url.substring(index3 + 1);
             String[] array = param.split("&");
             Arrays.sort(array);
             for (String value: array){
                 newparam = newparam + value + "&";
             }
             if (!newparam.isEmpty() && newparam.endsWith("&")){
                 newparam = newparam.substring(0,newparam.length() - 1);
             }
         }
         try {
             MessageDigest digest = MessageDigest.getInstance("SHA-256");
             StringBuilder data = new StringBuilder();
             if (isPost)
                 data.append("POST").append("\n");
             else
                 data.append("GET").append("\n");
             data.append(host).append("\n");
             data.append(path).append("\n");
             data.append(newparam).append("\n");
             if (isPost)
                 data.append(bytesToHex(digest.digest(body.getBytes("UTF-8")))).append("\n");
             data.append("X-TimeStamp:").append(time);
             String authToken = signAndBase64Encode(data.toString(), getAppKey(), "HmacSHA256");
             return authToken;
         }
         catch(Exception e)
         {
             errorRecord.recordError("Unable to calculate a request signature: " + e.getMessage());
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

      String getManualURL() {
         return  m_manualURL + "?appId=" + m_appId + "&language=" + m_Lang + "&platform=ANDROID" + "&userId=" + m_uId;
     }

     String getRobotURL(){
         String realUrl = robotURL + "?appId=" + m_appId + "&language=" + m_Lang + "&platform=ANDROID" + "&userId=" + m_uId;
         return  realUrl;
     }

     String getFAQURL() {
         String realUrl = m_faqURL + "?appId=" + m_appId + "&language=" + m_Lang;
         return  realUrl;
     }

     String getHelpfulURL(String id, String sectionId) {
         String realUrl = m_faqURL + "?appId=" + m_appId + "&id=" + id + "&language=" + m_Lang +  "&platform=ANDROID"+ "&sectionId=" + sectionId;
         return realUrl;
     }

     String getPostHelpfullBody(String id, String sectionId,String helpful) {
         String realUrl = m_faqURL + "?appId=" + m_appId + "&helpful=" + helpful + "&id=" + id + "&language=" + m_Lang +  "&platform=ANDROID"+ "&sectionId=" + sectionId;
         return  realUrl;
     }

     String getPostBody(String id, String sectionId) {
         String realUrl = "id=" + id + "&sectionId=" + sectionId + "&appId=" + m_appId +  "&language=" + m_Lang +"&platform=ANDROID";;
         return  realUrl;
     }

     String getTime()
     {
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
         sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//         String  restime = sdf.format(new Date());
         String  restime = sdf.format(System.currentTimeMillis() - diffTime.get());
         return restime;

//         Calendar cal = Calendar.getInstance(Locale.US);
//         int zoneOffset = cal.get(Calendar.ZONE_OFFSET);   //取得时间偏移量
//         int dstOffset = cal.get(Calendar.DST_OFFSET); //取得夏令时差
//         cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
//         String s = DateFormat.format("yyyy'-'MM'-'dd'T'hh':'mm':'ss'Z'", cal).toString();
//         return s;
     }

     String getAppKey() {
         return m_appKey;
     }

     String getSession() {
         return m_session;
     }

     Context getContext() { return appContext; }

     boolean getShow() {
         boolean flag = m_isShow;
         m_isShow = true;
         return flag;
     }

     void setShow(boolean show) { m_isShow = show; }


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

     synchronized JSONObject sendUserDataToJS_infoeditor() {
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
             hardwareJson.put("deviceModel",model);
             hardwareJson.put("batteryLevel",batteryLevel);
             hardwareJson.put("batteryStatus",sbettryStatus);
             hardwareJson.put("freeSpace",freeSize);
             hardwareJson.put("totalSpace",totalSize);

             otherJson.put("countryCode",getCountryZipCode());
 //            otherJson.put("fullPrivacyMode",0);
             otherJson.put("host",getLocalIpAddress());
             otherJson.put("osVersion",sysVersion);
             otherJson.put("networkType",m_networkType);
             otherJson.put("platform","ANDROID");
             otherJson.put("sdkVersion",SDKVerison);
             Locale locale = appContext.getResources().getConfiguration().locale;
             String language = locale.getLanguage();
             if (!locale.getCountry() .equals(""))
                 language = language + "_" + locale.getCountry();

             otherJson.put("sysLanguage",language);

             obt.put("application",appJson);
             obt.put("hardware",hardwareJson);
             obt.put("other",otherJson);
             obt.put("tags",tagArray);
             if (m_customData != null)
                 obt.put("custom",new JSONObject(m_customData));
         }
         catch (JSONException e) {
             errorRecord.recordError("sendUserDataToJS_infoeditor error " + e.getMessage());
         }
         return obt;
     }

     String sendUserDataToJS()
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
             userinfo.put("deviceId", deviceId);
             userinfo.put("sysLang", sysLanguage);
             userinfo.put("model", model);
             userinfo.put("manufacturer", mANUFACTURER);
             userinfo.put("sysVersion", sysVersion);
             userinfo.put("os", "Android");
             userinfo.put("countryCode", countryCode);
             userinfo.put("host", getLocalIpAddress());
             ret.put("userInfo", userinfo);
         }
         catch (Exception ex){
             ex.printStackTrace();
             errorRecord.recordError("sendUserDataToJS error " + ex.getMessage());
         }
         return ret.toString();
     }

     synchronized void setUrlInfo(String nonce, String body, String session)
     {
         m_NonceMap.put(nonce, body);
         m_session = session;
     }

     String getUrlBody(String nonce)
     {
         if (m_NonceMap.containsKey(nonce))
         {
             return m_NonceMap.remove(nonce);
         }
         return "";
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

     boolean isMIUI() {
         String manufacturer = Build.MANUFACTURER;
         //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
         if ("xiaomi".equalsIgnoreCase(manufacturer)) {
             return true;
         }
         return false;
     }
     //获取当前系统的语言
     private String getSystemLanguage() {
         return Locale.getDefault().getLanguage();
     }
 }