package io.palaima.eventscalendar;

import android.support.annotation.NonNull;

public class Properties {
    /**
     * flag that indicates if logging is enabled or not
     */
    private boolean logEnabled = true;

    /**
     * if true, touch gestures are enabled on the chart
     */
    private boolean touchEnabled = true;

    /**
     * Flag that indicates if highlighting per tap (touch) is enabled
     */
    private boolean highlightPerTapEnabled = false;

    /**
     * flag that indicates if highlighting per dragging over a fully zoomed out
     * chart is enabled
     */
    private boolean highlightPerDragEnabled = false;

    /**
     * flag that indicates if pinch-zoom is enabled. if true, both x and y axis
     * can be scaled with 2 fingers, if false, x and y axis can be scaled
     * separately
     */
    private boolean pinchZoomEnabled = true;

    /**
     * flag that indicates if double tap zoom is enabled or not
     */
    private boolean doubleTapToZoomEnabled = true;

    /**
     * If set to true, chart continues to scroll after touch up
     */
    private boolean dragDecelerationEnabled = true;

    /**
     * Deceleration friction coefficient in [0 ; 1] interval, higher values
     * indicate that speed will decrease slowly, for example if it set to 0, it
     * will stop immediately. 1 is an invalid value, and will be converted to
     * 0.999f automatically.
     */
    private float dragDecelerationFrictionCoef = 0.9f;

    /**
     * if true, dragging is enabled for the chart
     */
    private boolean dragEnabled = true;

    private boolean scaleXEnabled = true;

    private boolean scaleYEnabled = true;

    public Properties() {

    }

    /**
     * BELOW CODE PERFORMS SETTERS AND GETTERS
     */


    public boolean isLogEnabled() {
        return logEnabled;
    }

    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    /**
     * Returns true if dragging is enabled for the chart, false if not.
     */
    public boolean isDragEnabled() {
        return dragEnabled;
    }

    public boolean isScaleXEnabled() {
        return scaleXEnabled;
    }

    public boolean isScaleYEnabled() {
        return scaleYEnabled;
    }

    /**
     * returns true if pinch-zoom is enabled, false if not
     */
    public boolean isPinchZoomEnabled() {
        return pinchZoomEnabled;
    }

    /**
     * Returns true if zooming via double-tap is enabled false if not.
     */
    public boolean isDoubleTapToZoomEnabled() {
        return doubleTapToZoomEnabled;
    }

    /**
     * Returns true if values can be highlighted via tap gesture, false if not.
     */
    public boolean isHighlightPerTapEnabled() {
        return highlightPerTapEnabled;
    }

    public boolean isHighlightPerDragEnabled() {
        return highlightPerDragEnabled;
    }

    /**
     * If set to true, chart continues to scroll after touch up default: true
     */
    public boolean isDragDecelerationEnabled() {
        return dragDecelerationEnabled;
    }

    /**
     * Returns drag deceleration friction coefficient
     */
    public float getDragDecelerationFrictionCoef() {
        return dragDecelerationFrictionCoef;
    }

    public static final class Builder {
        @NonNull
        private final CalendarView calendarView;

        private boolean logEnabled = true;
        private boolean touchEnabled = true;
        private boolean dragEnabled = true;
        private boolean scaleXEnabled = true;
        private boolean scaleYEnabled = true;
        private boolean pinchZoomEnabled = false;
        private boolean doubleTapToZoomEnabled = true;
        private boolean highlightPerTapEnabled = false;
        private boolean highlightPerDragEnabled = false;
        private boolean dragDecelerationEnabled = true;
        private float dragDecelerationFrictionCoef = 0.9f;

        public Builder(@NonNull CalendarView calendarView, @NonNull Properties properties) {
            this.calendarView = calendarView;

            logEnabled = properties.isLogEnabled();
            touchEnabled = properties.isTouchEnabled();
            dragEnabled = properties.isDragEnabled();
            scaleXEnabled = properties.isScaleXEnabled();
            scaleYEnabled = properties.isScaleYEnabled();
            pinchZoomEnabled = properties.isPinchZoomEnabled();
            doubleTapToZoomEnabled = properties.isDoubleTapToZoomEnabled();
            highlightPerTapEnabled = properties.isHighlightPerTapEnabled();
            highlightPerDragEnabled = properties.isHighlightPerDragEnabled();
            dragDecelerationEnabled = properties.isDragDecelerationEnabled();
            dragDecelerationFrictionCoef = properties.getDragDecelerationFrictionCoef();
        }

        public CalendarView set() {
            calendarView.setProperties(build());
            return calendarView;
        }

        public Properties build() {
            Properties properties = new Properties();
            properties.logEnabled = logEnabled;
            properties.touchEnabled = touchEnabled;
            properties.dragEnabled = dragEnabled;
            properties.scaleXEnabled = scaleXEnabled;
            properties.scaleYEnabled = scaleYEnabled;
            properties.pinchZoomEnabled = pinchZoomEnabled;
            properties.doubleTapToZoomEnabled = doubleTapToZoomEnabled;
            properties.highlightPerTapEnabled = highlightPerTapEnabled;
            properties.highlightPerDragEnabled = highlightPerDragEnabled;
            properties.dragDecelerationEnabled = dragDecelerationEnabled;
            properties.dragDecelerationFrictionCoef = dragDecelerationFrictionCoef;
            return properties;
        }

        public Properties.Builder logEnabled(boolean enabled) {
            logEnabled = enabled;
            return this;
        }

        public Properties.Builder touchEnabled(boolean enabled) {
            touchEnabled = enabled;
            return this;
        }

        /**
         * Set this to true to enable dragging (moving the chart with the finger)
         * for the chart (this does not effect scaling).
         */
        public Properties.Builder dragEnabled(boolean enabled) {
            dragEnabled = enabled;
            return this;
        }

        /**
         * Set this to true to enable scaling (zooming in and out by gesture) for
         * the chart (this does not effect dragging) on both X- and Y-Axis.
         */
        public Properties.Builder scaleEnabled(boolean enabled) {
            scaleXEnabled = enabled;
            scaleYEnabled = enabled;
            return this;
        }

        public Properties.Builder scaleXEnabled(boolean enabled) {
            scaleXEnabled = enabled;
            return this;
        }

        public Properties.Builder scaleYEnabled(boolean enabled) {
            scaleYEnabled = enabled;
            return this;
        }

        /**
         * If set to true, both x and y axis can be scaled simultaneously with 2 fingers, if false,
         * x and y axis can be scaled separately. default: false
         */
        public Properties.Builder pinchZoomEnabled(boolean enabled) {
            pinchZoomEnabled = enabled;
            return this;
        }

        /**
         * Set this to true to enable zooming in by double-tap on the chart.
         * Default: enabled
         */
        public Properties.Builder doubleTapToZoomEnabled(boolean enabled) {
            doubleTapToZoomEnabled = enabled;
            return this;
        }

        /**
         * Set this to false to prevent values from being highlighted by tap gesture.
         * Values can still be highlighted via drag or programmatically. Default: true
         */
        public Properties.Builder highlightPerTapEnabled(boolean enabled) {
            highlightPerTapEnabled = enabled;
            return this;
        }

        /**
         * Set this to true to allow highlighting per dragging over the chart
         * surface when it is fully zoomed out. Default: true
         */
        public Properties.Builder highlightPerDragEnabled(boolean enabled) {
            highlightPerDragEnabled = enabled;
            return this;
        }

        /**
         * If set to true, chart continues to scroll after touch up. Default: true.
         */
        public Properties.Builder dragDecelerationEnabled(boolean enabled) {
            dragDecelerationEnabled = enabled;
            return this;
        }

        /**
         * Deceleration friction coefficient in [0 ; 1] interval, higher values
         * indicate that speed will decrease slowly, for example if it set to 0, it
         * will stop immediately. 1 is an invalid value, and will be converted to
         * 0.999f automatically.
         */
        public Properties.Builder dragDecelerationFrictionCoef(float value) {
            if (value < 0.f)
                value = 0.f;

            if (value >= 1f)
                value = 0.999f;
            dragDecelerationFrictionCoef = value;
            return this;
        }
    }
}
