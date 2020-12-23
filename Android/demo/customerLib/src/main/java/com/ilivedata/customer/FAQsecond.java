package com.ilivedata.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class FAQsecond extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_second);
        final CustomerData  instan = CustomerData.getInstance();
        final String title = getIntent().getStringExtra("title");

        ListView titile = findViewById(R.id.title);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        List<String> list=new ArrayList<>();
        //新增加adapter适配器
        ArrayAdapter adapter=new ArrayAdapter<String>(this,R.layout.myitem,list);

        if (instan.faqMap.containsKey(title)) {
            for (String key : instan.faqMap.get(title).keySet())
                adapter.add(key);
            titile.setAdapter(adapter);
        }

        titile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String item = (String) parent.getAdapter().getItem(position);
                Intent intent = new Intent(FAQsecond.this, FAQBody.class);
                intent.putExtra("firstTitle",title);
                intent.putExtra("secondTitle",item);
                startActivity(intent);
            }
        });
    }
}
