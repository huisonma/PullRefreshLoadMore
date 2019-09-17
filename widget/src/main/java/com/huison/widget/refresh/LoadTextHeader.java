package com.huison.widget.refresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;

/**
 * Created by huisonma on 2018/1/9.
 */

public class LoadTextHeader extends AppCompatTextView implements IHeader {

    public LoadTextHeader(Context context) {
        this(context, null);
    }

    public LoadTextHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadTextHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onPullToRefresh(float moveDis) {
        setText("下拉刷新");
        int height = getHeight();
        float scale = moveDis / height;
        setPivotX(getWidth() / 2);
        setPivotY(getHeight() / 2);
        setScaleX(scale);
        setScaleY(scale);
    }

    @Override
    public void onReleaseToRefresh(float moveDis) {
        setText("释放刷新");
    }

    private ValueAnimator animator;

    @Override
    public void onRefreshing() {
        setText("刷新中...");
        animator = ValueAnimator.ofFloat(0, 30, 0, -30, 0);
        animator.setDuration(800);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
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
    public void onRefreshEnd() {
        if (animator != null) {
            animator.cancel();
            setTranslationX(0);
        }
    }
}
