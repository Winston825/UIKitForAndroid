package com.winston.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * Created by ruantihong on 2/29/16.
 */
public class CursorSeekBar extends View {

    private static final String TAG = "CursorSeekBar";

    private static final int DEFAULT_DURATION = 100;

    private enum DIRECTION {
        LEFT, RIGHT
    }

    private int mDuration;

    private Scroller mScroller;

    private Drawable mCursorBG;

    private int[] mPressedEnableState = new int[]{
            android.R.attr.state_pressed, android.R.attr.state_enabled
    };
    private int[] mUnPresseEanabledState = new int[] {
            -android.R.attr.state_pressed, android.R.attr.state_enabled };
    /**
     * Colors of text and seekbar in different states.
     */
    private int mTextColorNormal;
    private int mTextColorSelected;
    private int mSeekbarColorNormal;
    private int mSeekbarColorSelected;

    /**
     * Height of seekbar
     */
    private int mSeekbarHeight;

    /**
     * Size of text mark.
     */
    private int mTextSize;

    /**
     * Space between the text and the seekbar
     */
    private int mMarginBetween;

    /**
     * Length of every part. As we divide some parts according to marks.
     */
    private int mPartLength;

    /**
     * Contents of text mark.
     */
    private CharSequence[] mTextArray;

    private float[] mTextWidthArray;

    private Rect mPaddingRect;
    private Rect mCursorRect;

    private RectF mSeekbarRect;
    private RectF mSeekbarRectSelected;

    private float mCursorIndex = 0;
    private int mCursorNextIndex = 0;

    private Paint mPaint;

    private int mPointerLastX;

    private int mPointerID = -1;

    private boolean mHited;

    private int mBoundary;

    private OnCursorChangeListener mListener;

    private Rect[] mClickRectArray;
    private int mClickIndex = -1;

    private int mClickDownLastX = -1;
    private int mClickDownLastY = -1;

    private int mItemCount;

    public CursorSeekBar(Context context) {
        this(context, null, 0);
    }

    public CursorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CursorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyConfig(context, attrs);

        if (mPaddingRect == null) {
            mPaddingRect = new Rect();
        }

        mPaddingRect.left = getPaddingLeft();
        mPaddingRect.top = getPaddingTop();
        mPaddingRect.right = getPaddingRight();
        mPaddingRect.bottom = getPaddingBottom();

        mCursorRect = new Rect();

        mSeekbarRect = new RectF();
        mSeekbarRectSelected = new RectF();

        if (mTextArray != null) {
            mTextWidthArray = new float[mTextArray.length];
            mClickRectArray = new Rect[mTextArray.length];
        }
        mScroller = new Scroller(context, new DecelerateInterpolator());

        initPaint();
        initTextWidthArray();

        setWillNotDraw(false);
        setFocusable(true);
        setClickable(true);

    }

    private void initTextWidthArray() {
        if (mTextArray != null && mTextArray.length > 0) {
            final int length = mTextArray.length;
            for (int i = 0; i < length; i++) {
                mTextWidthArray[i] = mPaint.measureText(mTextArray[i].toString());
            }
        }
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);
    }

    private void applyConfig(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CursorSeekBar);

        mDuration = typedArray.getInteger(R.styleable.CursorSeekBar_autoMoveDuration,
                DEFAULT_DURATION);

        mCursorBG = typedArray.getDrawable(R.styleable.CursorSeekBar_cursorBackground);

        mTextColorNormal = typedArray.getColor(R.styleable.CursorSeekBar_textColorNormal,
                Color.BLACK);
        mTextColorSelected = typedArray.getColor(R.styleable.CursorSeekBar_textColorSelected,
                Color.rgb(242, 79, 115));
        mSeekbarColorNormal = typedArray.getColor(R.styleable.CursorSeekBar_seekbarColorNormal,
                Color.rgb(218, 215, 215));
        mSeekbarColorSelected = typedArray.getColor(R.styleable.CursorSeekBar_seekbarColorSelected,
                Color.rgb(242, 79, 115));

        mSeekbarHeight = (int) typedArray.getDimension(R.styleable.CursorSeekBar_seekbarHeight, 10);
        mTextSize = (int) typedArray.getDimension(R.styleable.CursorSeekBar_textSize, 15);
        mMarginBetween = (int) typedArray.getDimension(R.styleable.CursorSeekBar_spaceBetween, 15);

        mTextArray = typedArray.getTextArray(R.styleable.CursorSeekBar_markTextArray);

        if (mTextArray != null && mTextArray.length > 0) {
            //TODO
            mCursorIndex = 0;
            mItemCount = mTextArray.length;
        }

        typedArray.recycle();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (mPaddingRect == null) {
            mPaddingRect = new Rect();
        }
        mPaddingRect.left = left;
        mPaddingRect.top = top;
        mPaddingRect.right = right;
        mPaddingRect.bottom = bottom;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int pointerH = mCursorBG.getIntrinsicHeight();

        final int maxofCursorAndSeekbar = Math.max(mSeekbarHeight, pointerH);

        int heightNeeded = maxofCursorAndSeekbar + mMarginBetween + mTextSize
                + mPaddingRect.top + mPaddingRect.bottom;

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                Math.max(heightSize, heightNeeded), MeasureSpec.EXACTLY);

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        mSeekbarRect.left = mPaddingRect.left + mCursorBG.getIntrinsicWidth() / 2;
        mSeekbarRect.right = widthSize - mPaddingRect.right - mCursorBG.getIntrinsicWidth() / 2;
        mSeekbarRect.top = mPaddingRect.top + mTextSize + mMarginBetween;
        mSeekbarRect.bottom = mSeekbarRect.top + mSeekbarHeight;

        mSeekbarRectSelected.top = mSeekbarRect.top;
        mSeekbarRectSelected.bottom = mSeekbarRect.bottom;

        mPartLength = ((int) (mSeekbarRect.right - mSeekbarRect.left)) / (mTextArray.length - 1);

        mBoundary = (int) (mSeekbarRect.right + mCursorBG.getIntrinsicWidth() / 2);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int length = mTextArray.length;
        mPaint.setTextSize(mTextSize);

        for (int i = 0; i < length; i++) {
            //TODO
            if (i == mCursorIndex || i == mItemCount) {
                mPaint.setColor(mTextColorSelected);
            } else {
                mPaint.setColor(mTextColorNormal);
            }

            final String text2draw = mTextArray[i].toString();
            final float textWidth = mTextWidthArray[i];

            float textDrawLeft = 0;
            // The last text mark's draw location should be adjust
            if (i == length - 1) {
                textDrawLeft = mSeekbarRect.right + (mCursorBG.getIntrinsicWidth() / 2) - textWidth;
            } else {
                textDrawLeft = mSeekbarRect.left + i * mPartLength - textWidth / 2;
            }

            canvas.drawText(text2draw, textDrawLeft, mPaddingRect.top + mTextSize, mPaint);

            Rect rect = mClickRectArray[i];
            if (rect == null) {
                rect = new Rect();
                rect.top = mPaddingRect.top;
                rect.bottom = rect.top + mTextSize + mMarginBetween + mSeekbarHeight;
                rect.left = (int) textDrawLeft;
                rect.right = (int) (rect.left + textWidth);

                mClickRectArray[i] = rect;
            }
        }

        /** Draw seekbar **/
        final float radius = (float) mSeekbarHeight / 2;
        mSeekbarRectSelected.left = mSeekbarRect.left + mPartLength * mCursorIndex;
        mSeekbarRectSelected.right = mSeekbarRect.left + mPartLength * mCursorIndex;

        //If whole of seekbar is selected, just draw seekbar with selected color
        if (mCursorIndex == 0) {
            mPaint.setColor(mSeekbarColorSelected);
            canvas.drawRoundRect(mSeekbarRect, radius, radius, mPaint);
        } else {
            //Draw background first
            mPaint.setColor(mSeekbarColorSelected);
            canvas.drawRoundRect(mSeekbarRect, radius, radius, mPaint);

            //Draw selected part
            mPaint.setColor(mSeekbarColorSelected);

            // Can draw rounded rectangle, but original rectangle is enough.
            // Because edges of selected part will be covered by cursors.
            canvas.drawRect(mSeekbarRectSelected, mPaint);
        }

        /*** Draw cursors ***/
        final int width = mCursorBG.getIntrinsicWidth();
        final int height = mCursorBG.getIntrinsicHeight();
        final int left = (int) (mSeekbarRectSelected.left - (float) width / 2);
        final int top = (int) ((mSeekbarRect.top + mSeekbarHeight / 2) - (height / 2));

        mCursorRect.left = left;
        mCursorRect.top = top;
        mCursorRect.right = left + width;
        mCursorRect.bottom = top + height;
        mCursorBG.setBounds(mCursorRect);
        mCursorBG.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        // For multiple touch
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                handleTouchDown(event);

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                handleTouchDown(event);

                break;
            case MotionEvent.ACTION_MOVE:

                handleTouchMove(event);

                break;
            case MotionEvent.ACTION_POINTER_UP:

                handleTouchUp(event);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                handleTouchUp(event);
                mClickIndex = -1;
                mClickDownLastX = -1;
                mClickDownLastY = -1;

                break;
        }

        return super.onTouchEvent(event);
    }

    private void handleTouchDown(MotionEvent event) {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int downX = (int) event.getX(actionIndex);
        final int downY = (int) event.getY(actionIndex);

        if (mCursorRect.contains(downX, downY)) {
            if (mHited) {
                return;
            }

            // If hit, change state of drawable, and record id of touch pointer.
            mPointerLastX = downX;
            mCursorBG.setState(mPressedEnableState);
            mPointerID = event.getPointerId(actionIndex);
            mHited = true;
            invalidate();
        }else {
            // If touch x-y not be contained in cursor,
            // then we check if it in click areas
            final int clickBoundaryTop = mClickRectArray[0].top;
            final int clickBoundaryBottom = mClickRectArray[0].bottom;
            mClickDownLastX = downX;
            mClickDownLastY = downY;

            // Step one : if in boundary of total Y.
            if (downY < clickBoundaryTop || downY > clickBoundaryBottom) {
                mClickIndex = -1;
                return;
            }

            // Step two: find nearest mark in x-axis
            final int partIndex = (int) ((downX - mSeekbarRect.left) / mPartLength);
            final int partDelta = (int) ((downX - mSeekbarRect.left) % mPartLength);
            if (partDelta < mPartLength / 2) {
                mClickIndex = partIndex;
            } else if (partDelta > mPartLength / 2) {
                mClickIndex = partIndex + 1;
            }

            if (mClickIndex == mCursorIndex || mCursorIndex == mItemCount) {
                mClickIndex = -1;
                return;
            }

            // Step three: check contain
            if (!mClickRectArray[mClickIndex].contains(downX, downY)) {
                mClickIndex = -1;
            }
        }
    }

    private void handleTouchUp(MotionEvent event) {
        final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int actionID = event.getPointerId(actionIndex);

        if (actionID == mPointerID) {
            if (!mHited) {
                return;
            }

            // If cursor between in tow mark locations, it should be located on
            // the lower or higher one.

            // step 1:Calculate the offset with lower mark.
            final int lower = (int) Math.floor(mCursorIndex);
            final int higher = (int) Math.ceil(mCursorIndex);

            final float offset = mCursorIndex - lower;
            if (offset != 0) {

                // step 2:Decide which mark will go to.
                if (offset < 0.5f) {
                    // If left cursor want to be located on lower mark, go ahead
                    // guys.
                    // Because right cursor will never appear lower than the
                    // left one.
                    mCursorNextIndex = lower;
                } else if (offset > 0.5f) {
                    mCursorNextIndex = higher;
                    // If left cursor want to be located on higher mark,
                    // situation becomes a little complicated.
                    // We should check that whether distance between left and
                    // right cursor is less than 1, and next index of left
                    // cursor is difference with current
                    // of right one.
                    if (Math.abs(mCursorIndex - mItemCount) <= 1
                            && mCursorNextIndex == mItemCount) {
                        // Left can not go to the higher, just to the lower one.
                        mCursorNextIndex = lower;
                    }
                }

                // step 3: Move to.
                if (!mScroller.computeScrollOffset()) {
                    final int fromX = (int) (mCursorIndex * mPartLength);

                    mScroller.startScroll(fromX, 0, mCursorNextIndex
                            * mPartLength - fromX, 0, mDuration);

                    triggleCallback(true, mCursorNextIndex);
                }
            }

            // Reset values of parameters
            mPointerLastX = 0;
            mCursorBG.setState(mUnPresseEanabledState);
            mPointerID = -1;
            mHited = false;

            invalidate();
        }  else {
            final int pointerIndex = event.findPointerIndex(actionID);
            final int upX = (int) event.getX(pointerIndex);
            final int upY = (int) event.getY(pointerIndex);

            if (mClickIndex != -1
                    && mClickRectArray[mClickIndex].contains(upX, upY)) {
                // Find nearest cursor
                final float distance2LeftCursor = Math.abs(mCursorIndex
                        - mClickIndex);
                final float distance2Right = Math.abs(mCursorIndex
                        - mClickIndex);

                final boolean moveLeft = distance2LeftCursor <= distance2Right;
                int fromX = 0;
                if (moveLeft) {
                    if (!mScroller.computeScrollOffset()) {
                        mCursorNextIndex = mClickIndex;
                        fromX = (int) (mCursorIndex * mPartLength);
                        mScroller.startScroll(fromX, 0,
                                mCursorNextIndex * mPartLength - fromX, 0,
                                mDuration);

                        triggleCallback(true, mCursorNextIndex);
                        invalidate();
                    }
                }
            }
        }
    }

    private void triggleCallback(boolean isLeft, int location) {
        if (mListener == null) {
            return;
        }

        if (isLeft) {
            mListener.onCursorChanged(location,
                    mTextArray[location].toString());
        }
    }
    private void handleTouchMove(MotionEvent event) {
        if (mClickIndex != -1) {
            final int actionIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            final int x = (int) event.getX(actionIndex);
            final int y = (int) event.getY(actionIndex);

            if (!mClickRectArray[mClickIndex].contains(x, y)) {
                mClickIndex = -1;
            }
        }

        if (mHited && mPointerID != -1) {

            final int index = event.findPointerIndex(mPointerID);
            final float x = event.getX(index);

            float deltaX = x - mPointerLastX;
            mPointerLastX = (int) x;

            DIRECTION direction = (deltaX < 0 ? DIRECTION.LEFT
                    : DIRECTION.RIGHT);

            if (direction == DIRECTION.LEFT && mCursorIndex == 0) {
                return;
            }

            // Check whether cursor will move out of boundary
            if (mCursorRect.left + deltaX < mPaddingRect.left) {
                mCursorIndex = 0;
                invalidate();
                return;
            }

            // Check whether left and right cursor will collision.

            // After some calculate, if deltaX is still be zero, do quick
            // return.
            if (deltaX == 0) {
                return;
            }

            // Calculate the movement.
            final float moveX = deltaX / mPartLength;
            mCursorIndex += moveX;

            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int deltaX = mScroller.getCurrX();

            mCursorIndex = (float) deltaX / mPartLength;

            invalidate();
        }
    }
    public void setSelection(int partIndex) {
        if (partIndex > mTextArray.length - 1|| partIndex < 0) {
            throw new IllegalArgumentException(
                    "Index should from 0 to size of text array minus 2!");
        }

        if (partIndex != mCursorIndex) {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mCursorIndex = partIndex;
            mCursorNextIndex = partIndex;
            final int leftFromX = (int) (mCursorIndex * mPartLength);
            mScroller.startScroll(leftFromX, 0, mCursorNextIndex
                    * mPartLength - leftFromX, 0, mDuration);
            triggleCallback(true, mCursorNextIndex);
            invalidate();
        }
    }


    public void setCursorBackground(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException(
                    "Do you want to make left cursor invisible?");
        }

        mCursorBG = drawable;

        requestLayout();
        invalidate();
    }

    public void setCursorBackground(int resID) {
        if (resID < 0) {
            throw new IllegalArgumentException(
                    "Do you want to make left cursor invisible?");
        }

        mCursorBG = getResources().getDrawable(resID);

        requestLayout();
        invalidate();
    }

    public void setTextMarkColorNormal(int color) {
        if (color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make text mark invisible?");
        }

        mTextColorNormal = color;

        invalidate();
    }

    public void setTextMarkColorSelected(int color) {
        if (color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make text mark invisible?");
        }

        mTextColorSelected = color;

        invalidate();
    }

    public void setSeekbarColorNormal(int color) {
        if (color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make seekbar invisible?");
        }

        mSeekbarColorNormal = color;

        invalidate();
    }

    public void setSeekbarColorSelected(int color) {
        if (color <= 0 || color == Color.TRANSPARENT) {
            throw new IllegalArgumentException(
                    "Do you want to make seekbar invisible?");
        }

        mSeekbarColorSelected = color;

        invalidate();
    }

    /**
     * In pixels. Users should call this method before view is added to parent.
     *
     * @param height
     */
    public void setSeekbarHeight(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException(
                    "Height of seekbar can not less than 0!");
        }

        mSeekbarHeight = height;
    }

    /**
     * To set space between text mark and seekbar.
     *
     * @param space
     */
    public void setSpaceBetween(int space) {
        if (space < 0) {
            throw new IllegalArgumentException(
                    "Space between text mark and seekbar can not less than 0!");
        }

        mMarginBetween = space;

        requestLayout();
        invalidate();
    }

    /**
     * This method should be called after {@link #setTextMarkSize(int)}, because
     * view will measure size of text mark by paint.
     *
     * @param size
     */
    public void setTextMarks(CharSequence... marks) {
        if (marks == null || marks.length == 0) {
            throw new IllegalArgumentException(
                    "Text array is null, how can i do...");
        }

        mTextArray = marks;
        mCursorIndex = 0;
        mTextWidthArray = new float[marks.length];
        mClickRectArray = new Rect[mTextArray.length];
        initTextWidthArray();

        requestLayout();
        invalidate();
    }

    /**
     * Users should call this method before view is added to parent.
     *
     * @param size
     *            in pixels
     */
    public void setTextMarkSize(int size) {
        if (size < 0) {
            return;
        }

        mTextSize = size;
        mPaint.setTextSize(size);
    }

    public int getCursorIndex() {
        return (int) mCursorIndex;
    }

    public void setCursorIndex(int index){
        mCursorIndex = index;
        triggleCallback(true,index);
        invalidate();
    }

    public void setOnCursorChangeListener(OnCursorChangeListener l) {
        mListener = l;
    }




    public interface OnCursorChangeListener {
        void onCursorChanged(int location, String textMark);
    }
}
