package com.huison.widget.refresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.huison.widget.R;

/**
 * Created by huisonma on 2018/1/9.
 */

public class PullRefreshLoadMoreLayout extends FrameLayout implements ITouchHelper.OnScrollListener {

    private static final float MOVE_FACTOR = 0.3f;
    private static final int SCROLL_ANIMATOR_DURATION = 100;

    private FrameLayout headerContainer;
    private LayoutParams headerParams;
    private FrameLayout footerContainer;
    private LayoutParams footerParams;
    private IHeader header;
    private IFooter footer;

    private int totalHeight;
    private int headerHeight;
    private int footerHeight;
    private int touchSlop;

    private View childView;

    private boolean isRefreshing;
    private boolean isLoadMore;
    private boolean isAutoLoadMore;
    private boolean allowLoadMore;

    private boolean isFirstLayout = true;

    private ITouchHelper touchHelper;
    private OnRefreshLoadMoreListener listener;

    private boolean intercept;
    private float lastInterceptY;

    public PullRefreshLoadMoreLayout(@NonNull Context context) {
        this(context, null);
    }

    public PullRefreshLoadMoreLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLoadMoreLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshLoadMoreLayout, defStyleAttr, 0);
        isAutoLoadMore = typedArray.getBoolean(R.styleable.PullRefreshLoadMoreLayout_isAutoLoadMore, true);
        allowLoadMore = typedArray.getBoolean(R.styleable.PullRefreshLoadMoreLayout_allowLoadMore, true);
        typedArray.recycle();

        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        headerContainer = new FrameLayout(context);
        headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerContainer.setLayoutParams(headerParams);
        addView(headerContainer, headerParams);

        footerContainer = new FrameLayout(context);
        footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        footerContainer.setLayoutParams(footerParams);
        addView(footerContainer, footerParams);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && isFirstLayout) {
            initLayout();

            isFirstLayout = false;
        }
    }

    private void initLayout() {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            if (child instanceof RecyclerView) {
                childView = child;
                touchHelper = new RecyclerViewTouchHelper((RecyclerView) childView, this);
                break;
            } else if (child instanceof ListView) {
                childView = child;
                touchHelper = new ListViewTouchHelper((ListView) childView, this);
                break;
            } else if (child instanceof ScrollView) {
                childView = child;
                touchHelper = new ScrollViewTouchHelper((ScrollView) childView);
                break;
            }
        }

        totalHeight = getHeight();
        headerHeight = headerContainer.getHeight();
        headerParams.topMargin = -headerHeight;
        headerContainer.setLayoutParams(headerParams);
        footerHeight = footerContainer.getHeight();
        footerParams.topMargin = totalHeight;
        footerContainer.setLayoutParams(footerParams);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float curInterceptY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRefreshing || isLoadMore) {
                    intercept = false;
                } else {
                    boolean isHeaderShow = headerParams.topMargin > -headerHeight;
                    boolean isFooterShow = footerParams.topMargin < totalHeight;
                    intercept = touchHelper != null && touchHelper.onIntercept(curInterceptY, lastInterceptY, isHeaderShow, isFooterShow, allowLoadMore);
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        lastInterceptY = curInterceptY;

        return intercept;
    }

    private float moveDis;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (touchHelper != null) {
            float curTouchY = ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveDis = curTouchY - lastInterceptY;
                    if (Math.abs(moveDis) < touchSlop) {
                        break;
                    }
                    if (isRefreshing || isLoadMore) {
                        break;
                    }
                    moveDis = moveDis * MOVE_FACTOR;

                    if (touchHelper.isContentSlideToTop()) {
                        updateHeaderMargin(moveDis);
                    } else if (touchHelper.isContentSlideToBottom()) {
                        updateFooterMargin(moveDis);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (moveDis > 0) {
                        if (touchHelper.isContentSlideToTop()) {
                            if (headerParams.topMargin < 0) {
                                scrollHeaderByAnimator(headerParams.topMargin, -headerHeight);
                                if (header != null) {
                                    header.onPullToRefresh(moveDis);
                                }
                            } else {
                                scrollHeaderByAnimator(headerParams.topMargin, 0);
                                if (header != null) {
                                    header.onRefreshing();
                                }
                                isRefreshing = true;
                                if (listener != null) {
                                    listener.onRefresh();
                                }
                            }
                        }
                    } else {
                        if (touchHelper.isContentSlideToBottom()) {
                            if (footerParams.topMargin > totalHeight - footerHeight) {
                                scrollFooterByAnimator(false, footerParams.topMargin, totalHeight);
                                if (footer != null) {
                                    footer.onPullToLoadMore(moveDis);
                                }
                            } else {
                                scrollFooterByAnimator(false, footerParams.topMargin, totalHeight - footerHeight);
                                if (footer != null) {
                                    footer.onLoadMore();
                                }
                                isLoadMore = true;
                                if (listener != null) {
                                    listener.onLoadMore();
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public void onScrollToBottom() {
        if (isAutoLoadMore && allowLoadMore && !isLoadMore && !intercept) {
            scrollFooterByAnimator(true, totalHeight, totalHeight - footerHeight);
            if (listener != null) {
                listener.onLoadMore();
            }
            isLoadMore = true;
        }
    }

    private void updateHeaderMargin(float moveDis) {
        moveDis = moveDis < 0 ? 0 : moveDis;
        headerParams.topMargin = (int) (-headerHeight + moveDis);
        headerContainer.setLayoutParams(headerParams);

        setChildViewTopMargin((int) moveDis);

        if (header != null) {
            if (moveDis < headerHeight) {
                header.onPullToRefresh(moveDis);
            } else {
                header.onReleaseToRefresh(moveDis);
            }
        }
    }

    private void setChildViewTopMargin(int topMargin) {
        LayoutParams childParams = (LayoutParams) childView.getLayoutParams();
        childParams.topMargin = topMargin;
        childView.setLayoutParams(childParams);
    }

    private void updateFooterMargin(float moveDis) {
        moveDis = moveDis > 0 ? 0 : moveDis;
        footerParams.topMargin = (int) (totalHeight + moveDis);
        footerContainer.setLayoutParams(footerParams);

        setChildViewBottomMargin((int) Math.abs(moveDis));
        scrollContentToBottom((int) -moveDis);

        if (footer != null) {
            if (Math.abs(moveDis) < footerHeight) {
                footer.onPullToLoadMore(moveDis);
            } else {
                footer.onReleaseToLoadMore(moveDis);
            }
        }
    }

    private void setChildViewBottomMargin(int bottomMargin) {
        LayoutParams childParams = (LayoutParams) childView.getLayoutParams();
        childParams.bottomMargin = bottomMargin;
        childView.setLayoutParams(childParams);
    }

    private void scrollContentToBottom(int deltaY) {
        if (childView instanceof RecyclerView) {
            childView.scrollBy(0, deltaY);
        } else if (childView instanceof ListView) {
            ((ListView) childView).smoothScrollBy(deltaY, 0);
        } else if (childView instanceof ScrollView) {
            childView.scrollBy(0, deltaY);
        }
    }

    private void scrollHeaderByAnimator(float startY, float endY) {
        ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
        animator.setDuration(SCROLL_ANIMATOR_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float floatValue = (float) animation.getAnimatedValue();
                headerParams.topMargin = (int) floatValue;
                headerContainer.setLayoutParams(headerParams);

                int topMargin = (int) (headerHeight + floatValue);
                setChildViewTopMargin(topMargin);
            }
        });
        animator.start();
    }

    private void scrollFooterByAnimator(final boolean isAuto, float startY, float endY) {
        ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
        animator.setDuration(SCROLL_ANIMATOR_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float floatValue = (float) animation.getAnimatedValue();
                footerParams.topMargin = (int) floatValue;
                footerContainer.setLayoutParams(footerParams);

                int bottomMargin = (int) (totalHeight - floatValue);
                setChildViewBottomMargin(bottomMargin);
                if (isAuto) {
                    scrollContentToBottom(bottomMargin);
                    if (footer != null) {
                        footer.onPullToLoadMore(bottomMargin);
                        if (bottomMargin == footerHeight) {
                            footer.onLoadMore();
                        }
                    }
                }
            }
        });
        animator.start();
    }

    public void setListener(OnRefreshLoadMoreListener listener) {
        this.listener = listener;
    }

    public void setIsAutoLoadMore(boolean isAutoLoadMore) {
        this.isAutoLoadMore = isAutoLoadMore;
    }

    public void setAllowLoadMore(boolean allowLoadMore) {
        this.allowLoadMore = allowLoadMore;
    }

    public void setHeader(IHeader header) {
        if (header instanceof View) {
            this.header = header;
            headerContainer.addView((View) header);
        }
    }

    public void setDefaultHeader(int height) {
        LoadTextHeader header = new LoadTextHeader(getContext());
        header.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
        setHeader(header);
    }

    public void setFooter(IFooter footer) {
        if (footer instanceof View) {
            this.footer = footer;
            footerContainer.addView((View) footer);
        }
    }

    public void setDefaultFooter(int height) {
        LoadTextFooter footer = new LoadTextFooter(getContext());
        footer.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height));
        setFooter(footer);
    }

    public void endRefreshing() {
        isRefreshing = false;
        scrollHeaderByAnimator(0, -headerHeight);
        if (header != null) {
            header.onRefreshEnd();
        }
    }

    public void endLoadMore() {
        isLoadMore = false;
        scrollFooterByAnimator(false, totalHeight - footerHeight, totalHeight);
        if (footer != null) {
            footer.onLoadMoreEnd();
        }
    }

    public interface OnRefreshLoadMoreListener {

        void onRefresh();

        void onLoadMore();
    }
}
