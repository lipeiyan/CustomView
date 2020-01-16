package com.payne.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.payne.customview.R;


/**
 * 描述：圆形边框
 * 作者：lipeiyan
 * 版本：v1.0.0
 */
public class RingView extends View {
    protected float mRingWidth;
    protected Paint mPaint;
    private float mRadius;
    private float cx, cy;

    public RingView(Context context) {
        this(context, null);
    }

    public RingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RingView);
        int color = array.getColor(R.styleable.RingView_RingColor, Color.RED);
        mRingWidth = array.getDimension(R.styleable.RingView_RingWidth, 4);
        array.recycle();
        this.mPaint = new Paint();
        mPaint.setColor(color);
        this.mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mRingWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRadius = Math.min(getMeasuredHeight(), getMeasuredWidth()) / 2.0f - mRingWidth;
        cx = getMeasuredWidth() / 2;
        cy = getMeasuredHeight() / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(cx, cy, mRadius, mPaint);


    }


}