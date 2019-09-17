package com.huison.scrollnotify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huison.widget.refresh.PullRefreshLoadMoreLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huisonma on 2018/1/10.
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

        final PullRefreshLoadMoreLayout pullRefreshLayout = findViewById(R.id.layout_pull_refresh_load_more);
        pullRefreshLayout.setDefaultHeader(getResources().getDimensionPixelOffset(R.dimen.refresh_header_height));
        pullRefreshLayout.setDefaultFooter(getResources().getDimensionPixelOffset(R.dimen.refresh_footer_height));
        pullRefreshLayout.setIsAutoLoadMore(false);
        pullRefreshLayout.setListener(new PullRefreshLoadMoreLayout.OnRefreshLoadMoreListener() {
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
                        pullRefreshLayout.endLoadMore();
                        list.addAll(MainActivity.items);
                        rvAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        rvAdapter = new RecyclerViewAdapter();
        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(rvAdapter);
        rvAdapter.notifyDataSetChanged();
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageViewHolder> {

        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_text, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
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
                textView = itemView.findViewById(R.id.text_view);
            }

            void setData(int position) {
                textView.setText("Index = " + position);
            }
        }
    }
}
