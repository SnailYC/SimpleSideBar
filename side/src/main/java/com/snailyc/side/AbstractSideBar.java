package com.snailyc.side;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class AbstractSideBar extends View {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SectionIndexerAdapter ADAPTER_DEFAULT = new SectionIndexerAdapter() {
        @Override
        public int getSectionCount() {
            return ALPHABET.length();
        }

        @Override
        public String getSectionTitle(int position) {
            return ALPHABET.substring(position, position + 1);
        }

        @Override
        public int getSectionWeight(int position) {
            return 1;
        }
    };
    private static final ScrollerListener LISTENER_DEFAULT = new ScrollerListener() {
        @Override
        public void onScrollPositionChanged(int sectionPosition) {

        }
    };

    private SectionIndexerAdapter mAdapter = ADAPTER_DEFAULT;

    private ScrollerListener mListener = LISTENER_DEFAULT;

    private TextPaint mTextPaint;

    /**
     * 用于测量文本位置的工具 Rect
     */
    private Rect mTextRect;

    /**
     * 标题集合。避免频繁调用适配器，只有在适配器数据源更改的情况下才刷新
     */
    protected String[] mTitleHolder;

    /**
     * 存放各个标题的 Y 坐标
     */
    protected int[] mVerticalOffsets;
    /**
     * 权重和
     */
    protected int mTotalWeight;
    /**
     * 滚动事件的阈值
     */
    private int mTouchSlop;
    /**
     * 当前选择的下标
     */
    private int mChoose = -1;
    /**
     * 高亮开始的元素下标
     */
    private int mFirstHighlightIndex = -1;
    /**
     * 高亮的元素数量
     */
    private int mHighlightRange = 1;
    //region 构造方法
    public AbstractSideBar(Context context) {
        super(context);
        init(context, null);

    }

    public AbstractSideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AbstractSideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AbstractSideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    //endregion

    private void init(Context context, AttributeSet attrs) {

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mTextRect = new Rect();
        mTextPaint = createTextPaint();
    }

    private TextPaint createTextPaint() {
        TextPaint p = new TextPaint();
        p.setAntiAlias(true);
        p.setTextAlign(Paint.Align.CENTER);
        return p;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTitleHolder == null) {
            return;
        }

        for (int i = 0, sectionCount = mTitleHolder.length; i < sectionCount; i++) {

            float textSize = getTextSizeFromPosition(i);
            int textColor = getTextColorFromPosition(i);

            mTextPaint.setTextSize(textSize);
            mTextPaint.setColor(textColor);

            final float textX = getTextXFromPosition(i);

            mTextPaint.getTextBounds(mTitleHolder[i], 0, mTitleHolder[i].length(), mTextRect);
            final float textCenterY = mVerticalOffsets[i] + mTextRect.bottom / 2;

            drawTextBackgroundFromPosition(canvas, textX, textCenterY, i);

            final float textCorrection = ((mTextPaint.descent() + mTextPaint.ascent())) / 2;
            final float textY = mVerticalOffsets[i] - textCorrection;

            canvas.drawText(mTitleHolder[i], 0, mTitleHolder[i].length(), textX,
                    textY, mTextPaint);
        }
    }

    /**
     * 初始化各个字母的初始坐标以及路径。一般在view大小发生改变或者数据源发生改动时刷新
     * 在调用 invalidate（）之前调用。
     */
    protected abstract void onSectionsChangedInternal();

    @ColorInt
    protected abstract int getTextColorFromPosition(int position);

    protected abstract float getTextSizeFromPosition(int position);

    protected abstract float getTextXFromPosition(int position);

    /**
     * 绘制每个标题的背景，在 onDraw 中多次调用
     *
     * @param textCenterX 当前标题的中心 X 坐标
     * @param textCenterY 当前标题的中心 Y 坐标
     * @param position    当前标题在标题列表中的位置
     */
    protected abstract void drawTextBackgroundFromPosition(Canvas canvas, float textCenterX, float textCenterY, int position);

    private boolean isDrag;
    private float mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (ev.getX() < getWidth() - getPaddingRight() - 60) {
                    return false;
                }
                isDrag = false;
                mDownY = y;

                onStartScrollBar(ev);

                final int position = sectionIndexAtYPosition(mDownY);
                if (position >= 0 && position < mTitleHolder.length) {
                    setCurrentSectionIndex(position);

                    notifySectionPositionChanged(position);
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if (!isDrag) {
                    final float yDiff = Math.abs(y - mDownY);
                    if (yDiff > mTouchSlop) {
                        isDrag = true;
                    }
                }
                if (isDrag) {
                    final int sectionPosition = sectionIndexAtYPosition(y);
                    onScrollBar(ev);
                    //如果当前选项更改了并且在集合范围内，通知监听器
                    if (getCurrentSectionPosition() != sectionPosition
                            && sectionPosition >= 0 && sectionPosition < mTitleHolder.length) {
                        setCurrentSectionIndex(sectionPosition);
                        notifySectionPositionChanged(sectionPosition);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDrag = false;
                onCancelScrollBar(ev);
                invalidate();
                return false;
        }
        return true;
    }

    /**
     * 返回-1代表没有
     */
    protected int sectionIndexAtYPosition(float y) {
        if (mVerticalOffsets == null || mVerticalOffsets.length < 1) {
            return -1;
        }
        if (y <= mVerticalOffsets[0]) {
            return 0;
        }

        if (y >= mVerticalOffsets[mVerticalOffsets.length - 1]) {
            return mTitleHolder.length - 1;
        }

        for (int i = 0; i < mVerticalOffsets.length; i++) {
            if (mVerticalOffsets[i] > y) {
                return Math.max(0, i - 1);
            }
        }

        return mTitleHolder.length - 1;
    }

    protected abstract void onStartScrollBar(MotionEvent ev);

    protected abstract void onScrollBar(MotionEvent ev);

    protected abstract void onCancelScrollBar(MotionEvent ev);


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        notifySectionsChangedInternal();
    }

    /**
     * 刷新数据源
     * 在view大小、adapter数据等发生改变的时候需要刷新
     */
    private void notifySectionsChangedInternal() {

        final int sectionCount = mAdapter.getSectionCount();
        if (mTitleHolder == null || sectionCount != mTitleHolder.length) { // 如果侧边索引数量改变了
            mTitleHolder = new String[sectionCount];

            mVerticalOffsets = new int[sectionCount];
        }
        int totalWeight = 0;
        for (int i = 0; i < sectionCount; i++) {
            totalWeight += mAdapter.getSectionWeight(i);
            mTitleHolder[i] = mAdapter.getSectionTitle(i);
        }
        mTotalWeight = totalWeight;

        setVerticalOffsets(mVerticalOffsets);
        onSectionsChangedInternal();
        invalidate();
    }

    /**
     * 获取垂直方向偏移量，也就是 Y 坐标
     *
     * @param intoArray 存放 Y 坐标的数组
     */
    private void setVerticalOffsets(int[] intoArray) {
        if (mTitleHolder == null || intoArray == null) {
            return;
        }
        if (intoArray.length < mTitleHolder.length) {
            throw new IllegalArgumentException("必须提供长度为 mTitleHolder.length,也就是 (" + mTitleHolder.length + ") 的数组来保存返回值");
        }

        final int totalSize = mTotalWeight;
        final float height = getHeight() - getPaddingTop() - getPaddingBottom();
        float offset = getPaddingTop();

        for (int i = 0; i < mTitleHolder.length; i++) {
            final float sectionSize = mAdapter.getSectionWeight(i);
            intoArray[i] = (int) offset;
            offset += (sectionSize / (float) totalSize) * height;
        }
    }


    public void setSectionIndexerAdapter(SectionIndexerAdapter adapter) {
        if (adapter == null) {
            mAdapter = ADAPTER_DEFAULT;
        } else {
            mAdapter = adapter;
        }
        notifySectionsChangedInternal();
    }

    public void setScrollerListener(ScrollerListener listener) {
        if (listener == null) {
            mListener = LISTENER_DEFAULT;
        } else {
            mListener = listener;
        }
    }

    private void notifySectionPositionChanged(int sectionPosition) {
        mListener.onScrollPositionChanged(sectionPosition);
    }


    public int getCurrentSectionPosition() {
        return mChoose;
    }

    protected void setCurrentSectionIndex(int sectionIndex) {
        mChoose = sectionIndex;
    }
    private void setHighlight(int firstHighlightIndex, int range) {
        mFirstHighlightIndex = firstHighlightIndex;
        mHighlightRange = range;
    }

    public boolean isHighlight(int position) {
        return position >= mFirstHighlightIndex && position < (mFirstHighlightIndex + mHighlightRange);
    }
    /**
     * 更改当前选中的位置
     */
    public void showSectionHighlight(int sectionIndex, int range) {
        setHighlight(sectionIndex, range);
        invalidate();
    }


    /**
     * 更新侧边索引信息
     */
    public void notifySectionsChanged() {
        notifySectionsChangedInternal();
    }


}
