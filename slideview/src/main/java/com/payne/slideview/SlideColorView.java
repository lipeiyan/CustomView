package com.payne.slideview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Administrator on 2017/3/15.
 */

public class SlideColorView extends View {
    private Rect mTextRect;
    private Rect mColorRect;
    private Paint mColorPaint;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mPurpleColor = Color.argb(255, 255, 0, 255);
    private int mGreenColor = Color.argb(255, 0, 255, 0);

    public SlideColorView(Context context) {
        super(context);
        init(context);
    }

    public SlideColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mColorPaint = new Paint();
        mBackgroundPaint = new Paint();
        mTextPaint = new Paint();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {


            @Override
            public void onGlobalLayout() {
                mTextRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                mColorRect = new Rect(0, 0, 0, getMeasuredHeight());
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBackgroundPaint.setColor(mPurpleColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mTextRect, mBackgroundPaint);

        mColorPaint.setColor(mGreenColor);
        mColorPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mColorRect, mColorPaint);

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(50);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        int baseLineY = (int) (mTextRect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式

        canvas.drawText(">>> 滑动清屏", mTextRect.centerX(), baseLineY, mTextPaint);

    }

    public void setProgress(int progress) {
        mColorRect.set(0, 0, progress, getMeasuredHeight());
        invalidate();
    }
}
