package com.snailyc;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.snailyc.side.AbstractSideBar;
import com.snailyc.side.R;
import com.snailyc.utils.ColorUtil;

public class MyAlphabetBar extends AbstractSideBar {
    //region 构造方法
    public MyAlphabetBar(Context context) {
        super(context);
        init(context, null);
    }

    public MyAlphabetBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyAlphabetBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyAlphabetBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    //endregion

    private void init(Context context, @Nullable AttributeSet attrs) {
        resolveXmlAttributes(context, attrs);
        mAnimator = ValueAnimator.ofFloat(0f, 1f);
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(0xff3F51B5);


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

    private int[] mAnimStartHorizontalOffsets;
    private int[] mAnimEndHorizontalOffsets;

    @Override
    protected void onSectionsChangedInternal() {
        int mCount = mTitleHolder.length;
        if (mAnimEndHorizontalOffsets == null || mAnimEndHorizontalOffsets.length != mCount) {
            mAnimEndHorizontalOffsets = new int[mCount];
        }
        if (mAnimStartHorizontalOffsets == null || mAnimStartHorizontalOffsets.length != mCount) {
            mAnimStartHorizontalOffsets = new int[mCount];
        }

        int startY = mVerticalOffsets[0];
        int endY = mVerticalOffsets[mVerticalOffsets.length - 1];

        int arcHeight = endY - startY;
        int mArcRadius = arcHeight * 7;

        int leftOffsetX = 10;


        float distance = (float) Math.sqrt(mArcRadius * mArcRadius - arcHeight * arcHeight / 4);

        for (int i = 0; i < mCount; i++) {

            mAnimStartHorizontalOffsets[i] = 0;

            final float verticalPosition = mVerticalOffsets[i];

            final int distanceInsideArcHeight = (int) (verticalPosition - startY);
            final int b = Math.abs(arcHeight / 2 - distanceInsideArcHeight);

            final int sideALength = (int) Math.sqrt((mArcRadius * mArcRadius) - (b * b));
            mAnimEndHorizontalOffsets[i] = (int) (sideALength - distance) + leftOffsetX;
        }
    }

    @Override
    protected int getTextColorFromPosition(int position) {
        if (isHighlight(position) ) {
            if (offsetXScale > 0.1) {
                return ColorUtil.getMiddleColor(0x00000000, mHighlightColor, offsetXScale);
            }
            return mHighlightColor;
        }
        return mTextColor;
    }


    @Override
    protected float getTextSizeFromPosition(int position) {
        return mTextSize + (mHighlightSize - mTextSize) * (position == getCurrentSectionPosition() ? offsetXScale : 0);
    }

    @Override
    protected float getTextXFromPosition(int position) {
        float offsetX = (mAnimStartHorizontalOffsets[position] + (mAnimEndHorizontalOffsets[position] - mAnimStartHorizontalOffsets[position]) * offsetXScale);
        float textX = getWidth() - getPaddingRight() - mTextSize / 2 - offsetX;
        textX = textX - (position == getCurrentSectionPosition() ? (mHighlightSize-mTextSize ): 0) * offsetXScale;
        return textX;
    }

    private Paint paint;

    @Override
    protected void drawTextBackgroundFromPosition(Canvas canvas, float textCenterX, float textCenterY, int position) {
//        if (position == getCurrentSectionPosition()) {
//            canvas.drawCircle(textCenterX, textCenterY, getTextSizeFromPosition(position)/2, paint);
//        }
    }

    @Override
    protected void onStartScrollBar(MotionEvent ev) {
        startBarAnim(1f);
    }


    @Override
    protected void onScrollBar(MotionEvent ev) {

    }

    @Override
    protected void onCancelScrollBar(MotionEvent ev) {
        startBarAnim(0f);
    }


    private ValueAnimator mAnimator;
    private static final long ANIM_DURATION = 300L;
    private float offsetXScale = 0;

    private void startBarAnim(float target) {
        cancelBarAnim();
        mAnimator.setFloatValues(offsetXScale, target);
        mAnimator.setDuration((long) (ANIM_DURATION * Math.abs(target - offsetXScale)));
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetXScale = (float) animation.getAnimatedValue();
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelBarAnim();
    }

}
