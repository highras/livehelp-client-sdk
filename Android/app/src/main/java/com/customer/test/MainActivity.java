package com.customer.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.provider.Settings.Secure;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ilivedata.customer.*;

public class MainActivity extends AppCompatActivity {
    Activity mactivity;
    CustomerData ll;
    EditText appidText ,userIdText ,langText,domainText;

    final String[] buttonNames = {"robot", "faq"};

    private <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    class TestButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.robot:
                    initCustome();
                    ll.robotShow(mactivity);
                    break;
                case R.id.faq:
                    initCustome();
                    ll.faqShow(mactivity);
                    break;
            }
        }
    }


    void initCustome(){
        String userId = userIdText.getText().toString();
        String slang = langText.getText().toString();
        String sdomain = domainText.getText().toString();
        String android_id=Secure.getString(getContentResolver(),Secure.ANDROID_ID);
        String key = BuildConfig.key;
        int pid = BuildConfig.pid;
//        mylog.log("android_id " + android_id);


        ll = CustomerData.getInstance();
        ll.m_robotURL = BuildConfig.roboturl;
        ll.m_manualBaseTail = BuildConfig.baseurl;
        ll.init(getApplicationContext(),sdomain,pid, key, userId,"tom",slang,"", "","", 0, null,null,"");
//        hasInit = true;
//        ll.getUnreadMsg(new IUnreadCallback() {
//            @Override
//            public void getMsg(boolean msg) {
//                mylog.log("unread " + msg);
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mactivity = (Activity) this;
//        String hehe= WebSettings.getDefaultUserAgent(getApplicationContext());
//        mylog.log(hehe);
//        CustomerData.getInstance().alertDialog(this,hehe);

        TestButtonListener testButtonListener = new TestButtonListener();
        for (String name : buttonNames) {
            int buttonId = getResources().getIdentifier(name, "id", getBaseContext().getPackageName());
            Button button = $(buttonId);
            button.setOnClickListener(testButtonListener);
//            testButtons.put(buttonId, name);
        }
        userIdText = findViewById(R.id.textUserId);
        langText = findViewById(R.id.textLang);
        domainText = findViewById(R.id.textdomain);
    }
}
