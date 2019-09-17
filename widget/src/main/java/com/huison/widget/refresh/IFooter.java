package com.huison.widget.refresh;

/**
 * Created by huisonma on 2018/1/9.
 */

public interface IFooter {

    void onPullToLoadMore(float moveDis);

    void onReleaseToLoadMore(float moveDis);

    void onLoadMore();

    void onLoadMoreEnd();
}
