package com.huison.scrollnotify.ui;

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

import com.huison.scrollnotify.R;

/**
 * Created by huison on 2018/1/9.
 */

public class PullRefreshLoadMoreLayout extends FrameLayout implements TouchHelperBase.OnScrollListener {

    private static final String TAG = "PullRefreshLoadMore";

    private static final float kMoveFactor = 0.3f;
    private static final int kDuration = 100;

    private FrameLayout headerVG;
    private LayoutParams headerParams;
    private FrameLayout footerVG;
    private LayoutParams footerParams;
    private IHeader header;
    private IFooter footer;

    private int height;
    private int headerHeight;
    private int footerHeight;
    private int touchSlop;

    private View childView;

    private boolean isRefreshing;
    private boolean isLoadMore;
    private boolean isAutoLoadMore;
    private boolean allowLoadMore;

    private TouchHelperBase touchHelper;
    private OnRefreshLoadMoreListener listener;


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

        headerVG = new FrameLayout(context);
        headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        headerVG.setLayoutParams(headerParams);
        addView(headerVG, headerParams);

        footerVG = new FrameLayout(context);
        footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        footerVG.setLayoutParams(footerParams);
        addView(footerVG, footerParams);
    }

    private boolean isFirst = true;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed && isFirst) {
            int childCount = getChildCount();
            for (int index = 0; index < childCount; index++) {
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

            height = getHeight();
            headerHeight = headerVG.getHeight();
            headerParams.topMargin = -headerHeight;
            headerVG.setLayoutParams(headerParams);
            footerHeight = footerVG.getHeight();
            footerParams.topMargin = height;
            footerVG.setLayoutParams(footerParams);

            isFirst = false;
        }
    }

    private boolean intercept;
    private float lastInterceptY;

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
                    boolean isFooterShow = footerParams.topMargin < height;
                    intercept = touchHelper != null && touchHelper.judgeIntercept(curInterceptY, lastInterceptY, isHeaderShow, isFooterShow, allowLoadMore);
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
                    moveDis = moveDis * kMoveFactor;

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
                            if (footerParams.topMargin > height - footerHeight) {
                                scrollFooterByAnimator(false, footerParams.topMargin, height);
                                if (footer != null) {
                                    footer.onPullToLoadMore(moveDis);
                                }
                            } else {
                                scrollFooterByAnimator(false, footerParams.topMargin, height - footerHeight);
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
            scrollFooterByAnimator(true, height, height - footerHeight);
            if (listener != null) {
                listener.onLoadMore();
            }
            isLoadMore = true;
        }
    }

    private void updateHeaderMargin(float moveDis) {
        moveDis = moveDis < 0 ? 0 : moveDis;
        headerParams.topMargin = (int) (-headerHeight + moveDis);
        headerVG.setLayoutParams(headerParams);

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
        footerParams.topMargin = (int) (height + moveDis);
        footerVG.setLayoutParams(footerParams);

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
        animator.setDuration(kDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float floatValue = (float) animation.getAnimatedValue();
                headerParams.topMargin = (int) floatValue;
                headerVG.setLayoutParams(headerParams);

                setChildViewTopMargin((int) (headerHeight + floatValue));
            }
        });
        animator.start();
    }

    private void scrollFooterByAnimator(final boolean isAuto, float startY, float endY) {
        ValueAnimator animator = ValueAnimator.ofFloat(startY, endY);
        animator.setDuration(kDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float floatValue = (float) animation.getAnimatedValue();
                footerParams.topMargin = (int) floatValue;
                footerVG.setLayoutParams(footerParams);

                int bottomMargin = (int) (height - floatValue);
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
        if (header != null && header instanceof View) {
            this.header = header;
            headerVG.addView((View) header);
        }
    }

    public void setFooter(IFooter footer) {
        if (footer != null && footer instanceof View) {
            this.footer = footer;
            footerVG.addView((View) footer);
        }
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
        scrollFooterByAnimator(false, height - footerHeight, height);
        if (footer != null) {
            footer.onLoadMoreEnd();
        }
    }
}
