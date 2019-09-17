package com.huison.widget.refresh;

/**
 * Created by huisonma on 2018/1/9.
 */

public interface ITouchHelper {

    interface OnScrollListener {
        void onScrollToBottom();
    }

    boolean onIntercept(float curInterceptY, float lastInterceptY, boolean isHeaderShow, boolean isFooterShow, boolean allowLoadMore);

    boolean isContentSlideToTop();

    boolean isContentSlideToBottom();
}
