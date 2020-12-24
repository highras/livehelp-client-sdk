package com.customer.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.provider.Settings.Secure;
import androidx.appcompat.app.AppCompatActivity;

import com.ilivedata.customer.*;

import java.util.HashMap;

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
//        mylog.log("android_id " + android_id);


        ll = CustomerData.getInstance();
        slang = "zh-CN";
//        ll.init(testXContext,sdomain,80900005, "nQVi/NlbIF8wEiHCxZL+YvWUB0atgEe/x8DWAauPnR4=", userId,"tom",slang,"", "","", 0, null,null,"");
        ll.init(getApplicationContext(),sdomain,80900001, "61hMzMf0lNTnsccFKRbZGdA8E/qtT/O7HkujsYkaAE8=", userId,"tom",slang,"", "","", 0, null,null,"");
//        ll.init(90900003, "vmolgNlPcKwu2fGPZcHL9BoTguixV/FM0hEa8ztVkB0=", userId,"tom",slang,"232", "34543","wifi", 2, new String[]{"1","2"},new HashMap<String, String>());
//        hasInit = true;
        ll.getUnreadMsg(new IUnreadCallback() {
            @Override
            public void getMsg(boolean msg) {
                mylog.log("unread " + msg);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mactivity = (Activity) this;

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
