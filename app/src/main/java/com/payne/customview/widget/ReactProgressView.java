package com.payne.customview.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.payne.customview.R;


/**
 * 描述：有动画的方块进度条
 * 作者：lipeiyan
 * 版本：v1.0.0
 */
public class ReactProgressView extends View {
    private int mReactNum;//总的方块数目
    private int mProgressNum = 0;
    private long mTotalTime = 2000;//动画执行总时间
    private int mReactProgressColor;//进度颜色
    private int mReactBackColor;//背景颜色
    private int mRectWidth;//每个矩形块的宽度
    private int mRectDistance;//矩形块之间的间距
    private int mCornerRadius;//圆角半径
    private float mRectProgress;//进度


    private Paint mPaint;

    public ReactProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }


    protected void initialize(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ReactProgressView);
        mReactNum = array.getInt(R.styleable.ReactProgressView_reactNum, 10);
        mReactBackColor = array.getColor(R.styleable.ReactProgressView_reactBackColor, Color.GRAY);
        mReactProgressColor = array.getColor(R.styleable.ReactProgressView_reactProgressColor, Color.RED);
        mRectWidth = (int) array.getDimension(R.styleable.ReactProgressView_rectWidth, 30);
        mRectDistance = (int) array.getDimension(R.styleable.ReactProgressView_rectDistance, 30);
        mCornerRadius = (int) array.getDimension(R.styleable.ReactProgressView_reactCornerRadius, 10);
        mRectProgress = array.getFloat(R.styleable.ReactProgressView_rectProgress, 0.0f);
        mTotalTime = array.getInt(R.styleable.ReactProgressView_reactTotalTime, 1000);
        array.recycle();
        startAnim(mRectProgress, true, mTotalTime);

    }


    public void startAnim(float progress, boolean isAnim, long time) {
        mTotalTime = time;
        mProgressNum = 0;
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, "progress", 0f, progress);
        progressAnimation.setDuration(isAnim ? time : 0);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackReact(canvas);
        drawProgressReact(canvas);
    }

    /**
     * 绘制背景方块
     */
    private void drawBackReact(Canvas canvas) {
        mPaint.setColor(mReactBackColor);
        for (int i = 0; i < mReactNum; i++) {
            canvas.drawRoundRect((mRectWidth + mRectDistance) * i, 0, (mRectWidth + mRectDistance) * i + mRectWidth, getMeasuredHeight(), mCornerRadius, mCornerRadius, mPaint);
        }
    }

    /**
     * 绘制进度方块
     */
    private void drawProgressReact(Canvas canvas) {
        mProgressNum = (int) (mReactNum * mRectProgress + 0.5);
        mPaint.setColor(mReactProgressColor);
        for (int i = 0; i < mProgressNum; i++) {
            canvas.drawRoundRect((mRectWidth + mRectDistance) * i, 0, (mRectWidth + mRectDistance) * i + mRectWidth, getMeasuredHeight(), mCornerRadius, mCornerRadius, mPaint);
        }


    }

    public float getProgress() {
        return mRectProgress;
    }

    @Keep
    public void setProgress(float progress) {
        this.mRectProgress = progress;
        invalidate();
    }

}