package com.huison.scrollnotify.ui;

import android.widget.ScrollView;

/**
 * Created by huison on 2018/1/9.
 */

public class ScrollViewTouchHelper implements TouchHelperBase {

    private ScrollView sv;

    public ScrollViewTouchHelper(ScrollView sv) {
        this.sv = sv;
    }

    @Override
    public boolean judgeIntercept(float curInterceptY, float lastInterceptY, boolean isHeaderShow, boolean isFooterShow, boolean allowLoadMore) {
        boolean intercept;

        if (sv.getScrollY() == 0) {
            intercept = curInterceptY > lastInterceptY  || isHeaderShow;
        } else {
            if (allowLoadMore && sv.getChildAt(0).getMeasuredHeight() <= sv.getScrollY() + sv.getHeight()) {
                intercept = curInterceptY < lastInterceptY  || isFooterShow;
            } else {
                intercept = false;
            }
        }

        return intercept;
    }

    @Override
    public boolean isContentSlideToTop() {
        return sv.getScrollY() == 0;
    }

    @Override
    public boolean isContentSlideToBottom() {
        if (sv.getChildCount() > 0) {
            int childHeight = sv.getChildAt(0).getMeasuredHeight();
            int svScrollY = sv.getScrollY() + sv.getHeight();
            return childHeight >= svScrollY;
        } else {
            return false;
        }
    }
}
