package com.livehelp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FAQBody extends Activity {
    CustomerData instan = CustomerData.INSTANCE;
    HashMap<String, String> faqMap = instan.FAQFileMap;
    FAQUnit.FAQInfo.FAQINNER info = null;
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
                    instan.errorRecord.recordError("postInitBody error " + errMsg);
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
                    instan.errorRecord.recordError("postInitBody error " + errMsg);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (instan.m_Lang.equals(instan.specailLan))
            setContentView(R.layout.activity_faq_body_ar);
        else
            setContentView(R.layout.activity_faq_body);

        instan.setcolor(this);


        String firstTitle = getIntent().getStringExtra("firstTitle");
        String secondTitle = getIntent().getStringExtra("secondTitle");

//        if (instan.faqMap.containsKey(firstTitle))
//            if (instan.faqMap.get(firstTitle).containsKey(secondTitle))
//                info = instan.faqMap.get(firstTitle).get(secondTitle);
        for (FAQUnit.FAQInfo hehe: instan.showfaqList){
            if (hehe.firstTitle.equals(firstTitle)) {
                for (FAQUnit.FAQInfo.FAQINNER tt : hehe.realInfo) {
                    if (tt.secondTitle.equals(secondTitle))
                        info = tt;
                }
            }
        }
        if (info == null){
            instan.errorRecord.recordError(" can't find FAQInfo firsttitle " + firstTitle + " second title " + secondTitle);
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

        /*if (instan.m_Lang.equals(instan.specailLan)){
            ConstraintSet jj = new ConstraintSet();
            jj.clone((ConstraintLayout) findViewById(R.id.mytitle));
            jj.clear(R.id.back, ConstraintSet.START);
            jj.clear(R.id.back, ConstraintSet.END);
            jj.connect(R.id.back, ConstraintSet.END, R.id.mytitle, ConstraintSet.END, 15);
            jj.connect(R.id.back, ConstraintSet.TOP, R.id.mytitle, ConstraintSet.TOP, 3);
            jj.applyTo(this.<ConstraintLayout>findViewById(R.id.mytitle));
            back.setImageResource(R.drawable.back_ar);
            title.setGravity(Gravity.RIGHT);
            time.setGravity(Gravity.RIGHT);
            title.setPadding(0,0,10,0);
            time.setPadding(0,0,10,0);


            ConstraintSet kk = new ConstraintSet();
            kk.clone((ConstraintLayout) findViewById(R.id.bottomLayout));
            kk.clear(R.id.nohelp, ConstraintSet.START);
            kk.clear(R.id.nohelp, ConstraintSet.END);

            kk.clear(R.id.help, ConstraintSet.START);
            kk.clear(R.id.help, ConstraintSet.END);

            kk.clear(R.id.textShow, ConstraintSet.START);
            kk.clear(R.id.textShow, ConstraintSet.END);

            kk.clear(R.id.buttonContact, ConstraintSet.START);
            kk.clear(R.id.buttonContact, ConstraintSet.END);

            kk.connect(R.id.buttonContact, ConstraintSet.START, R.id.mytitle, ConstraintSet.START, 5);
            kk.connect(R.id.buttonContact, ConstraintSet.TOP, R.id.mytitle, ConstraintSet.TOP, 3);

            kk.connect(R.id.textShow, ConstraintSet.END, R.id.mytitle, ConstraintSet.END);
            kk.connect(R.id.textShow, ConstraintSet.START, R.id.buttonContact, ConstraintSet.END);


            kk.applyTo(this.<ConstraintLayout>findViewById(R.id.mytitle));
            back.setImageResource(R.drawable.back_ar);
            title.setGravity(Gravity.RIGHT);
            time.setGravity(Gravity.RIGHT);
            title.setPadding(0,0,10,0);
            time.setPadding(0,0,10,0);
        }
*/
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
