package com.payne.customview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.payne.customview.R;

/**
 * 描述：测肤页面虚线
 * 作者：lipeiyan
 * 版本：v1.0.0
 */
public class DotLine extends View {
    private DIRECTION mDirection;
    private Paint mPaint;
    private Path mPath;
    private int mWidth, mHeight;
    private float mStrokeWidth;//虚线宽度
    private float mCircleRadius;//第一个圆半径大小
    private DashPathEffect mDashPathEffect;
    private int mCircleColor;//第一个圆颜色
    private int mLineColor;//虚线颜色

    /**
     * 虚线绘制方向
     */
    public enum DIRECTION {
        LEFT_RIGHT, LEFT_BOTTOM_RIGHT, LEFT_TOP_RIGHT, RIGHT_LEFT, RIGHT_BOTTOM_LEFT, RIGHT_TOP_LEFT
    }

    public DotLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DotLine);
        int direction = array.getInt(R.styleable.DotLine_LineDirection, 0);
        mStrokeWidth = array.getDimension(R.styleable.DotLine_LineWidth, 10f);
        mCircleRadius = array.getDimension(R.styleable.DotLine_LineCircleRadius, 10f);
        mCircleColor = array.getColor(R.styleable.DotLine_LineCircleColor, Color.WHITE);
        mLineColor = array.getColor(R.styleable.DotLine_LineColor, Color.WHITE);
        switch (direction) {
            case 0:
                mDirection = DIRECTION.LEFT_RIGHT;
                break;
            case 1:
                mDirection = DIRECTION.LEFT_BOTTOM_RIGHT;
                break;
            case 2:
                mDirection = DIRECTION.LEFT_TOP_RIGHT;
                break;
            case 3:
                mDirection = DIRECTION.RIGHT_LEFT;
                break;
            case 4:
                mDirection = DIRECTION.RIGHT_BOTTOM_LEFT;
                break;
            case 5:
                mDirection = DIRECTION.RIGHT_TOP_LEFT;
                break;
        }
        array.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
        mDashPathEffect = new DashPathEffect(new float[]{0.01f, mStrokeWidth * 1.5f}, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mDirection) {
            case LEFT_RIGHT:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mPaint);

                mPaint.setColor(mLineColor);
                mPaint.setPathEffect(mDashPathEffect);
                mPaint.setStyle(Paint.Style.STROKE);
                mPath.moveTo(mCircleRadius * 2 + mStrokeWidth, mCircleRadius);
                mPath.lineTo(mWidth, mCircleRadius);
                break;
            case LEFT_BOTTOM_RIGHT:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mPaint);

                mPaint.setColor(mLineColor);
                mPaint.setPathEffect(mDashPathEffect);
                mPaint.setStyle(Paint.Style.STROKE);
                mPath.moveTo(mCircleRadius, mCircleRadius * 2 + mStrokeWidth);
                mPath.lineTo(mCircleRadius, mHeight - mStrokeWidth / 2);
                mPath.lineTo(mWidth, mHeight - mStrokeWidth / 2);
                break;
            case LEFT_TOP_RIGHT:

                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(mCircleRadius, mHeight - mCircleRadius, mCircleRadius, mPaint);

                mPaint.setColor(mLineColor);
                mPaint.setPathEffect(mDashPathEffect);
                mPaint.setStyle(Paint.Style.STROKE);
                mPath.moveTo(mCircleRadius, mHeight - mCircleRadius * 2 - mStrokeWidth);
                mPath.lineTo(mCircleRadius, mStrokeWidth / 2);
                mPath.lineTo(mWidth, mStrokeWidth / 2);
                break;
            case RIGHT_LEFT:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(mWidth - mCircleRadius, mCircleRadius, mCircleRadius, mPaint);

                mPaint.setColor(mLineColor);
                mPaint.setPathEffect(mDashPathEffect);
                mPaint.setStyle(Paint.Style.STROKE);
                mPath.moveTo(mWidth - mCircleRadius * 2 - mStrokeWidth, mCircleRadius);
                mPath.lineTo(0, mCircleRadius);
                break;
            case RIGHT_BOTTOM_LEFT:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(mWidth - mCircleRadius, mCircleRadius, mCircleRadius, mPaint);

                mPaint.setColor(mLineColor);
                mPaint.setPathEffect(mDashPathEffect);
                mPaint.setStyle(Paint.Style.STROKE);
                mPath.moveTo(mWidth - mCircleRadius, mCircleRadius * 2 + mStrokeWidth);
                mPath.lineTo(mWidth - mCircleRadius, mHeight - mStrokeWidth / 2);
                mPath.lineTo(0, mHeight - mStrokeWidth / 2);
                break;
            case RIGHT_TOP_LEFT:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCircleColor);
                canvas.drawCircle(mWidth - mCircleRadius, mHeight - mCircleRadius, mCircleRadius, mPaint);

                mPaint.setColor(mLineColor);
                mPaint.setPathEffect(mDashPathEffect);
                mPaint.setStyle(Paint.Style.STROKE);

                mPath.moveTo(mWidth - mCircleRadius, mHeight - mCircleRadius * 2 - mStrokeWidth);
                mPath.lineTo(mWidth - mStrokeWidth, mStrokeWidth / 2);
                mPath.lineTo(0, mStrokeWidth / 2);
                break;


        }

        mPaint.setStrokeWidth(mStrokeWidth);//线条宽度
        canvas.drawPath(mPath, mPaint);
    }
}
