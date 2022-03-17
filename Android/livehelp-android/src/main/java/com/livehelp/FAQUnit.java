package com.livehelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQUnit extends Activity {
    Context context = this;
    CustomerData instan = CustomerData.INSTANCE;
    JSONObject respondsDta = null;
    SearchView searchView;
    ExpandableListView faqList;
    LanagePrompt tmplanagePrompt = new LanagePrompt();
    MyAdapter adapter;
    MyAdapter searchAdapter;
    private ImageView imageButton;
    List<SpannableString> listData;

    //group数据
    private ArrayList<SpannableString> mGroupList = new ArrayList<>();
    private ArrayList<SpannableString> searchGroupList = new ArrayList<>();
    private HashMap<String,String> searchMap  = new HashMap<>();
    //item数据
    private ArrayList<ArrayList<SpannableString>> mItemSet = new ArrayList<>();

//    class FAQInfo {
//        String id;
//        String sectionId;
//        String body;
//        long modifyTime;
//
//        FAQInfo(String _id, String _sectionId, String _body, long _modifyTime) {
//            id = _id;
//            sectionId = _sectionId;
//            body = _body;
//            modifyTime = _modifyTime;
//        }
//    }

    static class FAQInfo {
        String firstTitle;
        ArrayList<FAQINNER> realInfo = new ArrayList();
         public static class FAQINNER {
            String secondTitle;
            String id;
            String sectionId;
            String body;
            long modifyTime;
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
                int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
                TextView textView = (TextView) searchView.findViewById(id);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp

                searchView.setQueryHint(tmplanagePrompt.searchtPrompt);

//                if (instan.faqMap != null) {
//                    for (String title : instan.faqMap.keySet()) {
//                        SpannableString tt = new SpannableString(title);
//                        mGroupList.add(tt);
//                        ArrayList<SpannableString> tmp = new ArrayList<SpannableString>();
//                        for (String secondtitle : instan.faqMap.get(title).keySet()) {
//                            SpannableString sectitle = new SpannableString(secondtitle);
//                            tmp.add(sectitle);
//                        }
//                        mItemSet.add(tmp);
//                    }
//                }
                if (instan.showfaqList != null) {
                    for (FAQInfo kkk  : instan.showfaqList) {
                        SpannableString tt = new SpannableString(kkk.firstTitle);
                        mGroupList.add(tt);
                        ArrayList<SpannableString> tmp = new ArrayList<SpannableString>();
                        for(FAQInfo.FAQINNER faqinner: kkk.realInfo){
                            SpannableString sectitle = new SpannableString(faqinner.secondTitle);
                            tmp.add(sectitle);
                        }
                        mItemSet.add(tmp);
                    }
                }
                adapter = new MyAdapter(getApplicationContext(), mGroupList, mItemSet);
//                adapter.notifyDataSetChanged();
                faqList.setAdapter(adapter);
            }
        });
    }

    void initFAQData() {
        String faqURL = instan.getFAQURL();

        instan.httpRequest(faqURL, false, "",new CustomerData.RequestCallback() {
            @Override
            public void onResult(int code, String errMsg, JSONObject ret) {
                if (errMsg.isEmpty()){
                    JSONObject data = ret.optJSONObject("data");
                    if (data != null) {
                        JSONObject messages = data.optJSONObject("messages");
                        if (messages != null) {
                            tmplanagePrompt.clickNo = messages.optString("sdk.api.faq.helpful.click.no");
                            tmplanagePrompt.clickYes = messages.optString("sdk.api.faq.helpful.click.yes");
                            tmplanagePrompt.helpfulNo = messages.optString("sdk.api.faq.helpful.option.no");
                            tmplanagePrompt.helpfulYes = messages.optString("sdk.api.faq.helpful.option.yes");
                            tmplanagePrompt.ContactUs = messages.optString("sdk.api.faq.button.contact.us");
                            tmplanagePrompt.searchtPrompt = messages.optString("sdk.api.faq.search.placeholder");
                            tmplanagePrompt.helpfulQuestion = messages.optString("sdk.api.faq.helpful.question");
                        }
                    }

                    instan.lanagePrompt = tmplanagePrompt;
                    JSONArray sections = data.optJSONArray("sections");
                    if (sections != null) {
//                        for (int i = 0; i < sections.length(); i++) {
//                            JSONObject obj = sections.optJSONObject(i);
//                            if (obj != null) {
//                                String firstTitle = obj.optString("title");
//                                JSONArray secondObj = obj.optJSONArray("faqs");
//                                if (secondObj != null) {
//                                    Map<String, FAQInfo> tmpMap = new HashMap<String, FAQInfo>();
//                                    for (int j = 0; j < secondObj.length(); j++) {
//                                        JSONObject realFAQ = secondObj.optJSONObject(j);
//                                        if (realFAQ != null) {
//                                            String secondTitle = realFAQ.optString("title");
//                                            FAQInfo faqInfo = new FAQInfo(realFAQ.optString("id"), realFAQ.optString("sectionId"), realFAQ.optString("body"), realFAQ.optLong("modifiedDate"));
//                                            tmpMap.put(secondTitle, faqInfo);
//                                        }
//                                    }
//                                    if (instan.faqMap != null)
//                                        instan.faqMap.put(firstTitle, tmpMap);
//                                }
//                            }
//                        }

                        for (int i = 0; i < sections.length(); i++) {
                            FAQInfo faqInfo = new FAQInfo();
                            JSONObject obj = sections.optJSONObject(i);
                            if (obj != null) {
                                faqInfo.firstTitle = obj.optString("title");
                                JSONArray secondObj = obj.optJSONArray("faqs");
                                if (secondObj != null) {
                                    for (int j = 0; j < secondObj.length(); j++) {
                                        JSONObject realFAQ = secondObj.optJSONObject(j);
                                        if (realFAQ != null) {
                                            FAQInfo.FAQINNER ttt = new FAQInfo.FAQINNER();
                                            ttt.secondTitle = realFAQ.optString("title");
                                            ttt.id = realFAQ.optString("id");
                                            ttt.sectionId = realFAQ.optString("sectionId");
                                            ttt.modifyTime = realFAQ.optLong("modifiedDate");
                                            ttt.body = realFAQ.optString("body");
                                            faqInfo.realInfo.add(ttt);
                                        }
                                    }
                                }
                                if (instan.showfaqList != null)
                                    instan.showfaqList.add(faqInfo);
                            }
                        }
                    }
                    showResponse();
                }
                else {
                    instan.errorRecord.recordError("initFAQData return HTTP code Exception:" + errMsg);
                    instan.alertDialog(FAQUnit.this, "initFAQData ERROR :\n" + errMsg);
                }
            }
        });
    }

    protected void onDestroy() {
        instan.writeObject();
//        instan.faqMap.clear();
//        instan = null;
        super.onDestroy();
    }

    void textFilter(String text) {
        try {
            if (text.isEmpty()) {
                adapter.isSearchFlag = false;
                adapter.mGroup = mGroupList;
                adapter.mItemList = mItemSet;
                adapter.notifyDataSetChanged();
//            faqList.setAdapter(adapter);
            } else {
                searchGroupList.clear();
                searchMap.clear();
       /*         if (instan.faqMap != null) {
                    for (String first : instan.faqMap.keySet()) {
                        for (String title : instan.faqMap.get(first).keySet()) {
                            int index = title.toLowerCase().indexOf(text);
                            if (index != -1) {
                                SpannableString ss = new SpannableString(title);
                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#49ADFF"));
                                ss.setSpan(foregroundColorSpan, index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                searchGroupList.add(ss);
                                searchMap.put(ss.toString(), first);
                                continue;
                            } else {
                                String body = instan.faqMap.get(first).get(title).body;
                                int index1 = body.indexOf(text);
                                if (index1 != -1) {
                                    SpannableString ss = new SpannableString(title);
                                    searchGroupList.add(ss);
                                    searchMap.put(ss.toString(), first);

                                }
                            }
                        }
                    }
                }*/

                if (instan.showfaqList != null) {
                    for (FAQInfo faqinfos : instan.showfaqList) {
                        for (FAQInfo.FAQINNER tt: faqinfos.realInfo) {
                            int index = tt.secondTitle.toLowerCase().indexOf(text);
                            if (index != -1) {
                                SpannableString ss = new SpannableString(tt.secondTitle);
                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#49ADFF"));
                                ss.setSpan(foregroundColorSpan, index, index + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                searchGroupList.add(ss);
                                searchMap.put(ss.toString(), faqinfos.firstTitle);
                                continue;
                            } else {
                                String body = tt.body;
                                int index1 = body.indexOf(text);
                                if (index1 != -1) {
                                    SpannableString ss = new SpannableString(tt.secondTitle);
                                    searchGroupList.add(ss);
                                    searchMap.put(ss.toString(), faqinfos.firstTitle);

                                }
                            }
                        }
                    }
                }

                if (!searchGroupList.isEmpty()) {
                    adapter.isSearchFlag = true;
                    adapter.mGroup = searchGroupList;
                    adapter.mItemList = new ArrayList<>();
                    adapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception ex){
            instan.errorRecord.recordError("textFilter error");
        }
//        faqList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
//        if (!faqList.isStackFromBottom())
//            faqList.setStackFromBottom(true);
//        else
//            faqList.setStackFromBottom(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (instan.m_Lang.equals(instan.specailLan)){
            setContentView(R.layout.activity_faq_ar);
        }
        else
            setContentView(R.layout.activity_faq);

        ConstraintLayout layout = findViewById(R.id.mytitle);
        int color = Color.parseColor(instan.backgroundcolor);
        layout.setBackgroundColor(color);

        Window window = getWindow();
        if (window == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = window.getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        imageButton = findViewById(R.id.back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listData = new ArrayList<>();
        //新增加adapter适配器
//        adapter = new ArrayAdapter<SpannableString>(this, android.R.layout.simple_list_item_1, listData);

        searchView = findViewById(R.id.mysearch);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出软键盘
                searchView.setIconified(false);
            }
        });

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

//        faqList.setChildDivider(new ColorDrawable(getResources().getColor(android.R.color.holo_green_dark)));

        faqList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideKeyboard();
            }
        });

        faqList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (adapter.isSearchFlag){
                    Intent intent = new Intent(FAQUnit.this, FAQBody.class);
                    String sectitle = searchGroupList.get(groupPosition).toString();
                    if (searchMap.containsKey(sectitle)){
                        String firsttitle = searchMap.get(sectitle);
                        intent.putExtra("firstTitle",firsttitle);
                        intent.putExtra("secondTitle",sectitle);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
        //设置子项布局监听
        faqList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(FAQUnit.this, FAQBody.class);
                String firsthehe = mGroupList.get(groupPosition).toString();
                String secondhehe = mItemSet.get(groupPosition).get(childPosition).toString();
                intent.putExtra("firstTitle",firsthehe);
                intent.putExtra("secondTitle",secondhehe);
                startActivity(intent);
                return true;

            }
        });

    //控制他只能打开一个组
        faqList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for(int i = 0;i < adapter.getGroupCount();i++){
                    if (i!=groupPosition){
                        faqList.collapseGroup(i);
                    }
                }
            }
        });


//        faqList.setSelector(R.drawable.bg_searchview);

//        if (instan.faqMap == null)
//            instan.faqMap = new HashMap<>();
//        if (instan.faqMap.isEmpty())
        if (instan.showfaqList == null)
            instan.showfaqList = new ArrayList<>();
        if (instan.showfaqList.isEmpty())
            initFAQData();
        else
            showResponse();
    }
}
