package com.huison.scrollnotify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.huison.scrollnotify.ui.LoadTextFooter;
import com.huison.scrollnotify.ui.LoadTextHeader;
import com.huison.scrollnotify.ui.OnRefreshLoadMoreListener;
import com.huison.scrollnotify.ui.PullRefreshLoadMoreLayout;

/**
 * Created by huison on 2018/1/10.
 */

public class ScrollViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);
        setTitle("ScrollView");

        LoadTextHeader loadTextHeader = new LoadTextHeader(this);
        loadTextHeader.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200));
        LoadTextFooter loadTextFooter = new LoadTextFooter(this);
        loadTextFooter.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200));
        final PullRefreshLoadMoreLayout pullRefreshLayout = (PullRefreshLoadMoreLayout) findViewById(R.id.layout_pull_refresh_load_more);
        pullRefreshLayout.setHeader(loadTextHeader);
        pullRefreshLayout.setFooter(loadTextFooter);
        pullRefreshLayout.setAllowLoadMore(false);
        pullRefreshLayout.setListener(new OnRefreshLoadMoreListener() {
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
