package com.payne.customview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.payne.customview.R;


public class SeekColorView extends View {

    private float mCircleRadius;
    private float mCircleX;
    private int mCircleColor;
    private int mProgress = 0;
    private int mLastProgress = 0;
    private int mMaxProgress = 100;
    private Paint mPaint;
    private Paint mCirclePaint;
    private LinearGradient mGradient;
    private int mStartColor, mEndColor;

    public SeekColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SeekColorView);
        mStartColor = array.getColor(R.styleable.SeekColorView_start_color, Color.RED);
        mEndColor = array.getColor(R.styleable.SeekColorView_end_color, Color.WHITE);
        mCircleColor = array.getColor(R.styleable.SeekColorView_circle_color, Color.RED);
        mProgress = array.getInt(R.styleable.SeekColorView_progress, 0);
        array.recycle();

        mPaint = new Paint();
        mPaint.setColor(mCircleColor);
        mPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCircleRadius = getMeasuredHeight() / 2.0f;
        float tintWidth = getMeasuredHeight() * 0.16f;
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(tintWidth);
        mCircleX = mCircleRadius + mProgress * 1f / mMaxProgress * (getMeasuredWidth() - mCircleRadius * 2);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            mGradient = new LinearGradient(0, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2,
                    mStartColor, mEndColor, Shader.TileMode.CLAMP);
            mPaint.setShader(mGradient);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mCircleRadius, getMeasuredHeight() / 2, getMeasuredWidth() - mCircleRadius, getMeasuredHeight() / 2, mPaint);
        canvas.drawCircle(mCircleX, getMeasuredHeight() / 2, mCircleRadius, mCirclePaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastProgress = mProgress;
            case MotionEvent.ACTION_MOVE:
                int progress = Math.round((event.getX() - mCircleRadius) / (getMeasuredWidth() - mCircleRadius * 2) * 100);
                if (progress >= 0 && progress <= 100 && mProgress != progress) {
                    mProgress = progress;
                    mCircleX = mCircleRadius + mProgress * 1f / mMaxProgress * (getMeasuredWidth() - mCircleRadius * 2);

                    if (onSeekChangeListener != null) {
                        onSeekChangeListener.onProgressChanged(mProgress);
                    }

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                int seekProgress = Math.round((event.getX() - mCircleRadius) / (getMeasuredWidth() - mCircleRadius * 2) * 100);

                if (mLastProgress != seekProgress && onSeekChangeListener != null) {
                    mProgress = seekProgress;
                    onSeekChangeListener.onSeek(mProgress);
                }
                break;
        }
        return true;

    }

    /**
     * 设置渐变色
     *
     * @param startColor 起始颜色
     * @param endColor   结束颜色
     */
    public void setGradientColors(int startColor, int endColor) {
        mStartColor = startColor;
        mEndColor = endColor;
        mGradient = new LinearGradient(0, 0, getMeasuredWidth(), getMeasuredHeight(),
                startColor, endColor, Shader.TileMode.CLAMP);
        mPaint.setShader(mGradient);

        invalidate();
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */
    public void setSeekProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

    /**
     * 设置渐变色
     *
     * @param startColor 起始颜色
     * @param endColor   结束颜色
     */
    public void setGradientColors(int startColor, int endColor, int progress) {
        mStartColor = startColor;
        mEndColor = endColor;
        mProgress = progress;
        mGradient = new LinearGradient(0, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2,
                startColor, endColor, Shader.TileMode.CLAMP);
        mPaint.setShader(mGradient);
        mProgress = progress;
        mCircleX = mCircleRadius + mProgress * 1f / mMaxProgress * (getMeasuredWidth() - mCircleRadius * 2);

        invalidate();
    }

    public int getMax() {
        return mMaxProgress;
    }

    private onSeekChangeListener onSeekChangeListener;

    public void setOnSeekChangeListener(onSeekChangeListener onSeekChangeListener) {
        this.onSeekChangeListener = onSeekChangeListener;
    }

    public interface onSeekChangeListener {
        void onSeek(int progress);//抬手

        void onProgressChanged(int progress);//滑动
    }

}
