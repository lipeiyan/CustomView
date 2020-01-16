package com.payne.customview.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.payne.customview.R;


/**
 * 描述：颜色选择view
 * 作者：lipeiyan
 * 版本：v1.0.0
 *
 * @author payne
 */
public class ColorView extends View {
    protected RectF mArcElements;
    protected float mRingWidth;
    protected float mRoundRadius;
    protected Paint mPaint;
    protected Paint mInnerPaint;
    float centerX;
    float centerY;
    private int mChooseColor;
    private Resources mResources;
    private int[] mColorArray;
    private int mColorIndex;
    private float mProgress;
    private int mIndex;

    private boolean mIsAnim;
    private Context mContext;

    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.BUTT);

        this.mInnerPaint = new Paint();
        this.mInnerPaint.setAntiAlias(true);
        mInnerPaint.setStrokeCap(Paint.Cap.BUTT);

        this.mArcElements = new RectF();
        mResources = context.getResources();
        mColorArray = initColorArray(mResources);
        mChooseColor = mColorArray[0];

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredHeight(), getMeasuredWidth());
        mRingWidth = size / 8.0f;
        mRoundRadius = size / 4.0f;
        mPaint.setStrokeWidth(mRingWidth);
        mArcElements.set(mRingWidth / 2, mRingWidth / 2, size - mRingWidth / 2, size - mRingWidth / 2);
        centerX = mArcElements.centerX();
        centerY = mArcElements.centerY();
    }

    private double mLastArc = 0;
    private float mStartAngle = 0;
    private float mAngleDiff = 0;

    private long mDownTime;
    private PointF mDownPoint = new PointF();
    private boolean mIsMove;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsAnim) {//执行动画时禁止触摸动作
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mIsMove = true;
                long time = System.currentTimeMillis();
                if (time - mDownTime >= 250 || calSlideLength(mDownPoint, event.getX(), event.getY()) >= dip2px(mContext, 1)) {
                    double arc = Math.atan2(event.getY() - centerY, event.getX() - centerX);
                    double arcDiff = arc - mLastArc;
                    mStartAngle = mStartAngle + (float) (arcDiff * 180 / Math.PI);
                    mLastArc = arc;
                    Log.e("mStartAngle", mStartAngle + "");
                    mColorIndex = (24 + Math.round(mStartAngle / -15)) % 24;
                    if (mColorIndex < 0) {
                        mColorIndex += 24;
                    }
                    mChooseColor = mColorArray[mColorIndex];
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_DOWN:
                mIsMove = false;
                if (!isAtRing(event.getX(), event.getY())) {
                    return false;
                }
                mDownTime = System.currentTimeMillis();
                mDownPoint.set(event.getX(), event.getY());
                mLastArc = Math.atan2(event.getY() - centerY, event.getX() - centerX);

                break;
            case MotionEvent.ACTION_UP:
                mIsMove = false;
                long upTime = System.currentTimeMillis();//根据时间和触摸距离判断点击还是滑动
                if (upTime - mDownTime < 250 && calSlideLength(mDownPoint, event.getX(), event.getY()) < dip2px(mContext, 1)) {
                    double arc = Math.atan2(event.getY() - centerY, event.getX() - centerX);
                    float angle = (float) (arc * 180 / Math.PI);
                    if (angle < 0) {
                        mIndex = (int) ((angle - 7.5) / 15) + 6;
                    } else {
                        mIndex = (int) ((angle + 7.5) / 15) + 6;
                    }

                    mColorIndex = (mColorIndex + 24 + mIndex) % 24;


                    long mAnimAllTime = 1800;
                    long mAnimTime;
                    if (mIndex < 0) {
                        mAnimTime = mAnimAllTime * (-mIndex) / 24;

                    } else if (mIndex < 12) {
                        mAnimTime = mAnimAllTime * (mIndex) / 24;

                    } else {
                        mIndex = mIndex - 24;
                        mAnimTime = mAnimAllTime * (-mIndex) / 24;
                    }

                    int roundColor = mColorArray[mColorIndex];
                    if (roundColor != mChooseColor) {
                        mChooseColor = roundColor;
                    }
                    mIsAnim = true;
                    mAngleDiff = mIndex * 15f;
                    startAnim(1f, true, mAnimTime);
                } else {
                    int num = Math.round(mStartAngle / 15);
                    mAngleDiff = mStartAngle - num * 15;
                    mIsAnim = true;
                    startAnim(1f, true, 200);
                }

                break;
            default:
                break;
        }
        return true;

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRing(canvas);
    }


    /**
     * 绘制圆环
     */

    private void drawRing(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        if (mIsAnim) {

            float startAngle;
            startAngle = mStartAngle - mProgress * mAngleDiff;

            if (startAngle > 180) {
                startAngle = startAngle - 360;
            }
            if (startAngle < -180) {
                startAngle = startAngle + 360;
            }

            for (int value : mColorArray) {
                mPaint.setColor(value);
                canvas.drawArc(mArcElements, startAngle - 97.5f, 15, false, mPaint);
                startAngle += 15;
            }

        } else {
            if (!mIsMove) {
                mStartAngle = mStartAngle - mProgress * mAngleDiff;
                if (mStartAngle > 180) {
                    mStartAngle = mStartAngle - 360;
                }
                if (mStartAngle < -180) {
                    mStartAngle = mStartAngle + 360;
                }
                mAngleDiff = 0;
            }

            float startAngle = mStartAngle;

            for (int value : mColorArray) {
                mPaint.setColor(value);
                canvas.drawArc(mArcElements, startAngle - 97.5f, 15, false, mPaint);
                startAngle += 15;
            }

            mInnerPaint.setColor(mChooseColor);
//            if (!mIsMove && mOnChooseColorListener != null) {
//                mOnChooseColorListener.onChooseColor(mChooseColor);
//            }
        }
        drawRound(canvas);
        drawTriangle(canvas);

    }

    public int getChooseColor() {
        return mChooseColor;
    }


    /**
     * 绘制圆
     */
    private void drawRound(Canvas canvas) {
        mInnerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mArcElements.centerX(), mArcElements.centerY(), mRoundRadius, mInnerPaint);
    }

    private void drawTriangle(Canvas canvas) {
        mInnerPaint.setStyle(Paint.Style.FILL);
        //实例化路径
        Path path = new Path();
        path.moveTo(centerX - mRoundRadius / 2, centerY - mRoundRadius / 2);
        path.lineTo(centerX + mRoundRadius / 2, centerY - mRoundRadius / 2);
        path.lineTo(centerX, centerY - mRoundRadius / 2 - (float) (mRoundRadius * Math.sqrt(3) / 2));
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, mInnerPaint);
    }

    /**
     * @return 获取颜色数组
     */
    private int[] initColorArray(Resources resources) {
        int[] mColorArray = new int[24];
        mColorArray[0] = resources.getColor(R.color.color_ffa6ff00);
        mColorArray[1] = resources.getColor(R.color.color_ff36ff00);
        mColorArray[2] = resources.getColor(R.color.color_ff00ff08);
        mColorArray[3] = resources.getColor(R.color.color_ff00ff42);
        mColorArray[4] = resources.getColor(R.color.color_ff00ff91);
        mColorArray[5] = resources.getColor(R.color.color_ff00ffd2);
        mColorArray[6] = resources.getColor(R.color.color_ff00e0ff);
        mColorArray[7] = resources.getColor(R.color.color_ff0095ff);
        mColorArray[8] = resources.getColor(R.color.color_ff0062ff);
        mColorArray[9] = resources.getColor(R.color.color_ff001fff);
        mColorArray[10] = resources.getColor(R.color.color_ff2f00ff);
        mColorArray[11] = resources.getColor(R.color.color_ff7300ff);
        mColorArray[12] = resources.getColor(R.color.color_ffb200ff);
        mColorArray[13] = resources.getColor(R.color.color_fff200ff);
        mColorArray[14] = resources.getColor(R.color.color_ffff0089);
        mColorArray[15] = resources.getColor(R.color.color_ffff0001);
        mColorArray[16] = resources.getColor(R.color.color_ffff1900);
        mColorArray[17] = resources.getColor(R.color.color_ffff4200);
        mColorArray[18] = resources.getColor(R.color.color_ffff6f00);
        mColorArray[19] = resources.getColor(R.color.color_ffff9300);
        mColorArray[20] = resources.getColor(R.color.color_ffffb500);
        mColorArray[21] = resources.getColor(R.color.color_ffffd300);
        mColorArray[22] = resources.getColor(R.color.color_ffffe500);
        mColorArray[23] = resources.getColor(R.color.color_ffffff00);
        return mColorArray;
    }

    /**
     * @return 是否在圆环上
     */
    private boolean isAtRing(float x, float y) {
        float maxRadius = mArcElements.width() / 2 + mRingWidth / 2 + mRingWidth / 2;
        float minRadius = mArcElements.width() / 2 - mRingWidth / 2 - mRingWidth / 2;
        double radius = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return radius >= minRadius && radius <= maxRadius;
    }

    public void setOnChooseColorListener(OnChooseColorListener onChooseColorListener) {
        this.mOnChooseColorListener = onChooseColorListener;
    }

    private OnChooseColorListener mOnChooseColorListener;

    public interface OnChooseColorListener {
        void onChooseColor(int color);
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
        progressAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnim = false;
                invalidate();
                if (mOnChooseColorListener != null) {
                    mOnChooseColorListener.onChooseColor(mChooseColor);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * @return 距离
     */
    private double calSlideLength(PointF point1, float x, float y) {
        return Math.sqrt(Math.pow(point1.x - x, 2) + Math.pow(point1.y - y, 2));
    }

    /**
     * @param context
     * @param dpValue
     * @return dp转px
     */
    public int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }
}