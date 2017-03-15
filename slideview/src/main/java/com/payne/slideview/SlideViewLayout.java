package com.payne.slideview;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SlideViewLayout extends ViewGroup {
    private ViewDragHelper mDragHelper;

    private ImageView mSlideView;
    private SlideColorView mBackGround;
    private Point mAutoBackOriginPos = new Point();
    private boolean mSlideRightFirstSuccess = true;

    public SlideViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBackGround = new SlideColorView(context);
        mSlideView = new ImageView(context);
        ViewGroup.LayoutParams backGroundParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBackGround.setLayoutParams(backGroundParams);
        addView(mBackGround);


        ViewGroup.LayoutParams slideViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSlideView.setLayoutParams(slideViewParams);
        mSlideView.setImageResource(R.mipmap.slide);
        mSlideView.setAdjustViewBounds(true);
        addView(mSlideView);

        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mSlideView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return 0;
            }


            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == mSlideView) {
                    mDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView == mSlideView) {
                    mBackGround.setProgress(left);

                    if (left > getMeasuredWidth() - mSlideView.getMeasuredWidth() &&
                            mSlideRightFirstSuccess && mOnSlideListener != null) {
                        mOnSlideListener.onSlideSuccess();
                        mSlideRightFirstSuccess = false;
                    }
                    if (left == 0) {
                        mSlideRightFirstSuccess = true;
                    }
                }
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAutoBackOriginPos.x = mSlideView.getLeft();
        mAutoBackOriginPos.y = mSlideView.getTop();

        mBackGround.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mSlideView.layout(0, 0, mSlideView.getMeasuredWidth(), mSlideView.getMeasuredHeight());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }

    private OnSlideListener mOnSlideListener;

    public interface OnSlideListener {
        void onSlideSuccess();
    }
}