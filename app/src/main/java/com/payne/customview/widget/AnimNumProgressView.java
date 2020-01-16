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
 * 描述：数字增长动画进度条
 * 作者：lipeiyan
 * 版本：v1.0.0
 */
public class AnimNumProgressView extends View {
    private Paint mPaint;
    private float mProgress;
    private float mTextSize;
    private boolean mShowSymbol;//是否显示百分比符号

    public AnimNumProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawNumText(canvas);
    }

    private void init(AttributeSet attrs) {

        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.AnimNumProgressView);
        int color = array.getColor(R.styleable.AnimNumProgressView_TextColor, Color.RED);
        mTextSize = array.getDimension(R.styleable.AnimNumProgressView_TextSize, 40);
        mShowSymbol = array.getBoolean(R.styleable.AnimNumProgressView_ShowSymbol, true);
        array.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(color);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTextSize);
    }

    private void drawNumText(Canvas canvas) {
        int num = (int) (mProgress * 100);
        if (mShowSymbol) {
            canvas.drawText(num + "%", mTextSize, mTextSize, mPaint);
        } else {
            canvas.drawText(num + "", mTextSize, mTextSize, mPaint);

        }
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
