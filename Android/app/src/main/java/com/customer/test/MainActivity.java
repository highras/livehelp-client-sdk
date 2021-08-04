package com.customer.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.provider.Settings.Secure;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ilivedata.customer.*;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity implements CustomeEdittext.OnSuccessListener{
    Activity mactivity;
    SharedPreferences sp;
    CustomerService myservice = new CustomerService();
    CustomeEdittext customProjectId, customUserid, customDomain,customprokey;

    final String[] buttonNames = {"robot", "faq","manualMain","login","loginout"};
    NiceSpinner lanspinner;
    boolean hasgetdefault = false;
    CustomLang langcode = new CustomLang();

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    HashMap<String,CustomeEdittext> textarrays = new HashMap<String,CustomeEdittext>(){{
        put("userID",null);
        put("proid",null);
        put("domain",null);
        put("projectKey",null);
    }};

    void alertDialog(final String str) {
//        Looper.prepare();
//        new AlertDialog.Builder(activity).setMessage(str).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        }).show();
//        Looper.loop();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(mactivity).setMessage(str).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });
    }


    class TestButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login:
                    initCustome();
                    break;
                case R.id.loginout:
                    myservice.resetUserInfo();
                    break;
                case R.id.robot:
                    myservice.showConversation(MainActivity.this, UserInterface.ConversationType.BOT);
                    break;
                case R.id.faq:
                    myservice.showAllFAQs(MainActivity.this);
                    break;
                case R.id.manualMain:
                    myservice.showConversation(MainActivity.this,UserInterface.ConversationType.HUMAN);
            }
        }
    }


    boolean initCustome(){
        saveDB();
        String userId, prokeys,slang, sdomain;
        int pid = 0;
        userId = customUserid.getContent();
        slang = ((CustomLang.CItem)(lanspinner.getSelectedItem())).getValue();
        String android_id=Secure.getString(getContentResolver(),Secure.ANDROID_ID);
        if (BuildConfig.BUILD_TYPE == "devTest" || BuildConfig.BUILD_TYPE == "production"){
            prokeys = BuildConfig.key;
            pid = BuildConfig.pid;
            sdomain = "funplus";
            CustomerData.INSTANCE.robotURL = BuildConfig.roboturl;
            CustomerData.INSTANCE.manualBaseTail = BuildConfig.baseurl;
        }
        else
        {
            sdomain = customDomain.getContent();
            if (sdomain == null || sdomain.isEmpty()){
                alertDialog("please input domain");
                return false;
            }
            prokeys = customprokey.getContent();

            if(prokeys == null || prokeys.isEmpty()){
                alertDialog("please input project key");
                return false;
            }

            String spid = customProjectId.getContent();
            if (spid == null || spid.isEmpty()){
                alertDialog("please input project id");
                return false;
            }
            pid = Integer.parseInt(spid);
        }

        if( userId.isEmpty() || sdomain.isEmpty() || pid ==0 || slang.isEmpty()){
            alertDialog("用户id 项目id 项目域名 语言 有空值");
            return false;
        }

        List<String> tags = new LinkedList<>();
        Map<String, String> tcustomdata = new HashMap<>();

        myservice.init(getApplicationContext(),pid, prokeys, sdomain, slang);

        myservice.setUserInfo("888","tom","werwer.com","163.com",tags,tcustomdata,"werlkjwelr",
                new UserInterface.IUserCallback(){
                    @Override
                    public void onResult(final String errmsg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (errmsg.isEmpty())
                                    Toast.makeText(getApplicationContext(), "login  ok" + errmsg, Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getApplicationContext(), "login  failed " + errmsg, Toast.LENGTH_SHORT).show();
                            }});
                    }
                });
        return true;
    }

    void getDB(){
        String userid= sp.getString("userid","");
        String projectkey= sp.getString("projectkey","");
        String domain= sp.getString("domain","");
        String projectid=sp.getString("projectid","");
        customUserid.edt_content.setText(userid);
        customProjectId.edt_content.setText(projectid);
        customDomain.edt_content.setText(domain);
        customprokey.edt_content.setText(projectkey);

//        customUserid.setsBottomText(userid);
//        customProjectId.setsBottomText(projectid);
//        customDomain.setsBottomText(domain);
//        customprokey.setsBottomText(projectkey);
    }

    void saveDB(){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("userid",customUserid.getContent());
        editor.putString("projectkey",customprokey.getContent());
        editor.putString("domain",customDomain.getContent());
        editor.putString("projectid",customProjectId.getContent());
        editor.commit();            //写入数据
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hasgetdefault)
                    return;
                getDB();
                hasgetdefault = true;
            }
        }, 200L);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp=getSharedPreferences("Logindb",MODE_PRIVATE);

        LinkedList<CustomLang.CItem> lanarray = langcode.getlangcode();
        int ii = lanarray.indexOf(new CustomLang.CItem("English","en"));
//        int ii = lanarray.indexOf(new CustomLang.CItem("Simplified Chinese","zh-CN"));
        lanspinner = findViewById(R.id.nice_spinner);
//        List<String> dataset = new LinkedList<>(lanarray);
        lanspinner.attachDataSource(lanarray); //设置下拉框要显示的数据集合
        lanspinner.setBackgroundResource(R.drawable.shape_nicespinner); //设置控件的形状和背景
        if (ii > 0)
            lanspinner.setSelectedIndex(ii);


        customUserid = $(R.id.userID);
        customUserid.setOnSuccessListener(this);

        customProjectId = $(R.id.proid);
        customProjectId.setOnSuccessListener(this);

        customDomain = $(R.id.domain);
        customDomain.setOnSuccessListener(this);

        customprokey = $(R.id.projectKey);
        customprokey.setOnSuccessListener(this);

        mactivity = (Activity) this;
//        String hehe= WebSettings.getDefaultUserAgent(getApplicationContext());
//        mylog.log(hehe);
//        CustomerData.getInstance().alertDialog(this,hehe);

        TestButtonListener testButtonListener = new TestButtonListener();
        for (String name : buttonNames) {
            int buttonId = getResources().getIdentifier(name, "id", getBaseContext().getPackageName());
            Button button = $(buttonId);
            button.setOnClickListener(testButtonListener);
        }
    }
    @Override
    public void onSuccess(String phone) {

    }
}