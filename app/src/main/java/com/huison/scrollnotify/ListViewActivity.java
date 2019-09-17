package com.huison.scrollnotify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huison.widget.refresh.PullRefreshLoadMoreLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huisonma on 2018/1/10.
 */

public class ListViewActivity extends AppCompatActivity {

    private List<Integer> list = new ArrayList<>();
    private ListViewAdapter lvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setTitle("ListView");

        list.addAll(MainActivity.items);

        final PullRefreshLoadMoreLayout pullRefreshLayout = findViewById(R.id.layout_pull_refresh_load_more);
        pullRefreshLayout.setDefaultHeader(getResources().getDimensionPixelOffset(R.dimen.refresh_header_height));
        pullRefreshLayout.setDefaultFooter(getResources().getDimensionPixelOffset(R.dimen.refresh_footer_height));
        pullRefreshLayout.setListener(new PullRefreshLoadMoreLayout.OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        list.addAll(MainActivity.items);
                        lvAdapter.notifyDataSetChanged();
                        pullRefreshLayout.endRefreshing();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.addAll(MainActivity.items);
                        lvAdapter.notifyDataSetChanged();
                        pullRefreshLayout.endLoadMore();
                    }
                }, 2000);
            }
        });

        lvAdapter = new ListViewAdapter();
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(lvAdapter);
        lvAdapter.notifyDataSetChanged();
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Integer getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_text, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setData(position);
            return convertView;
        }

        class Holder {
            TextView textView;

            Holder(View itemView) {
                textView = itemView.findViewById(R.id.text_view);
            }

            void setData(int position) {
                textView.setText("Index = " + position);
            }
        }
    }
}
