package com.customer.test;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class CustomLang {
    LinkedList<CItem> langlist = new LinkedList<CItem>();


//    TreeSet langset = new TreeSet<CItem>(new Comparator<CItem>() {
//
//        @Override
//        public int compare(CItem o1, CItem o2) {
//            return (o1.ID).compareTo(o2.ID);
//        }
//    });

    LinkedList<CItem> getlangcode(){
        return langlist;
    }

    CustomLang(){
        langlist.add(new CItem ("Arabic","ar"));
        langlist.add(new CItem ("Azerbaijani","az"));
        langlist.add(new CItem ("Bulgarian","bg"));
        langlist.add(new CItem ("Bengali","bn"));
        langlist.add(new CItem ("Catalan","ca"));
        langlist.add(new CItem ("Czech","cs"));
        langlist.add(new CItem ("Danish","da"));
        langlist.add(new CItem ("Dutch","nl"));
        langlist.add(new CItem ("English","en"));
        langlist.add(new CItem ("Estonian","et"));
        langlist.add(new CItem ("Finnish","fi"));
        langlist.add(new CItem ("French","fr"));
        langlist.add(new CItem ("German","de"));
        langlist.add(new CItem ("Greek","el"));
        langlist.add(new CItem ("Haitian_Creole","ht"));
        langlist.add(new CItem ("Hebrew","he"));
        langlist.add(new CItem ("Hindi","hi"));
        langlist.add(new CItem ("Hungarian","hu"));
        langlist.add(new CItem ("Indonesian","id"));
        langlist.add(new CItem ("Icelandic","is"));
        langlist.add(new CItem ("Italian","it"));
        langlist.add(new CItem ("Japanese","ja"));
        langlist.add(new CItem ("Korean","ko"));
        langlist.add(new CItem ("Kazakh","kk"));
        langlist.add(new CItem ("Central_Khmer","km"));
        langlist.add(new CItem ("Latvian","lv"));
        langlist.add(new CItem ("Lithuanian","lt"));
        langlist.add(new CItem ("Malay","ms"));
        langlist.add(new CItem ("Burmese","my"));
        langlist.add(new CItem ("Norwegian","no"));
        langlist.add(new CItem ("Persian","fa"));
        langlist.add(new CItem ("Polish","pl"));
        langlist.add(new CItem ("Portuguese","pt"));
        langlist.add(new CItem ("Romanian","ro"));
        langlist.add(new CItem ("Russian","ru"));
        langlist.add(new CItem ("Slovak","sk"));
        langlist.add(new CItem ("Slovenian","sl"));
        langlist.add(new CItem ("Spanish","es"));
        langlist.add(new CItem ("Swedish","sv"));
        langlist.add(new CItem ("Serbian","sr"));
        langlist.add(new CItem ("Tamil","ta"));
        langlist.add(new CItem ("Thai","th"));
        langlist.add(new CItem ("Turkish","tr"));
        langlist.add(new CItem ("Ukrainian","uk"));
        langlist.add(new CItem ("Urdu","ur"));
        langlist.add(new CItem ("Uzbek","uz"));
        langlist.add(new CItem ("Vietnamese","vi"));
        langlist.add(new CItem ("Filipino","tl"));
        langlist.add(new CItem ("RussianLatin","ru"));
        langlist.add(new CItem ("Simplified Chinese","zh-CN"));

        Collections.sort(langlist, new Comparator<CItem>() {
            @Override
            public int compare(CItem o1, CItem o2) {
                return (o1.ID).compareTo(o2.ID);
            }
        });
    }

    static class CItem {
        public String ID = "";
        public String Value = "";

        public boolean equals(Object obj) {
            if (obj instanceof  CItem) {
                if (this.ID.equals(((CItem) obj).ID) && this.Value.equals(((CItem) obj).Value)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            return false;
        }

        public CItem(String _ID, String _Value) {
            ID = _ID;
            Value = _Value;
        }

        @Override
        public String toString() {           //为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
            // TODO Auto-generated method stub
            return ID;
        }

        public String getID() {
            return ID;
        }

        public String getValue() {
            return Value;
        }
    }


}
