package com.ilivedata.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class FAQBody extends Activity {
    CustomerData instan = CustomerData.getInstance();
    Map<String, String> faqMap = instan.FAQFileMap;
    FAQUnit.FAQInfo info = null;
    TextView textno, textyes, texthelpmsg;
    ImageView contactUs;
    FAQUnit.LanagePrompt remind = instan.lanagePrompt;
    String faqId = "";

    String stampToDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_Date = sdf.format(new  Date(time));
        return time_Date;
    }


    JSONObject sendPost(String postUrl) {
        StringBuffer sb = new StringBuffer();
        JSONObject tt = null;
        try {
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);//超时时间
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            String time = instan.getTime();
            conn.setRequestProperty("Authorization", instan.getAuth(postUrl, time, "", "POST"));
            conn.setRequestProperty("X-TimeStamp", time);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.connect();
//        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
//        dos.writeBytes(data);
//        dos.flush();

            //获得响应状态
            int resultCode = conn.getResponseCode();
            if (HttpURLConnection.HTTP_OK == resultCode) {

                String readLine = new String();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                tt = new JSONObject(sb.toString());
            } else {
                String str = instan.inputStreamToString(conn.getErrorStream());
                Log.e("customsdk","post url " + postUrl + " failed:"  + str);
            }
        }
        catch (Exception ex)
        {
            Log.e("customsdk", "sendpost error " + ex.getMessage());
        }
        return tt;
    }

    void clickHelpful(final String flag) {
        if (flag.equals("true")) {
            texthelpmsg.setText(remind.clickYes);
        } else {
            texthelpmsg.setText(remind.clickNo);
            contactUs.setVisibility(View.VISIBLE);
        }

        textno.setVisibility(View.GONE);
        textyes.setVisibility(View.GONE);
        final String postUrl = instan.getPostHelpfullBody(info.id, info.sectionId, flag);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject ll = sendPost(postUrl);
                    if (ll != null) {
                        if (ll.getInt("return_code") == 0) {
                            faqMap.put(faqId, flag);
                            instan.writeObject();
                        }
                    }
                } catch (Exception e) {
                    Log.e("customsdk","clickHelpful error " + e.getMessage());
                }
            }
        }).start();
    }

    void postInitBody(){
        final String postUrl = instan.getHelpfulURL(info.id, info.sectionId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPost(postUrl);
                } catch (Exception e) {
                    System.out.println("-----" + e);
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_body);

        String firstTitle = getIntent().getStringExtra("firstTitle");
        String secondTitle = getIntent().getStringExtra("secondTitle");

        CustomerData instan = CustomerData.getInstance();
        if (instan.faqMap.containsKey(firstTitle))
            if (instan.faqMap.get(firstTitle).containsKey(secondTitle))
                info = instan.faqMap.get(firstTitle).get(secondTitle);


        faqId = info.id;
        WebView webView = findViewById(R.id.bodyShow);
        postInitBody();

        String viewData = info.body.replace("<img","<img style=\"max-width:100%;height:auto\"");//图片适应宽度
        webView.getSettings().setTextZoom(100);
        webView.loadData(viewData, "text/html; charset=UTF-8", null);
//        webView.loadData(viewData, "text/html", "utf-8");
        contactUs = findViewById(R.id.contactus);
        textno = findViewById(R.id.textno);
        textyes = findViewById(R.id.textyes);
        texthelpmsg = findViewById(R.id.textshowmsg);
        TextView time = findViewById(R.id.modifyTime);
        TextView title = findViewById(R.id.title);

        time.setText("\t\t" + stampToDate(info.modifyTime));
        title.setText(secondTitle);

//        contactUs.setText(remind.ContactUs);

        textno.setText(remind.helpfulNo);
        textyes.setText(remind.helpfulYes);
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
            textno.setVisibility(View.GONE);
            textyes.setVisibility(View.GONE);
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
                Intent intent = new Intent(FAQBody.this, RobotActivity.class);
                startActivity(intent);
            }
        });
    }
}
