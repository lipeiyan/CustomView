package com.payne.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.payne.customview.R;
import com.payne.customview.util.Utils;


/**
 * <p>描述:打卡时间进度自定义view</p>
 * 作者： lpy<br>
 * 日期： 2019-03-06 09:41<br>
 * 版本： v1.0.0<br>
 */
public class PunchTimeView extends View {

    private Context mContext;

    private Paint mPaint;

    private int mLineColor;
    private float mLineWidth;
    private int mProgressColor;
    private float mProgress;
    private boolean mLineVisible = false;

    private int mMarginTop;

    public PunchTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PunchTimeView);
        mLineColor = array.getColor(R.styleable.PunchTimeView_timeLineColor, Color.parseColor("#c6c6c6"));
        mLineWidth = array.getDimension(R.styleable.PunchTimeView_timeLineWidth, 2);
        mProgressColor = array.getColor(R.styleable.PunchTimeView_progressColor, Color.parseColor("#0000ff"));
        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mMarginTop = Utils.dip2px(mContext, 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
    }

    /**
     * 绘制细线
     */
    private void drawLine(Canvas canvas) {
        //进度条和细线相交的Y坐标
        float mPositionY = (getMeasuredHeight() - mMarginTop) * (1 - mProgress);

        if (mPositionY < 0) {
            mPositionY = 0;
        }
        mPositionY = mPositionY + mMarginTop;

        if (mLineVisible) {//绘制细线
            mPaint.setStrokeWidth(mLineWidth);
            mPaint.setColor(mLineColor);
            canvas.drawLine(getMeasuredWidth() >> 1, mPositionY, getMeasuredWidth() >> 1, 0, mPaint);
        }
        //绘制进度
        mPaint.setStrokeWidth(getMeasuredWidth());
        mPaint.setColor(mProgressColor);
        canvas.drawLine(getMeasuredWidth() >> 1, getMeasuredHeight(), getMeasuredWidth() >> 1, mPositionY, mPaint);
    }

    /**
     * 绘制进度
     *
     * @param progress
     */
    public void drawProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    /**
     * 是否显示细线
     *
     * @param visible
     */
    public void showLine(boolean visible) {
        mLineVisible = visible;
        invalidate();
    }
}
