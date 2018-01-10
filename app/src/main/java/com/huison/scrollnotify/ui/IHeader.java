package com.huison.scrollnotify.ui;

/**
 * Created by huison on 2018/1/9.
 */

public interface IHeader {

    void onPullToRefresh(float moveDis);

    void onReleaseToRefresh(float moveDis);

    void onRefreshing();

    void onRefreshEnd();
}
