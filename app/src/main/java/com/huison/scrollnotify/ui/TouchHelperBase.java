package com.huison.scrollnotify.ui;

/**
 * Created by huison on 2018/1/9.
 */

public interface TouchHelperBase {

    interface OnScrollListener {
        void onScrollToBottom();
    }

    boolean judgeIntercept(float curInterceptY, float lastInterceptY, boolean isHeaderShow, boolean isFooterShow, boolean allowLoadMore);

    boolean isContentSlideToTop();

    boolean isContentSlideToBottom();
}
