package com.huison.widget.refresh;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huisonma on 2018/1/9.
 */

final class RecyclerViewTouchHelper implements ITouchHelper {

    private RecyclerView rv;

    private LinearLayoutManager layoutManager;

    RecyclerViewTouchHelper(RecyclerView rv, final OnScrollListener listener) {
        this.rv = rv;
        layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    if (listener != null) {
                        listener.onScrollToBottom();
                    }
                }
            }
        });
    }

    @Override
    public boolean onIntercept(float curInterceptY, float lastInterceptY, boolean isHeaderShow, boolean isFooterShow, boolean allowLoadMore) {
        boolean intercept;

        int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
        View firstView = rv.getChildAt(firstVisiblePos);
        if (firstVisiblePos == 0 && firstView.getTop() == 0) {
            intercept = curInterceptY > lastInterceptY || isHeaderShow;
        } else {
            if (allowLoadMore && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                intercept = curInterceptY < lastInterceptY || isFooterShow;
            } else {
                intercept = false;
            }
        }

        return intercept;
    }

    @Override
    public boolean isContentSlideToTop() {
        int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
        View firstView = layoutManager.getChildAt(firstVisiblePos);
        return firstVisiblePos == 0 && firstView.getTop() == 0;
    }

    @Override
    public boolean isContentSlideToBottom() {
        int lastVisiblePos = layoutManager.findLastVisibleItemPosition();
        return lastVisiblePos == layoutManager.getItemCount() - 1;
    }
}
