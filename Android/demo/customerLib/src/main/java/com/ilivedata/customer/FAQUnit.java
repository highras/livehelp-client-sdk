package com.ilivedata.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FAQUnit extends Activity {
    Context context = this;
    CustomerData instan;
    JSONObject respondsDta = null;
    SearchView searchView;
    ListView faqList;
    LanagePrompt tmplanagePrompt = new LanagePrompt();
    ArrayAdapter adapter;
    List<SpannableString> listData;

    class FAQInfo {
        String id;
        String sectionId;
        String body;
        long modifyTime;

        FAQInfo(String _id, String _sectionId, String _body, long _modifyTime) {
            id = _id;
            sectionId = _sectionId;
            body = _body;
            modifyTime = _modifyTime;
        }
    }

    class LanagePrompt {
        String helpfulYes = "YES";
        String helpfulNo = "NO";
        String clickYes = "You found this helpful";
        String clickNo = "You didn't find this helpful";
        String ContactUs = "Contact Us";
        String searchtPrompt = "Your question";
        String helpfulQuestion = "Was this helpful?";
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // 这将让键盘在所有的情况下都被隐藏
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
        }
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchView.setQueryHint(tmplanagePrompt.searchtPrompt);
                for (String title : instan.faqMap.keySet()) {
                    SpannableString tt = new SpannableString(title);
                    adapter.add(tt);
                }
//                adapter.notifyDataSetChanged();
                faqList.setAdapter(adapter);
            }
        });
    }

    void initFAQData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String time = instan.getTime();
                    String realUrl = instan.getFAQURL();
                    URL urlObject = new URL(realUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                    connection.setDoOutput(false);
                    // 设置是否从httpUrlConnection读入
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(20 * 1000);
                    connection.setReadTimeout(20 * 1000);
                    connection.setRequestProperty("Authorization", instan.getAuth(realUrl, time, "","GET"));
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
                        respondsDta = new JSONObject(builder.toString());
                        JSONObject data = respondsDta.optJSONObject("data");
                        if (data != null) {
                            JSONObject messages = data.optJSONObject("messages");
                            if (messages != null) {
                                tmplanagePrompt.clickNo = messages.optString("sdk.api.faq.helpful.click.no");
                                tmplanagePrompt.clickYes = messages.getString("sdk.api.faq.helpful.click.yes");
                                tmplanagePrompt.helpfulNo = messages.getString("sdk.api.faq.helpful.option.no");
                                tmplanagePrompt.helpfulYes = messages.getString("sdk.api.faq.helpful.option.yes");
                                tmplanagePrompt.ContactUs = messages.getString("sdk.api.faq.button.contact.us");
                                tmplanagePrompt.searchtPrompt = messages.getString("sdk.api.faq.search.placeholder");
                                tmplanagePrompt.helpfulQuestion = messages.getString("sdk.api.faq.helpful.question");
                            }
                        }

                        instan.lanagePrompt = tmplanagePrompt;

                        JSONArray sections = data.optJSONArray("sections");
                        if (sections != null) {
                            for (int i = 0; i < sections.length(); i++) {
                                JSONObject obj = sections.optJSONObject(i);
                                if (obj != null) {
                                    String firstTitle = obj.optString("title");
                                    JSONArray secondObj = obj.optJSONArray("faqs");
                                    if (secondObj != null) {
                                        Map<String, FAQInfo> tmpMap = new HashMap<String, FAQInfo>();
                                        for (int j = 0; j < secondObj.length(); j++) {
                                            JSONObject realFAQ = secondObj.optJSONObject(j);
                                            if (realFAQ != null) {
                                                String secondTitle = realFAQ.optString("title");
                                                FAQInfo faqInfo = new FAQInfo(realFAQ.optString("id"), realFAQ.optString("sectionId"), realFAQ.optString("body"), realFAQ.optLong("modifiedDate"));
                                                tmpMap.put(secondTitle, faqInfo);
                                            }
                                        }
                                        instan.faqMap.put(firstTitle, tmpMap);
                                    }
                                }
                            }
                        }
                        showResponse();
                    } else {
                        String str = instan.inputStreamToString(connection.getErrorStream());
                        Log.e("customsdk","initFAQData return HTTP code Exception:" + str);
                        instan.alertDialog(FAQUnit.this, "initFAQData ERROR :\n" + str);
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.e("customsdk","faq return code Exception:" + e.getMessage());
                }
            }
        }).start();
    }

    protected void onDestroy() {
        instan.writeObject();
        instan.faqMap = null;
//        instan = null;
        super.onDestroy();
    }

    void textFilter(String text) {
        List<SpannableString> firstList = new ArrayList<>();
        List<SpannableString> secondList = new ArrayList<>();
        listData.clear();
        if (text.isEmpty()) {
            for (String title : instan.faqMap.keySet()) {
                SpannableString tt = new SpannableString(title);
                adapter.add(tt);
            }
        } else {
            for (String first : instan.faqMap.keySet()) {
                for (String title : instan.faqMap.get(first).keySet()) {
                    int index = title.toLowerCase().indexOf(text);
                    if (index != -1) {
                        SpannableString ss = new SpannableString(title);
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF0000"));
                        ss.setSpan(foregroundColorSpan, index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        firstList.add(ss);
                        continue;
                    } else {
                        String body = instan.faqMap.get(first).get(title).body;
                        int index1 = body.indexOf(text);
                        if (index1 != -1) {
                            SpannableString ss = new SpannableString(title);
                            secondList.add(ss);
                        }
                    }
                }
            }
            firstList.addAll(secondList);
        }
        listData.addAll(firstList);
        adapter.notifyDataSetChanged();
        faqList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
//        if (!faqList.isStackFromBottom())
//            faqList.setStackFromBottom(true);
//        else
//            faqList.setStackFromBottom(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        instan = CustomerData.getInstance();

        listData = new ArrayList<>();
        //新增加adapter适配器
        adapter = new ArrayAdapter<SpannableString>(this, android.R.layout.simple_list_item_1, listData);

        searchView = findViewById(R.id.mysearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                hideKeyboard();
                textFilter(newText.trim());

                return true;
            }

            // 搜索框文本改变事件
            @Override
            public boolean onQueryTextChange(String newText) {
                textFilter(newText.trim());
                return true;
            }
        });
        faqList = findViewById(R.id.faqList);
        faqList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideKeyboard();
            }
        });
//        faqList.setSelector(R.drawable.bg_searchview);
        faqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                SpannableString item = (SpannableString) parent.getAdapter().getItem(position);
                Intent intent = null;
                if (instan.faqMap.containsKey(item.toString())) {
                    intent = new Intent(context, FAQsecond.class);
                    intent.putExtra("title", item.toString());
                    startActivity(intent);
                }
                else {
                    for (String first : instan.faqMap.keySet()) {
                        for (String title : instan.faqMap.get(first).keySet()) {
                            if (title == item.toString()) {
                                intent = new Intent(context, FAQBody.class);
                                intent.putExtra("firstTitle", first);
                                intent.putExtra("secondTitle", title);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        });


        if (instan.faqMap == null)
            instan.faqMap = new HashMap<>();
        if (instan.faqMap.isEmpty())
            initFAQData();
        else
            showResponse();
    }

    public static void show(Activity activity) {
        CustomerData tmpData = CustomerData.getInstance();
        Context appcontext = tmpData.getContext();
        if (appcontext == null){
            Log.e("customsdk","activity not set");
            return;
        }
        try {
            File dir = appcontext.getFilesDir();
            if (tmpData.FAQfile == null)
                tmpData.FAQfile = new File(dir.getAbsolutePath() + "/FAQfile");
            if (!tmpData.FAQfile.exists())
                tmpData.FAQfile.createNewFile();
            tmpData.readObject();

            Intent intent = new Intent(appcontext, FAQUnit.class);
            activity.startActivity(intent);
            }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
