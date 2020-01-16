package com.payne.customview.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.payne.customview.R;

import java.util.ArrayList;

public class LineGraphicView extends View {
    /**
     * 坐标点半径
     */
    private int mCircleRadius;
    /**
     * 坐标线颜色
     */
    private int mAxisColor;
    /**
     * 文字颜色
     */
    private int mTextColor;
    /**
     * 提提大小
     */
    private int mTextSize;
    /**
     * 边框颜色
     */
    private int mStrokeColor;
    /**
     * 表格外边距
     */
    private int mPadding;
    /**
     * 是否绘制边框
     */
    private boolean mShowStroke;
    /**
     * 坐标点颜色
     */
    private int mCircleColor;
    /**
     * 折线颜色
     */
    private int mLineColor;
    /**
     * 最后一个坐标点颜色
     */
    private int mCircleLastColor;
    /**
     * 渐变开始颜色
     */
    private int mGradientStartColor;
    /**
     * 渐变结束颜色
     */
    private int mGradientEndColor;
    private Context mContext;
    private Paint mPaint;
    /**
     * 渐变专用画笔
     */
    private Paint mGradientPaint;
    private Shader mGradientShader;
    /**
     * 血药绘制动画的路径
     */
    private Path mAnimPath;

    private int mCanvasHeight;//view总高度
    private int mCanvasWidth;//view 总宽度
    private int mTableHeight;//表格总高度

    private boolean isMeasure = true;
    /**
     * Y坐标最大值
     */
    private int mYMaxValue;
    /**
     * Y坐标间距值
     */
    private int mAverageValue;
    /**
     * 曲线上总点数
     */
    private Point[] mPoints;
    /**
     * 纵坐标值
     */
    private ArrayList<Double> mAxisY;
    /**
     * 横坐标值
     */
    private ArrayList<String> mAxisX;
    /**
     * 坐标点对应的X的位置，绝对像素位置
     */
    private ArrayList<Integer> mPositionXList = new ArrayList<>();
    /**
     * y轴坐标点间距高度像素
     */
    private int mRowHeight;

    private float mProgress;

    public LineGraphicView(Context context) {
        this(context, null);
    }

    public LineGraphicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LineGraphicView);
        mCircleRadius = (int) array.getDimension(R.styleable.LineGraphicView_lineGraphicCircleRadius, dip2px(10));
        mAxisColor = array.getColor(R.styleable.LineGraphicView_lineGraphicAxisColor, Color.GRAY);
        mTextColor = array.getColor(R.styleable.LineGraphicView_lineGraphicTextColor, Color.BLACK);
        mTextSize = (int) array.getDimension(R.styleable.LineGraphicView_lineGraphicTextSize, dip2px(12));
        mStrokeColor = array.getColor(R.styleable.LineGraphicView_lineGraphicStrokeColor, Color.BLACK);
        mPadding = (int) array.getDimension(R.styleable.LineGraphicView_lineGraphicPadding, dip2px(20));
        mShowStroke = array.getBoolean(R.styleable.LineGraphicView_lineGraphicShowStroke, false);
        mCircleColor = array.getColor(R.styleable.LineGraphicView_lineGraphicCircleColor, Color.BLACK);
        mLineColor = array.getColor(R.styleable.LineGraphicView_lineGraphicLineColor, Color.BLACK);
        mCircleLastColor = array.getColor(R.styleable.LineGraphicView_lineGraphicCircleLastColor, Color.GREEN);
        mGradientStartColor = array.getColor(R.styleable.LineGraphicView_lineGraphicGradientStartColor, Color.YELLOW);
        mGradientEndColor = array.getColor(R.styleable.LineGraphicView_lineGraphicGradientEndColor, Color.RED);
        array.recycle();

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGradientPaint.setAlpha(200);
        mGradientPaint.setStyle(Style.FILL);

        mAnimPath = new Path();
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
     * @param time 动画持续时间
     */
    public void startAnim(long time) {
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, "progress", 0f, 1.0f);
        progressAnimation.setDuration(time);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (isMeasure) {
            this.mCanvasHeight = getHeight();
            this.mCanvasWidth = getWidth();
            mPadding = mTextSize * 2;
            if (mTableHeight == 0) {
                mTableHeight = mCanvasHeight - mPadding * 2;
            }
            isMeasure = false;
        }

        if (mGradientShader == null) {
            mGradientShader = new LinearGradient(0, mCanvasHeight / 4, 0, mTableHeight, new int[]{mGradientStartColor, mGradientEndColor}, null, Shader.TileMode.CLAMP);
            mGradientPaint.setShader(mGradientShader);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAxisX == null || mAxisX.size() == 0 || mAxisY == null || mAxisY.size() == 0) {
            return;
        }
        drawAllXLine(canvas);
        drawAllYLine(canvas);

        mPoints = getPoints();

        drawAnim(canvas);

        drawLine(canvas);
        drawCircle(canvas);
        if (mShowStroke) {
            drawStroke(canvas);
        }
    }

    private void drawAnim(Canvas canvas) {
        mAnimPath.moveTo(mCanvasWidth - mPadding, mTableHeight + mPadding);
        mAnimPath.lineTo(mPadding, mTableHeight + mPadding);

        for (Point point : mPoints) {
            mAnimPath.lineTo(point.x, (mCanvasHeight - mPadding - point.y) * (1 - mProgress) + point.y);
        }

        mAnimPath.close();//把开始的点和最后的点连接在一起，构成一个封闭图形
        canvas.drawPath(mAnimPath, mGradientPaint);
    }

    /**
     * 画四周的边框
     */
    private void drawStroke(Canvas canvas) {
        mPaint.setStyle(Style.STROKE);
        mPaint.setColor(mStrokeColor);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setStyle(Style.FILL);

    }

    /**
     * 画原点
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        mPaint.setStyle(Style.FILL);
        for (int i = 0; i < mPoints.length; i++) {
            if (i == mPoints.length - 1) {
                mPaint.setColor(mCircleLastColor);
                mPaint.setAlpha((int) (255 * mProgress));

                canvas.drawCircle(mPoints[i].x, mPoints[i].y, mCircleRadius, mPaint);
            } else {
                mPaint.setColor(mCircleColor);
                mPaint.setAlpha((int) (255 * mProgress));
                canvas.drawCircle(mPoints[i].x, mPoints[i].y, mCircleRadius, mPaint);
            }
        }
    }

    /**
     * 画所有横向表格，包括X轴
     */
    private void drawAllXLine(Canvas canvas) {
        mPaint.setColor(mAxisColor);
        mPaint.setStrokeWidth(0.5f);
        for (int i = 0; i < mRowHeight + 1; i++) {
            canvas.drawLine(mPadding, mPadding + (mTableHeight / mRowHeight) * i, (mCanvasWidth - mPadding),
                    mPadding + (mTableHeight / mRowHeight) * i, mPaint);
            // Y坐标
            drawText(String.valueOf(mAverageValue * i), mPadding / 2, mTableHeight - (mTableHeight / mRowHeight) * i + mPadding + 6,
                    canvas);
        }
    }

    /**
     * 画所有纵向表格，包括Y轴
     */
    private void drawAllYLine(Canvas canvas) {
        mPaint.setColor(mAxisColor);
        mPaint.setStrokeWidth(0.5f);
        for (int i = 0; i < mAxisY.size(); i++) {
            mPositionXList.add(mPadding + (mCanvasWidth - mPadding - mPadding) / (mAxisY.size() - 1) * i);
            canvas.drawLine(mPadding + (mCanvasWidth - mPadding - mPadding) / (mAxisY.size() - 1) * i, mPadding, mPadding
                    + (mCanvasWidth - mPadding - mPadding) / (mAxisY.size() - 1) * i, mTableHeight + mPadding, mPaint);
            // X坐标
            drawText(mAxisX.get(i), mPadding + (mCanvasWidth - mPadding - mPadding) / (mAxisY.size() - 1) * i, mTableHeight + mPadding * 2,
                    canvas);
        }
    }

    /**
     * 绘制折线
     */
    private void drawLine(Canvas canvas) {
        Point startp;
        Point endp;
        mPaint.setColor(mLineColor);
        mPaint.setAlpha((int) (200 * mProgress));
        for (int i = 0; i < mPoints.length - 1; i++) {
            startp = mPoints[i];
            endp = mPoints[i + 1];
            canvas.drawLine(startp.x, startp.y, endp.x, endp.y, mPaint);
        }
    }

    /**
     * 绘制坐标
     */
    private void drawText(String text, int x, int y, Canvas canvas) {
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        canvas.drawText(text, x, y, mPaint);
    }

    private Point[] getPoints() {
        Point[] points = new Point[mAxisY.size()];
        for (int i = 0; i < mAxisY.size(); i++) {
            int ph = mTableHeight - (int) (mTableHeight * (mAxisY.get(i) / mYMaxValue));
            points[i] = new Point(mPositionXList.get(i), ph + mPadding);
        }
        return points;
    }

    /**
     * 设置表格数据
     *
     * @param yAxisData     Y轴数据点
     * @param xAxisData     X轴值
     * @param yMaxValue     Y轴最大数据
     * @param yAverageValue X轴间隔数值
     */
    public void setData(ArrayList<Double> yAxisData, ArrayList<String> xAxisData, int yMaxValue, int yAverageValue) {
        this.mYMaxValue = yMaxValue;
        this.mAverageValue = yAverageValue;
        this.mPoints = new Point[yAxisData.size()];
        this.mAxisX = xAxisData;
        this.mAxisY = yAxisData;
        this.mRowHeight = yMaxValue / yAverageValue;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return (int) (dpValue * displayMetrics.density + 0.5f);
    }

}