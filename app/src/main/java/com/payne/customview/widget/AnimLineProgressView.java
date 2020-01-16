package com.payne.customview.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.payne.customview.R;

/**
 * 描述：矩形进度条
 * 作者：lipeiyan
 * 版本：v1.0.0
 */
public class AnimLineProgressView extends View {
    private Paint mPaint;
    private float mProgress;
    private int mColor;
    private int mBackColor;

    public AnimLineProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AnimLineProgressView);
        mColor = array.getColor(R.styleable.AnimLineProgressView_AnimProgressColor, Color.RED);
        mBackColor = array.getColor(R.styleable.AnimLineProgressView_AnimProgressBackColor, Color.GRAY);
        array.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void drawProgress(Canvas canvas) {
        mPaint.setColor(mBackColor);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setColor(mColor);
        canvas.drawRect(0, 0, getMeasuredWidth() * mProgress, getMeasuredHeight(), mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
    }

    public float getProgress() {
        return mProgress;
    }

    @Keep
    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    public void startAnim(final float progress, final boolean isAnim, final long time) {
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, "progress", 0f, progress);
        progressAnimation.setDuration(isAnim ? time : 0);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.start();
    }
}
