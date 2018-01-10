package com.huison.scrollnotify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huison.scrollnotify.ui.LoadTextFooter;
import com.huison.scrollnotify.ui.LoadTextHeader;
import com.huison.scrollnotify.ui.OnRefreshLoadMoreListener;
import com.huison.scrollnotify.ui.PullRefreshLoadMoreLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huison on 2018/1/10.
 */

public class RecyclerViewActivity extends AppCompatActivity {

    private List<Integer> list = new ArrayList<>();
    private RecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        setTitle("RecyclerView");

        list.addAll(MainActivity.items);

        LoadTextHeader loadTextHeader = new LoadTextHeader(this);
        loadTextHeader.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200));
        LoadTextFooter loadTextFooter = new LoadTextFooter(this);
        loadTextFooter.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200));
        final PullRefreshLoadMoreLayout pullRefreshLayout = (PullRefreshLoadMoreLayout) findViewById(R.id.layout_pull_refresh_load_more);
        pullRefreshLayout.setHeader(loadTextHeader);
        pullRefreshLayout.setFooter(loadTextFooter);
        pullRefreshLayout.setIsAutoLoadMore(false);
        pullRefreshLayout.setListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        list.addAll(MainActivity.items);
                        rvAdapter.notifyDataSetChanged();
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
                        rvAdapter.notifyDataSetChanged();
                        pullRefreshLayout.endLoadMore();
                    }
                }, 2000);
            }
        });

        rvAdapter = new RecyclerViewAdapter();
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(rvAdapter);
        rvAdapter.notifyDataSetChanged();
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageViewHolder> {

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_text, parent, false));
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            ImageViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text_view);
            }

            void setData(int position) {
                textView.setText("Index = " + position);
            }
        }
    }
}
