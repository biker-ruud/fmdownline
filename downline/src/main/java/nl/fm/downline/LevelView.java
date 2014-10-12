package nl.fm.downline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import nl.fm.downline.common.LevelRanges;

/**
 * @author Ruud de Jong
 */
public class LevelView extends View {

    private static final float MARGIN_LEFT_PERCENTAGE = 2.0f;
    private static final float MARGIN_RIGHT_PERCENTAGE = 2.0f;
    private static final float MARGIN_TOP_PERCENTAGE = 2.0f;
    private static final float MARGIN_BOTTOM_PERCENTAGE = 6.0f;
    private static final String LOG_TAG = "LevelView";

    private int measuredWidth;
    private int measuredHeight;
    private int level;
    private float levelPoints;

    public LevelView(Context context) {
        super(context);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LevelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLevelInfo(int level, float levelPoints) {
        this.level = level;
        this.levelPoints = levelPoints;
    }

    @Override
    protected final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(LOG_TAG, "onDraw()");
        drawInternal(canvas);
    }

    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.measuredWidth = parentWidth;
        this.measuredHeight = (parentWidth * 3) / 4;
        Log.i(LOG_TAG, "setMeasuredDimension: " + this.measuredWidth + ", " + this.measuredHeight);
        this.setMeasuredDimension(this.measuredWidth, this.measuredHeight);
    }

    private void drawInternal(Canvas canvas) {
        Log.i(LOG_TAG, "drawInternal()");
        canvas.drawLine(getMinX(), getMinY(), getMaxX(), getMinY(), getSolidLinePaint());

        float avaliableWidthPerBar = (getMaxX() - getMinX()) / 7.0f;
        float barWidth = avaliableWidthPerBar * 0.8f;
        float barMargin = avaliableWidthPerBar - barWidth;
        Log.i(LOG_TAG, "avaliableWidthPerBar " + avaliableWidthPerBar);
        Log.i(LOG_TAG, "barWidth " + barWidth);
        Log.i(LOG_TAG, "barMargin " + barMargin);
        Log.i(LOG_TAG, "X " + getMinX() + " - " + getMaxX());
        Log.i(LOG_TAG, "Y " + getMinY() + " - " + getMaxY());
        float relativeMaxHeight = getMinY() - getMaxY();
        float calculatedTextSize = determineTextSize("21 %", barWidth * 0.9f);
        for (int i=0; i<7; i++) {
            int currentBarLevel = (i+1)*3;
            float left = getMinX() + (i*avaliableWidthPerBar) + (barMargin / 2.0f);
            float relativeBarTop = (((float) currentBarLevel) / 21.0f);
            float top = getMinY() - (relativeBarTop * relativeMaxHeight);
            float right = getMinX() + ((i+1)*avaliableWidthPerBar) - (barMargin / 2.0f);
            float bottom = getMinY();
            if (currentBarLevel < level) {
                canvas.drawRect(left, top, right, bottom, getAchievedPaint());
            } else if (level < currentBarLevel) {
                canvas.drawRect(left, top, right, bottom, getGrayPaint());
            } else {
                float levelRatio = determineLevelRatio();
                Log.i(LOG_TAG, "Level completeness ratio: " + levelRatio);
                float barHeight = bottom - top;
                float relativeDivider = levelRatio * barHeight;
                float divider = bottom - relativeDivider;
                canvas.drawRect(left, divider, right, bottom, getAchievedPaint());
                canvas.drawRect(left, top, right, divider, getGrayPaint());
            }

            String barText = currentBarLevel + " %";
            Paint textPaint = getTextPaint(calculatedTextSize);
            Rect bounds = new Rect();
            textPaint.getTextBounds(barText, 0, barText.length(), bounds);
            float leftTextMargin = 0.0f;
            if (barWidth - bounds.width() > 0.0f) {
                leftTextMargin = (barWidth - bounds.width()) / 2.0f;
            }
            canvas.drawText(barText, left + leftTextMargin, bottom, textPaint);
        }
    }

    private float getMinX() {
        return getLeftMargin();
    }

    private float getMaxX() {
        return this.measuredWidth - (getRightMargin());
    }

    private float getMinY() {
        return this.measuredHeight - getBottomMargin();
    }

    private float getMaxY() {
        return getTopMargin();
    }

    private float getLeftMargin() {
        return ((float)this.measuredWidth) * MARGIN_LEFT_PERCENTAGE / 100.0f;
    }

    private float getRightMargin() {
        return ((float)this.measuredWidth) * MARGIN_RIGHT_PERCENTAGE / 100.0f;
    }

    private float getTopMargin() {
        return ((float)this.measuredHeight) * MARGIN_TOP_PERCENTAGE / 100.0f;
    }

    private float getBottomMargin() {
        return ((float)this.measuredHeight) * MARGIN_BOTTOM_PERCENTAGE / 100.0f;
    }

    private Paint getSolidLinePaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        return paint;
    }

    private Paint getGrayPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        return paint;
    }

    private Paint getAchievedPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.rgb(192, 192, 0));
        return paint;
    }

    private Paint getTextPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        return paint;
    }

    private Paint getTextPaint(float textSize) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        return paint;
    }

    private float determineLevelRatio() {
        LevelRanges.Range levelRange = LevelRanges.getRange(level);
        float levelSize = levelRange.getMax() -levelRange.getMin();
        return (levelPoints - levelRange.getMin()) / levelSize;
    }

    private float determineTextSize(String maxText, float maxWidth) {
        Rect bounds = new Rect();
        float increment = 1.0f;
        Paint textPaint = getTextPaint();
        float textSize = 1.0f;
        textPaint.setTextSize(textSize);
        textPaint.getTextBounds(maxText, 0, maxText.length(), bounds);
        while (bounds.width() < maxWidth) {
            textSize += increment;
            textPaint.setTextSize(textSize);
            textPaint.getTextBounds(maxText, 0, maxText.length(), bounds);
        }
        return textSize - increment;
    }

}
