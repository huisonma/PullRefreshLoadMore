package com.huison.scrollnotify.ui;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by huison on 2018/1/9.
 */

public class ListViewTouchHelper implements TouchHelperBase, AbsListView.OnScrollListener {

    private ListView lv;
    private OnScrollListener listener;

    public ListViewTouchHelper(ListView lv, OnScrollListener listener) {
        this.listener = listener;
        this.lv = lv;
        this.lv.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    private int preFirstChildTop;
    private int preFirstVisibleItem;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount != 0) {
            boolean isSlideDown;
            boolean isLastVisiblePos = lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1;
            boolean isSlideToBottom = lv.getChildAt(lv.getChildCount() - 1).getBottom() <= lv.getBottom();
            if (firstVisibleItem == preFirstVisibleItem) {
                int newFirstChildTop = getFirstChildTop();
                isSlideDown = newFirstChildTop - preFirstChildTop < 0;
                if (isSlideDown && isLastVisiblePos && isSlideToBottom) {
                    if (listener != null) {
                        listener.onScrollToBottom();
                    }
                }
                preFirstChildTop = newFirstChildTop;
            } else {
                isSlideDown = firstVisibleItem - preFirstVisibleItem > 0;
                if (isSlideDown && isLastVisiblePos && isSlideToBottom) {
                    if (listener != null) {
                        listener.onScrollToBottom();
                    }
                }
                preFirstChildTop = getFirstChildTop();
                preFirstVisibleItem = firstVisibleItem;
            }
        }
    }

    private int getFirstChildTop() {
        View firstChild = lv.getChildAt(0);
        if (firstChild != null) {
            return firstChild.getTop();
        } else {
            return 0;
        }
    }

    @Override
    public boolean judgeIntercept(float curInterceptY, float lastInterceptY, boolean isHeaderShow, boolean isFooterShow, boolean allowLoadMore) {
        boolean intercept;

        int firstVisiblePos = lv.getFirstVisiblePosition();
        View firstChild = lv.getChildAt(0);
        if (firstVisiblePos == 0 && firstChild != null && firstChild.getTop() == 0) {
            intercept = curInterceptY > lastInterceptY || isHeaderShow;
        } else {
            int childCount = lv.getChildCount();
            if (allowLoadMore && childCount > 0) {
                boolean isLastVisiblePos = lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1;
                boolean isSlideToBottom = lv.getChildAt(childCount - 1).getBottom() == lv.getBottom();
                if (isLastVisiblePos && isSlideToBottom) {
                    intercept = curInterceptY < lastInterceptY || isFooterShow;
                } else {
                    intercept = false;
                }
            } else {
                intercept = false;
            }
        }

        return intercept;
    }

    @Override
    public boolean isContentSlideToTop() {
        int firstVisiblePos = lv.getFirstVisiblePosition();
        View firstChild = lv.getChildAt(0);
        return firstVisiblePos == 0 && firstChild.getTop() == 0;
    }

    @Override
    public boolean isContentSlideToBottom() {
        int childCount = lv.getChildCount();
        if (childCount > 0) {
            boolean isLastVisiblePos = lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1;
            int lastChildBottom = lv.getChildAt(childCount - 1).getBottom();
            boolean isSlideToBottom = lastChildBottom >= lv.getBottom();
            return isLastVisiblePos && isSlideToBottom;
        } else {
            return false;
        }
    }
}
