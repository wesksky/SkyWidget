package com.sky.skywidget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sky.skywidget.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    ListView listView;
    List<Widget> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        list = new ArrayList<>();
        addItem("计时器", TimerCountActivity.class);

        listView = $(R.id.listview);
        listView.setAdapter(new ListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MainActivity.this, list.get(i).ShowActivity));
            }
        });
    }

    private void addItem(String name, Class clazz) {
        Widget widget = new Widget();
        widget.name = name;
        widget.ShowActivity = clazz;
        if (list != null) {
            list.add(widget);
        }
    }

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View view = View.inflate(MainActivity.this, R.layout.item_main_list, null);
            TextView textView = (TextView) view.findViewById(R.id.item_main_list_content);
            textView.setText(list.get(i).name);
            return view;
        }
    }

    class Widget {
        String name;
        Class ShowActivity;
    }

}
