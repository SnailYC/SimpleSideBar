package com.snailyc;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.snailyc.side.AbstractSideBar;
import com.snailyc.side.R;
import com.snailyc.utils.ColorUtil;

public class MyAlphabetBar2 extends AbstractSideBar {
    //region 构造方法
    public MyAlphabetBar2(Context context) {
        super(context);
        init(context, null);
    }

    public MyAlphabetBar2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyAlphabetBar2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyAlphabetBar2(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    //endregion

    private static final float REFER_RADIUS = 200F;

    /**
     * 参照圆的圆心X坐标相对于基准线的偏移
     * mReferCircleX代表处于正常状态下的圆心坐标，偏移量圆的半径，此时基准线刚好是圆的切线
     */
    private float mReferCircleX=   REFER_RADIUS;
    /**
     * 参照圆的圆心X坐标相对于基准线的偏移，
     * mReferCircleXHighlight 代表触摸状态下动画结束的圆心坐标，偏移量为 圆的半径/2，此时圆突出于基准线的部分为文字的路径
     */
    private float mReferCircleXHighlight= (REFER_RADIUS/2);

    private PointF mCircleCenter = new PointF();

    private void init(Context context, @Nullable AttributeSet attrs) {
        resolveXmlAttributes(context, attrs);
    }

    private int mTextColor = 0x66ffffff;
    private int mHighlightColor = 0xffffffff;
    private float mTextSize = 40;
    private float mHighlightSize = 80;

    private void resolveXmlAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlphabetBar);
        try {

            mTextColor = typedArray.getColor(R.styleable.AlphabetBar_textColor, mTextColor);
            mHighlightColor = typedArray.getColor(R.styleable.AlphabetBar_highlightColor, mHighlightColor);
            mTextSize = typedArray.getDimension(R.styleable.AlphabetBar_textSize, mTextSize);
            mHighlightSize = typedArray.getDimension(R.styleable.AlphabetBar_highlightSize, mHighlightSize);

        } finally {
            typedArray.recycle();
        }

    }


    @Override
    protected void onSectionsChangedInternal() {
        setCircleCenter(mReferCircleX,mCircleCenter.y);
    }

    @Override
    protected int getTextColorFromPosition(int position) {
        float offsetXScale = getOffsetXScaleFromPosition(position);
        if (isHighlight(position)) {
            if (offsetXScale > 0.1) {
                return ColorUtil.getMiddleColor(0x00000000, mHighlightColor, offsetXScale);
            }
            return mHighlightColor;
        }
        return mTextColor;
    }



    @Override
    protected float getTextSizeFromPosition(int position) {
        float offsetXScale = getOffsetXScaleFromPosition(position);
        return mTextSize + (mHighlightSize - mTextSize) * (position == getCurrentSectionPosition() ? offsetXScale : 0);
    }

    @Override
    protected float getTextXFromPosition(int position) {
        int offsetX = getOffsetXFromPosition(position);

        return getWidth() - getPaddingRight() - mTextSize / 2 - offsetX;
    }

    private float getOffsetXScaleFromPosition(int position) {
        int offsetX = getOffsetXFromPosition(position);
        float offsetXScale;
        if (offsetX == 0) {
            offsetXScale = 0;
        } else {
            offsetXScale = offsetX / Math.abs(REFER_RADIUS-mReferCircleXHighlight);
        }
        return offsetXScale;

    }
    private int getOffsetXFromPosition(int position) {
        int offsetX;
        final float verticalPosition = mVerticalOffsets[position];
        if (verticalPosition <= mYIntersect[0]) {
            offsetX = 0;
        } else if (verticalPosition >= mYIntersect[1]) {
            offsetX = 0;
        } else {
            final int arcHeight = (int) (mYIntersect[1] - mYIntersect[0]);
            final int distanceInsideArcHeight = (int) (verticalPosition - mYIntersect[0]);
            final int b = Math.abs(arcHeight / 2 - distanceInsideArcHeight);

            final int sideALength = (int) Math.sqrt((REFER_RADIUS * REFER_RADIUS) - (b * b));
            offsetX= (int) (sideALength - mCircleCenter.x);
        }
        return offsetX;
    }


    @Override
    protected void drawTextBackgroundFromPosition(Canvas canvas, float textCenterX, float textCenterY, int position) { }

    @Override
    protected void onStartScrollBar(MotionEvent ev) {
        setCircleCenter(mCircleCenter.x,   ev.getY());
        startBarAnim(mReferCircleXHighlight);
    }


    @Override
    protected void onScrollBar(MotionEvent ev) {

        setCircleCenter(mCircleCenter.x,   ev.getY());
        calculateDrawableElements();
    }

    @Override
    protected void onCancelScrollBar(MotionEvent ev) {
        startBarAnim(mReferCircleX);
    }


    private static final long ANIM_DURATION = 200L;
    private ValueAnimator mAnimator =ValueAnimator.ofFloat(0f, 1f);



    private void startBarAnim(float circleX) {
        cancelBarAnim();
        mAnimator.setFloatValues(mCircleCenter.x, circleX);
        mAnimator.setDuration((long) (ANIM_DURATION * Math.abs(mCircleCenter.x - circleX)/Math.abs(mReferCircleX -mReferCircleXHighlight)));
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float centerX = (float) animation.getAnimatedValue();
                setCircleCenter(centerX, mCircleCenter.y);
                calculateDrawableElements();
                invalidate();
            }
        });
        mAnimator.start();


    }
    private void cancelBarAnim() {
        mAnimator.removeAllUpdateListeners();
        mAnimator.removeAllListeners();
        mAnimator.cancel();
    }

    private float[] mYIntersect = new float[2];


    private void calculateDrawableElements() {
        setYIntersect( mCircleCenter, REFER_RADIUS, mYIntersect);
    }


    /**
     * 基准线与波浪的圆应该有两个交点（没有波浪的时候应该是少于两个）
     *
     * @param withCircle     圆心
     * @param andRadius      圆的半径
     * @param intoFloatArray 用于存放两个相交点的Y坐标
     */
    private void setYIntersect(  PointF withCircle, float andRadius, float[] intoFloatArray) {
        if (intoFloatArray.length < 2) {
            throw new IllegalArgumentException("必须提供长度为 2 的数组来保存返回值");
        }

        final float horizontalDistance = Math.abs(withCircle.x  );


        if (andRadius-horizontalDistance<= 0.1f) {
            intoFloatArray[0] = withCircle.y;
            intoFloatArray[1] = withCircle.y;
            return;
        }

        final float b = (float) Math.sqrt((andRadius * andRadius) - (horizontalDistance * horizontalDistance));
        intoFloatArray[0] = withCircle.y - b;
        intoFloatArray[1] = withCircle.y + b;
    }

    private void setCircleCenter(float x, float y) {
        mCircleCenter.x = x;
        mCircleCenter.y = y;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelBarAnim();
    }

}
