package com.payne.customview.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.payne.customview.R;


/**
 * 描述：带有进度的环状进度条
 * 作者：lipeiyan
 * 版本：v1.0.0
 */
public class RingProgressView extends View {
    public static final int PROGRESS_FACTOR = -360;
    protected RectF mArcElements;
    protected int mRingWidth;
    protected Paint mPaint;
    protected Paint mCirclePaint;
    protected Paint mTextPaint;
    protected float mProgress;//进度大小


    protected int mRingProgressStartColor;//进度渐变色开始颜色
    protected int mRingProgressEndColor;//进度进变色结束颜色
    protected int mRingBackColor;//进度背景颜色
    protected int mRingCirCleColor;//原点颜色
    protected int mRingCirCleStrokeColor;//圆点边框颜色
    protected float mRingProgressNumTextSize;//进度数字大小
    protected float mRingProgressTitleTextSize;//描述文字大小
    protected float mNumMarginTop;//数字上面的间距
    protected float mTitleMarginTop;//标题上方间距
    protected int mRingProgressTextColor;//文字颜色
    protected String mProgressTitle;//进度描述文字
    protected long mAnimTime;//动画时间
    protected boolean mShowPercentSymbol;//动画时间
    protected boolean mShowText;//是否显示标题
    protected boolean mShowCircle;//是否显示指示圆点
    protected int mStrokeCap;//绘制头部的类型

    private LinearGradient mShader;//渐变
    private Rect mTextRect;
    private int mCursorCircleRadius;//指示圆点的半径
    private int mCursorCircleStrokeWidth;//指示半径的边框宽度

    private int mCircleMargin;
    private int mRadius;

    public RingProgressView(Context context) {
        this(context, null);
    }

    public RingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RingProgressView);
        mRingProgressStartColor = array.getColor(R.styleable.RingProgressView_ringProgressStartColor, Color.RED);
        mRingProgressEndColor = array.getColor(R.styleable.RingProgressView_ringProgressEndColor, Color.GREEN);
        mRingBackColor = array.getColor(R.styleable.RingProgressView_ringProgressBackColor, Color.GRAY);
        mRingCirCleColor = array.getColor(R.styleable.RingProgressView_ringProgressCirCleColor, Color.YELLOW);
        mRingCirCleStrokeColor = array.getColor(R.styleable.RingProgressView_ringProgressCirCleStrokeColor, Color.RED);
        mRingWidth = (int) array.getDimension(R.styleable.RingProgressView_ringWidth, 20);
        mProgressTitle = array.getString(R.styleable.RingProgressView_ringProgressTitle);
        mRingProgressNumTextSize = array.getDimension(R.styleable.RingProgressView_ringProgressNumTextSize, 20);
        mNumMarginTop = array.getDimension(R.styleable.RingProgressView_ringProgressNumMarginTop, 0);
        mTitleMarginTop = array.getDimension(R.styleable.RingProgressView_ringProgressTitleMarginTop, 0);
        mRingProgressTitleTextSize = array.getDimension(R.styleable.RingProgressView_ringProgressTitleTextSize, 10);
        mRingProgressTextColor = array.getColor(R.styleable.RingProgressView_ringProgressTextColor, Color.WHITE);
        mProgress = array.getFloat(R.styleable.RingProgressView_ringProgressProgress, 0.0f);
        mAnimTime = array.getInt(R.styleable.RingProgressView_ringProgressAnimTime, 1000);
        mShowPercentSymbol = array.getBoolean(R.styleable.RingProgressView_ringProgressShowPercentSymbol, true);
        mShowText = array.getBoolean(R.styleable.RingProgressView_ringProgressShowText, true);
        mShowCircle = array.getBoolean(R.styleable.RingProgressView_ringProgressShowCircle, true);
        mStrokeCap = array.getInt(R.styleable.RingProgressView_ringProgressStrokeCap, 0);

        array.recycle();

        this.mTextPaint = new Paint();
        this.mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        this.mCirclePaint = new Paint();
        this.mCirclePaint.setAntiAlias(true);

        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mRingWidth);

        switch (mStrokeCap) {
            case 0:
                mPaint.setStrokeCap(Paint.Cap.BUTT);
                break;
            case 1:
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case 2:
                mPaint.setStrokeCap(Paint.Cap.SQUARE);
                break;
        }

        this.mArcElements = new RectF();
        mTextRect = new Rect();
        mCursorCircleRadius = mRingWidth / 2;
        mCursorCircleStrokeWidth = mRingWidth / 4;
        mCircleMargin = mCursorCircleStrokeWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredHeight(), getMeasuredWidth());
        float outerRadius = (size / 2) - mCircleMargin;
        float offsetX = (getMeasuredWidth() - size) / 2;
        float offsetY = (getMeasuredHeight() - size) / 2;

        if (mShader == null) {
            mShader = new LinearGradient(0, size / 4, 0, size * 3 / 4, new int[]{mRingProgressStartColor, mRingProgressEndColor},
                    null, Shader.TileMode.CLAMP);
        }

        int halfRingWidth = mRingWidth / 2;
        float arcX0 = mCircleMargin + offsetX + halfRingWidth;
        float arcY0 = mCircleMargin + offsetY + halfRingWidth;
        float arcX1 = offsetX + size - halfRingWidth - mCircleMargin;
        float arcY1 = offsetY + size - halfRingWidth - mCircleMargin;
        mArcElements.set(arcX0, arcY0, arcX1, arcY1);

        mRadius = (int) (outerRadius - halfRingWidth);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArcProgress(canvas);
        if (mShowText) {
            int progressNumHeight = drawNumProgress(canvas);
            drawTitle(canvas, progressNumHeight);
        }
        if (mShowCircle) {
            drawCircle(canvas);
        }
    }


    /**
     * 绘制圆弧进度
     */
    private void drawArcProgress(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setShader(null);
        mPaint.setColor(mRingBackColor);
        canvas.drawArc(mArcElements, 0, 360, false, mPaint);

        mPaint.setShader(mShader);
        canvas.drawArc(mArcElements, -90, -mProgress * PROGRESS_FACTOR, false, mPaint);
    }

    /**
     * 绘制进度数字
     */
    private int drawNumProgress(Canvas canvas) {
        int progressText = (int) (mProgress * 100);
        String progressNum;
        if (mShowPercentSymbol) {
            progressNum = progressText + "%";
        } else {
            progressNum = progressText + "";
        }
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mRingProgressTextColor);
        mTextPaint.setTextSize(mRingProgressNumTextSize);
        mTextPaint.getTextBounds(progressNum, 0, progressNum.length(), mTextRect);

        int progressNumWidth = mTextRect.width();
        int progressNumHeight = mTextRect.height();

        if (mNumMarginTop == 0) {
            mNumMarginTop = mRadius * 0.25f;
        }
        canvas.drawText(progressNum, getMeasuredWidth() / 2, mRingWidth + mCursorCircleStrokeWidth + mNumMarginTop + progressNumHeight, mTextPaint);

        return progressNumHeight;
    }

    /**
     * 绘制标题
     */
    private void drawTitle(Canvas canvas, int progressNumHeight) {
        if (mTitleMarginTop == 0) {
            mTitleMarginTop = mRingProgressTitleTextSize * 0.5f;
        }
        mTextPaint.setTextSize(mRingProgressTitleTextSize);
        mTextPaint.getTextBounds(mProgressTitle, 0, mProgressTitle.length(), mTextRect);
        int textWidth = mTextRect.width();
        int textHeight = mTextRect.height();

        canvas.drawText(mProgressTitle, getMeasuredWidth() / 2, mRingWidth + mCursorCircleStrokeWidth + mNumMarginTop + progressNumHeight + mTitleMarginTop + textHeight, mTextPaint);

    }

    public float getProgress() {
        return mProgress;
    }

    @Keep
    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    /**
     * 开始进度动画
     *
     * @param progress 进度 例如 0.8f
     * @param isAnim   是否使用动画
     * @param time     动画时间
     */
    public void startAnim(final float progress, final boolean isAnim, final long time) {
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, "progress", 0f, progress);
        progressAnimation.setDuration(isAnim ? time : 0);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.start();
    }

    /**
     * 绘制圆点
     */
    private void drawCircle(Canvas canvas) {

        mCirclePaint.setColor(mRingCirCleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        canvas.drawCircle((float) (getMeasuredWidth() * 0.5f + mRadius * Math.sin(mProgress * Math.PI * 2)), (float) (getMeasuredHeight() * 0.5f - mRadius * Math.cos(mProgress * Math.PI * 2)), mCursorCircleRadius, mCirclePaint);

        mCirclePaint.setColor(mRingCirCleStrokeColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);//画笔属性是实心圆
        mCirclePaint.setStrokeWidth(mCursorCircleStrokeWidth);
        canvas.drawCircle((float) (getMeasuredWidth() * 0.5f + mRadius * Math.sin(mProgress * Math.PI * 2)), (float) (getMeasuredHeight() * 0.5f - mRadius * Math.cos(mProgress * Math.PI * 2)), mCursorCircleRadius + mCursorCircleStrokeWidth * 0.5f, mCirclePaint);
    }

    public void setProgressColor(int startColor, int endColor) {
        mRingProgressStartColor = startColor;
        mRingProgressEndColor = endColor;
    }
}