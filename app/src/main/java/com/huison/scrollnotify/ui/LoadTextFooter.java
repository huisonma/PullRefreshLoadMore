package com.huison.scrollnotify.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;

/**
 * Created by huison on 2018/1/9.
 */

public class LoadTextFooter extends AppCompatTextView implements IFooter {

    public LoadTextFooter(Context context) {
        this(context, null);
    }

    public LoadTextFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadTextFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onPullToLoadMore(float moveDis) {
        setText("上拉加载");
        int height = getHeight();
        float scale = Math.abs(moveDis) / height;
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        setScaleX(scale);
        setScaleY(scale);
    }

    @Override
    public void onReleaseToLoadMore(float moveDis) {
        setText("释放加载");
    }

    private ValueAnimator animator;

    @Override
    public void onLoadMore() {
        setText("加载中...");
        animator = ValueAnimator.ofFloat(100, 0, -100, 0);
        animator.setDuration(800);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float floatValue = (float) animation.getAnimatedValue();
                setTranslationX(floatValue);
            }
        });
        animator.start();
    }

    @Override
    public void onLoadMoreEnd() {
        if (animator != null) {
            animator.cancel();
            setTranslationX(0);
        }
    }
}
