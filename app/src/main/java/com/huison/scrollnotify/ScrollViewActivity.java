package com.huison.scrollnotify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.huison.widget.refresh.PullRefreshLoadMoreLayout;

/**
 * Created by huisonma on 2018/1/10.
 */

public class ScrollViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);
        setTitle("ScrollView");

        final PullRefreshLoadMoreLayout pullRefreshLayout = findViewById(R.id.layout_pull_refresh_load_more);
        pullRefreshLayout.setDefaultHeader(getResources().getDimensionPixelOffset(R.dimen.refresh_header_height));
        pullRefreshLayout.setDefaultFooter(getResources().getDimensionPixelOffset(R.dimen.refresh_footer_height));
        pullRefreshLayout.setAllowLoadMore(false);
        pullRefreshLayout.setListener(new PullRefreshLoadMoreLayout.OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                }, 2000);
            }
        });
    }
}
