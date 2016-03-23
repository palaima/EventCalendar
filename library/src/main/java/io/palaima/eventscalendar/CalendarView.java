package io.palaima.eventscalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.List;

import io.palaima.eventscalendar.data.CalendarEvent;
import io.palaima.eventscalendar.highlight.Highlight;
import io.palaima.eventscalendar.listener.BarLineChartTouchListener;
import io.palaima.eventscalendar.listener.OnChartGestureListener;
import io.palaima.eventscalendar.renderer.CategoryRenderer;
import io.palaima.eventscalendar.renderer.CircleRenderer;
import io.palaima.eventscalendar.renderer.DefaultCategoryRenderer;
import io.palaima.eventscalendar.renderer.DefaultGridRenderer;
import io.palaima.eventscalendar.renderer.DefaultTimeScaleRenderer;
import io.palaima.eventscalendar.renderer.GridRenderer;
import io.palaima.eventscalendar.renderer.TimeScaleRenderer;

public class CalendarView extends ViewGroup implements CalendarInterface {

    public enum Mode {
        DAY, WEEK
    }

    private static final String TAG = CalendarView.class.getSimpleName();

    private boolean mOffsetsCalculated = false;

    /**
     * Gesture listener for custom callbacks when making gestures on the chart.
     */
    private OnChartGestureListener mGestureListener;

    protected BarLineChartTouchListener calendarTouchListener;

    private Properties properties;

    private Config config;

    private ViewPortHandler viewPortHandler;

    private Transformer transformer;

    private GridRenderer gridRenderer;

    private CircleRenderer circleRenderer;

    private TimeScaleRenderer timeScaleRenderer;

    private CategoryRenderer categoryRenderer;

    private float cellHeight;

    private float hourStepHeight;

    private float mDeltaX;
    private float mXChartMin;
    private float mXChartMax;
    private float minutesRange;
    private float calendarMinYValue;


    /**
     * default constructor for initialization in code
     */
    public CalendarView(Context context) {
        super(context);
        init();
    }

    /**
     * constructor for initialization in xml
     */
    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * even more awesome constructor
     */
    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {

        properties = new Properties();
        config = new Config(getContext());
        viewPortHandler = new ViewPortHandler();
        transformer = new Transformer(viewPortHandler);

        calendarTouchListener = new BarLineChartTouchListener(this, config.getResourcesHolder(), viewPortHandler.getMatrixTouch());

        gridRenderer = new DefaultGridRenderer();
        timeScaleRenderer = new DefaultTimeScaleRenderer();
        categoryRenderer = new DefaultCategoryRenderer();
        circleRenderer = new CircleRenderer(viewPortHandler, transformer, config.getResourcesHolder());

        calcMinMax();

        if (properties.isLogEnabled())
            Log.i("", "Chart.init()");
    }

    // for performance tracking
    private long totalTime = 0;
    private long drawCycles = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);

        if (isInEditMode()) {
            canvas.drawColor(Color.rgb(200, 200, 200));
            canvas.drawText("CalendarView: No Preview available", canvas.getWidth()/2, canvas.getHeight()/2, config.getResourcesHolder().getInfoPaint());
            return;
        }

        long startTime = System.currentTimeMillis();

        calculateOffsets();

        if (timeScaleRenderer != null && config.isTimeScaleEnabled()) {
            timeScaleRenderer.renderTimeScale(canvas, config, viewPortHandler, transformer);
        }

        if (categoryRenderer != null && config.isCategoriesEnabled()) {
            categoryRenderer.renderCategories(canvas, config, viewPortHandler, transformer);
        }

        // make sure the graph values and grid cannot be drawn outside the
        // content-rect
        int clipRestoreCount = canvas.save();
        canvas.clipRect(viewPortHandler.getContentRect());

        gridRenderer.renderBackground(canvas, config, viewPortHandler, transformer);

        if (config.getCalendarEvents() == null || config.getCalendarEvents().isEmpty()) {

            boolean hasText = !TextUtils.isEmpty(config.getNoDataText());
            boolean hasDescription = !TextUtils.isEmpty(config.getNoDataTextDescription());
            float line1height = hasText ? calcTextHeight(config.getResourcesHolder().getInfoPaint(), config.getNoDataText()) : 0.f;
            float line2height = hasDescription ? calcTextHeight(config.getResourcesHolder().getInfoPaint(), config.getNoDataTextDescription()) : 0.f;
            float lineSpacing = (hasText && hasDescription) ?
                (config.getResourcesHolder().getInfoPaint().getFontSpacing() - line1height) : 0.f;

            // if no data, inform the user

            float y = (getHeight() -
                (line1height + lineSpacing + line2height)) / 2.f
                + line1height;

            if (hasText) {
                canvas.drawText(config.getNoDataText(), getWidth() / 2, y, config.getResourcesHolder().getInfoPaint());

                if (hasDescription) {
                    y = y + line1height + lineSpacing;
                }
            }

            if (hasDescription) {
                canvas.drawText(config.getNoDataTextDescription(), getWidth() / 2, y, config.getResourcesHolder().getInfoPaint());
            }
        }

        gridRenderer.renderVerticalLines(canvas, config, viewPortHandler, transformer);
        gridRenderer.renderTimeSteps(canvas, config, viewPortHandler, transformer);

        circleRenderer.renderCircle(canvas);

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        if (properties.isLogEnabled()) {
            long drawTime = (System.currentTimeMillis() - startTime);
            totalTime += drawTime;
            drawCycles += 1;
            long average = totalTime / drawCycles;
            if (drawCycles % 100 == 0) {
                Log.i(TAG, "Draw Time: " + drawTime + " ms, average: " + average + " ms, cycles: " + drawCycles);
            }

        }
    }

    /**
     * calculates the approximate height of a text, depending on a demo text
     * avoid repeated calls (e.g. inside drawing methods)
     *
     * @param paint
     * @param demoText
     * @return
     */
    public static int calcTextHeight(Paint paint, String demoText) {

        Rect r = new Rect();
        paint.getTextBounds(demoText, 0, demoText.length(), r);
        return r.height();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = ScreenHelper.dpToPx(getResources(), 50f);
        setMeasuredDimension(
            Math.max(
                getSuggestedMinimumWidth(),
                resolveSize(size, widthMeasureSpec)
            ),
            Math.max(
                getSuggestedMinimumHeight(),
                resolveSize(size, heightMeasureSpec)
            )
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (properties.isLogEnabled())
            Log.i(TAG, "OnSizeChanged()");

        if (w > 0 && h > 0 && w < 10000 && h < 10000) {

            viewPortHandler.setChartDimens(w, h);

            if (properties.isLogEnabled())
                Log.i(TAG, "Setting chart dimens, width: " + w + ", height: " + h);

        }

        notifyDataSetChanged();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * will be called on a touch event.
     * needed to use scaling and scrolling
     *
     * @return true if it was consumed
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);

        if (!properties.isTouchEnabled()) {
            return b;
        }

        b |= calendarTouchListener.onTouch(this, event);
        return b;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (calendarTouchListener != null) {
            calendarTouchListener.computeScroll();
        }
    }

    @Override public float getMinX() {
        return 0;
    }

    @Override public float getMaxX() {
        return 0;
    }

    @Override public float getMinY() {
        return 0;
    }

    @Override public float getMaxY() {
        return 0;
    }

    @Override public PointF getCenterOfView() {
        return getCenter();
    }

    @Override public PointF getCenterOffsets() {
        return viewPortHandler.getContentCenter();
    }

    @Override public RectF getContentRect() {
        return viewPortHandler.getContentRect();
    }

    /**
     * Returns the center point of the chart (the whole View) in pixels.
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2f, getHeight() / 2f);
    }

    public void notifyDataSetChanged() {

        List<CalendarEvent> calendarEvents = config.getCalendarEvents();

        if (calendarEvents == null || calendarEvents.isEmpty()) {
            if (properties.isLogEnabled())
                Log.i(TAG, "Preparing... DATA NOT SET.");
        }

        if (properties.isLogEnabled())
            Log.i(TAG, "Preparing...");


        float hoursCount = config.getHoursCount();
        float maxZoomOutHours = config.getMaxZoomOutHours();

        float zoomOutHours = hoursCount >= maxZoomOutHours ? maxZoomOutHours : hoursCount;
        float minScale = hoursCount / zoomOutHours;

        //viewPortHandler.setMinimumScaleX(minScale);
        viewPortHandler.setMinimumScaleY(minScale);

        float maxZoomInHours = config.getMaxZoomInHours();

        float zoomInHours = hoursCount >= maxZoomInHours ? maxZoomInHours : hoursCount;

        float maxScale = hoursCount / zoomInHours;

        //viewPortHandler.setMaximumScaleX(maxScale);
        viewPortHandler.setMaximumScaleY(maxScale);

        Log.d(TAG, "max scale " + maxScale);
        Log.d(TAG, "min scale " + minScale);

        mOffsetsCalculated = false;
        calcMinMax();
        calculateOffsets();
    }

    protected void calcMinMax() {

        mXChartMin = 1;
        mXChartMax = config.getDaysCount();
        mDeltaX = config.getDaysCount() * config.getCategoriesCount();
        minutesRange = config.getHoursCount() * 60; // convert to minutes
        calendarMinYValue = -minutesRange;
    }

    public void calculateOffsets() {
        if (mOffsetsCalculated) {
            return;
        }

        float minOffset = config.getResourcesHolder().dpToPx(config.getMinOffset());

        float offsetLeftExtra = config.getResourcesHolder().dpToPx(config.getExtraLeftOffset()),
            offsetRightExtra = config.getResourcesHolder().dpToPx(config.getExtraRightOffset()),
            offsetTopExtra = config.getResourcesHolder().dpToPx(config.getExtraTopOffset()),
            offsetBottomExtra = config.getResourcesHolder().dpToPx(config.getExtraBottomOffset());

        float offsetLeft = Math.max(minOffset, offsetLeftExtra);
        float offsetTop = Math.max(minOffset, offsetRightExtra);
        float offsetRight = Math.max(minOffset, offsetTopExtra);
        float offsetBottom = Math.max(minOffset, offsetBottomExtra);

        if (config.isTimeScaleEnabled()) {
            offsetLeft += config.getResourcesHolder().dpToPx(config.getTimeScaleWidth());
        }

        if (config.isCategoriesEnabled()) {
            offsetTop += config.getResourcesHolder().dpToPx(config.getCategoriesHeight());
        }

        viewPortHandler.restrainViewPort(
            offsetLeft,
            offsetTop,
            offsetRight,
            offsetBottom
        );

        if (properties.isLogEnabled()) {
            Log.i(TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop
                + ", offsetRight: " + offsetRight + ", offsetBottom: " + offsetBottom);

            Log.i(TAG, "mXChartMin: " + mXChartMin + ", mDeltaX: " + mDeltaX
                + ", minutesRange: " + minutesRange + ", calendarMinYValue: " + calendarMinYValue);
        }

        transformer.prepareMatrixOffset();
        transformer.prepareMatrixValuePx(mXChartMin, mDeltaX, minutesRange, calendarMinYValue);

        mOffsetsCalculated = true;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    /**
     * disables intercept touchevents
     */
    public void disableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(true);
    }

    /**
     * enables intercept touchevents
     */
    public void enableScroll() {
        ViewParent parent = getParent();
        if (parent != null)
            parent.requestDisallowInterceptTouchEvent(false);
    }

    /**
     * Returns the custom gesture listener.
     */
    @Nullable
    public OnChartGestureListener getOnChartGestureListener() {
        return mGestureListener;
    }

    @NonNull
    public ViewPortHandler getViewPortHandler() {
        return viewPortHandler;
    }

    @NonNull
    public Properties.Builder newProperties() {
        return new Properties.Builder(this, new Properties());
    }

    @NonNull
    public Properties.Builder mergeProperties() {
        return new Properties.Builder(this, properties);
    }

    @NonNull
    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        if (properties == null) {
            throw new IllegalStateException("Properties cannot be null");
        }
        this.properties = properties;
    }

    @NonNull
    public Transaction newTransaction() {
        return new Transaction(this, config);
    }

    @NonNull
    public Config.Builder config() {
        return new Config.Builder(this, config);
    }

    @NonNull
    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        if (config == null) {
            throw new IllegalStateException("Config cannot be null");
        }

        this.config = config;

        Log.d("Config", "categories " + config.getCategoriesCount());



        notifyDataSetChanged();
        ViewCompat.postInvalidateOnAnimation(this);
    }


    /**
     * Highlights the value selected by touch gesture. Unlike
     * highlightValues(...), this generates a callback to the
     * OnChartValueSelectedListener.
     *
     * @param high         - the highlight object
     * @param callListener - call the listener
     */
    public void highlightValue(Highlight high, boolean callListener) {

        /*Entry e = null;

        if (high == null) {
            mIndicesToHighlight = null;
        } else {

            if (getProperties().isLogEnabled())
                Log.i(TAG, "Highlighted: " + high.toString());

            e = mData.getEntryForHighlight(high);
            if (e == null || e.getXIndex() != high.getXIndex()) {
                mIndicesToHighlight = null;
                high = null;
            } else {
                // set the indices to highlight
                mIndicesToHighlight = new Highlight[]{
                    high
                };
            }
        }

        if (callListener && mSelectionListener != null) {

            if (!valuesToHighlight())
                mSelectionListener.onNothingSelected();
            else {
                // notify the listener
                mSelectionListener.onValueSelected(e, high.getDataSetIndex(), high);
            }
        }
        // redraw the chart
        invalidate();*/
    }

    /**
     * Zooms in by 1.4f, into the charts center. center.
     */
    public void zoomIn() {

        PointF center = viewPortHandler.getContentCenter();

        Matrix save = viewPortHandler.zoomIn(center.x, -center.y);
        viewPortHandler.refresh(save, this, false);

        // Range might have changed, which means that Y-axis labels
        // could have changed in size, affecting Y-axis size.
        // So we need to recalculate offsets.
        calculateOffsets();
        postInvalidate();
    }

    /**
     * Zooms out by 0.7f, from the charts center. center.
     */
    public void zoomOut() {

        PointF center = viewPortHandler.getContentCenter();

        Matrix save = viewPortHandler.zoomOut(center.x, -center.y);
        viewPortHandler.refresh(save, this, false);

        // Range might have changed, which means that Y-axis labels
        // could have changed in size, affecting Y-axis size.
        // So we need to recalculate offsets.
        calculateOffsets();
        postInvalidate();
    }

    /**
     * Zooms in or out by the given scale factor. x and y are the coordinates
     * (in pixels) of the zoom center.
     *
     * @param scaleX if < 1f --> zoom out, if > 1f --> zoom in
     * @param scaleY if < 1f --> zoom out, if > 1f --> zoom in
     * @param x
     * @param y
     */
    public void zoom(float scaleX, float scaleY, float x, float y) {
        Matrix save = viewPortHandler.zoom(scaleX, scaleY, x, -y);
        viewPortHandler.refresh(save, this, false);

        // Range might have changed, which means that Y-axis labels
        // could have changed in size, affecting Y-axis size.
        // So we need to recalculate offsets.
        calculateOffsets();
        postInvalidate();
    }

    /**
     * Resets all zooming and dragging and makes the chart fit exactly it's
     * bounds.
     */
    public void fitScreen() {
        Matrix save = viewPortHandler.fitScreen();
        viewPortHandler.refresh(save, this, false);

        calculateOffsets();
        postInvalidate();
    }

    /**
     * Sets the minimum scale factor value to which can be zoomed out. 1f =
     * fitScreen
     *
     * @param scaleX
     * @param scaleY
     */
    public void setScaleMinimum(float scaleX, float scaleY) {
        viewPortHandler.setMinimumScaleX(scaleX);
        viewPortHandler.setMinimumScaleY(scaleY);
    }

    private void calculateStepHeight() {

        /*final int maxHeight = getResources().getDimensionPixelSize(R.dimen.calendar_max_cell_height);
        final int min5Height = getResources().getDimensionPixelSize(R.dimen.calendar_5_minute_height);*/
        final float maxHeight = ScreenHelper.dpToPx(getResources(), 48);
        final float min5Height = ScreenHelper.dpToPx(getResources(), 8);

        int stepSize = 15; // in minutes

        cellHeight = Math.min(maxHeight, stepSize / 5 * min5Height);
        hourStepHeight = cellHeight * 60 / stepSize;
    }



    /*private void drawCells(Canvas canvas) {
        int columns = (type == Type.WEEK ? 7 : 1) * categoriesCount;
        int columnsDividers = columns - 1;

        float startX = viewPortHandler.offsetLeft();
        float actualWidth = viewPortHandler.getChartWidth() - startX;
        float columnWidth = actualWidth / columns;

        // TODO: 30/01/16 define minimum width for columns depending on type/zoom/etc

        if (columnWidth > 0 && columns > 0) {
            final int restore = canvas.save();

            canvas.translate(getScrollX(), getScrollY());

            final int dp = (int) (ScreenHelper.dpToPx(getContext(), 1) / 2 + 0.5);
            final int margin = calendarSheetMargin + dp;

            // Draw secondary dividers
            int dividerHeight = cellHeight;
            int startY = -getScrollY() % dividerHeight - dp;
            for (int y = startY; y <= canvas.getHeight() + startY + dividerHeight; y += dividerHeight) {
                cellPaint.setColor(colorCellSecondary);
                canvas.drawLine(margin, y, canvas.getWidth(), y, cellPaint);
            }

            final int hourOffset = durationToPx(new Duration(timeScale.getStart(), timeScale.getStart()
                .withMinuteOfHour(0)
                .plusHours(1)), hourStepHeight);

            // Draw primary dividers
            dividerHeight = stepSize.getMinute() >= 20 ? hourStepHeight : cellHeight * 2;
            startY = -getScrollY() % dividerHeight - dp + hourOffset;
            for (int y = startY; y <= canvas.getHeight() + startY + dividerHeight + hourOffset; y += dividerHeight) {
                cellPaint.setColor(colorCellPrimary);
                canvas.drawLine(margin, y, canvas.getWidth(), y, cellPaint);
            }

            final boolean isExpandingOrCollapsing = expandAnimator != null && expandAnimator.isRunning();

            final int startX = margin - getScrollX() % columnWidth - dp;

            // draw vertical lines when not expanded
            if (!isExpanded() && !isExpandingOrCollapsing) {
                for (int x = startX; x <= canvas.getWidth() + startX + columnWidth; x += columnWidth) {
                    canvas.drawLine(x, 0, x, canvas.getHeight(), cellPaint);
                }
            }

            // draw vertical lines when expanding/collapsing
            if (isExpandingOrCollapsing) {
                final int maxCells = Math.round(canvas.getWidth() / columnWidth);
                final int firstVisibleCellIndex = getScrollX() / columnWidth;

                for (int i = 0; i < maxCells; i++) {
                    final int x;
                    if (firstVisibleCellIndex + i <= expandedSheetIndex) {
                        x = startX + i * columnWidth;
                    } else {
                        x = startX + (i - 1) * columnWidth + cellExpandingWidth;
                    }

                    canvas.drawLine(x, 0, x, canvas.getHeight(), cellPaint);
                }
            }

            canvas.restoreToCount(restore);
        }
    }*/

    /**
     * Pre-allocates {@link Rect}s for each calendar entry and initializes them using current layout size
     */
    private void calculateEntryRectBounds(boolean initialRun) {
        // todo
    }

   /* private void drawTimeColumnAndAxes(Canvas canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0, mHeaderTextHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.
        canvas.clipRect(0, mHeaderTextHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, getHeight(), Region.Op.REPLACE);

        for (int i = 0; i < 24; i++) {
            float top = mHeaderTextHeight + mHeaderRowPadding * 2 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            String time = getDateTimeInterpreter().interpretTime(i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            if (top < getHeight()) canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, mTimeTextPaint);
        }
    }*/
}
