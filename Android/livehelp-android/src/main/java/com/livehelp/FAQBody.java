package com.livehelp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FAQBody extends Activity {
    CustomerData instan = CustomerData.INSTANCE;
    HashMap<String, String> faqMap = instan.FAQFileMap;
    FAQUnit.FAQInfo info = null;
    TextView texthelpmsg;
    FAQUnit.LanagePrompt remind = instan.lanagePrompt;
    ImageButton textyes, textno;
    Button contactUs;
    String faqId = "";

    String stampToDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_Date = sdf.format(new  Date(time));
        return time_Date;
    }

    void clickHelpful(final String flag) {
        textno.setVisibility(View.INVISIBLE);
        textyes.setVisibility(View.INVISIBLE);
        if (flag.equals("true")) {
            texthelpmsg.setText(remind.clickYes);
//            textyes.setImageResource(R.drawable.helpclick);
        } else {
            texthelpmsg.setText(remind.clickNo);
            contactUs.setVisibility(View.VISIBLE);
        }

        final String postUrl = instan.getPostHelpfullBody(info.id, info.sectionId, flag);

        instan.httpRequest(postUrl, true, "",new CustomerData.RequestCallback() {
            @Override
            public void onResult(int code, String errMsg, JSONObject ret) {
                if (errMsg.isEmpty()){
                    if (ret.optInt("return_code") == 0) {
                        faqMap.put(faqId, flag);
                        instan.writeObject();
                    }
                }
                else {
                    instan.errorRecorder.recordError("postInitBody error " + errMsg);
                }
            }
        });
    }

    void postInitBody(){
        final String postUrl = instan.getHelpfulURL(info.id, info.sectionId);
        instan.httpRequest(postUrl, true, "",new CustomerData.RequestCallback() {
            @Override
            public void onResult(int code,String errMsg, JSONObject ret) {
                if (!errMsg.isEmpty()){
                    instan.errorRecorder.recordError("postInitBody error " + errMsg);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_body);

        instan.setcolor(this);

        String firstTitle = getIntent().getStringExtra("firstTitle");
        String secondTitle = getIntent().getStringExtra("secondTitle");

        if (instan.faqMap.containsKey(firstTitle))
            if (instan.faqMap.get(firstTitle).containsKey(secondTitle))
                info = instan.faqMap.get(firstTitle).get(secondTitle);
        if (info == null){
            instan.errorRecorder.recordError(" can't find FAQInfo firsttitle " + firstTitle + " second title " + secondTitle);
            return;
        }

        faqId = info.id;
        WebView webView = findViewById(R.id.bodyShow);
        postInitBody();

        String viewData = info.body.replace("<img","<img style=\"max-width:100%;height:auto\"");//图片适应宽度
        webView.getSettings().setTextZoom(100);
        webView.loadData(viewData, "text/html; charset=UTF-8", null);
//        webView.loadData(viewData, "text/html", "utf-8");
        contactUs = findViewById(R.id.buttonContact);
        textno = findViewById(R.id.nohelp);
        textyes = findViewById(R.id.help);
        texthelpmsg = findViewById(R.id.textShow);
        TextView time = findViewById(R.id.modifyTime);
        TextView title = findViewById(R.id.title);

        time.setText(stampToDate(info.modifyTime));
        title.setText(secondTitle);
        contactUs.setText(remind.ContactUs);

        texthelpmsg.setText(remind.helpfulQuestion);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickHelpful("false");
            }
        });

        textyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickHelpful("true");
            }
        });

        if (faqMap.containsKey(info.id)) {
            textno.setVisibility(View.INVISIBLE);
            textyes.setVisibility(View.INVISIBLE);
            if (faqMap.get(info.id).equals("true")) {
                contactUs.setVisibility(View.GONE);
                texthelpmsg.setText(remind.clickYes);
            } else {
                contactUs.setVisibility(View.VISIBLE);
                texthelpmsg.setText(remind.clickNo);
            }
        }

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerData.INSTANCE.robotShow(FAQBody.this);
            }
        });
    }
}
