package com.huison.scrollnotify.ui;

/**
 * Created by huison on 2018/1/9.
 */

public interface IFooter {

    void onPullToLoadMore(float moveDis);

    void onReleaseToLoadMore(float moveDis);

    void onLoadMore();

    void onLoadMoreEnd();
}
