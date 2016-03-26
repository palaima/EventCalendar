package io.palaima.eventscalendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import java.util.HashMap;

public final class ResourcesHolder {

    private final Context context;
    private final Resources resources;

    private Paint categoryTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint timeScaleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint timeScaleGridPaint       = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint timeScaleBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint calendarGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint calendarGridBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint calendarBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static int minimumFlingVelocity = 50;
    private static int maximumFlingVelocity = 8000;

    private HashMap<Float, Float> dps = new HashMap<>();

    ResourcesHolder(@NonNull Context context) {
        this.context = context;
        this.resources = context.getResources();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        minimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        maximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();


        timeScaleBackgroundPaint.setStyle(Paint.Style.FILL);
        timeScaleBackgroundPaint.setColor(Color.WHITE);

        timeScaleGridPaint.setColor(Color.GRAY);
        timeScaleGridPaint.setStrokeWidth(1f);
        timeScaleGridPaint.setStyle(Paint.Style.STROKE);
        timeScaleGridPaint.setAlpha(90);


        calendarGridBackgroundPaint.setStyle(Paint.Style.FILL);
        calendarGridBackgroundPaint.setColor(Color.rgb(240, 240, 240)); // light

        calendarBorderPaint.setStyle(Paint.Style.STROKE);
        calendarBorderPaint.setColor(Color.BLACK);
        calendarBorderPaint.setStrokeWidth(ScreenHelper.dpToPx(resources, 1));

        calendarGridPaint.setColor(Color.GRAY);
        calendarGridPaint.setStrokeWidth(ScreenHelper.dpToPx(resources, 0.5f));
        calendarGridPaint.setStyle(Paint.Style.STROKE);
        calendarGridPaint.setAlpha(90);

        categoryTextPaint.setColor(Color.rgb(247, 189, 51));
        categoryTextPaint.setTextSize(ScreenHelper.dpToPx(resources, 14f));

        timeScaleTextPaint.setColor(Color.BLACK);
        timeScaleTextPaint.setTextSize(ScreenHelper.dpToPx(resources, 10f));

        infoPaint.setColor(Color.rgb(247, 189, 51)); // orange
        infoPaint.setTextAlign(Paint.Align.CENTER);
        infoPaint.setTextSize(ScreenHelper.dpToPx(resources, 12f));
    }

    public Paint getCalendarBorderPaint() {
        return calendarBorderPaint;
    }

    public Paint getCalendarGridBackgroundPaint() {
        return calendarGridBackgroundPaint;
    }

    public Paint getCalendarGridPaint() {
        return calendarGridPaint;
    }

    public Paint getTimeScaleBackgroundPaint() {
        return timeScaleBackgroundPaint;
    }

    public Paint getTimeScaleGridPaint() {
        return timeScaleGridPaint;
    }

    public Paint getInfoPaint() {
        return infoPaint;
    }

    public Paint getCategoryTextPaint() {
        return categoryTextPaint;
    }

    public Paint getTimeScaleTextPaint() {
        return timeScaleTextPaint;
    }

    public float dpToPx(float dp) {
        if (dps.containsKey(dp)) {
            return dps.get(dp);
        }
        float dpToPx = ScreenHelper.dpToPx(resources, dp);
        dps.put(dp, dpToPx);
        return dpToPx;
    }

    public int getMinimumFlingVelocity() {
        return minimumFlingVelocity;
    }

    public int getMaximumFlingVelocity() {
        return maximumFlingVelocity;
    }

    public void velocityTrackerPointerUpCleanUpIfNecessary(MotionEvent ev, VelocityTracker tracker) {

        // Check the dot product of current velocities.
        // If the pointer that left was opposing another velocity vector, clear.
        tracker.computeCurrentVelocity(1000, maximumFlingVelocity);
        final int upIndex = ev.getActionIndex();
        final int id1 = ev.getPointerId(upIndex);
        final float x1 = tracker.getXVelocity(id1);
        final float y1 = tracker.getYVelocity(id1);
        for (int i = 0, count = ev.getPointerCount(); i < count; i++) {
            if (i == upIndex)
                continue;

            final int id2 = ev.getPointerId(i);
            final float x = x1 * tracker.getXVelocity(id2);
            final float y = y1 * tracker.getYVelocity(id2);

            final float dot = x + y;
            if (dot < 0) {
                tracker.clear();
                break;
            }
        }
    }

    private Bitmap fromDrawableRes(Resources resources, @DrawableRes int resId) {
        Drawable drawable = resources.getDrawable(resId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static class Builder {

        @NonNull
        private final Config config;

        @NonNull
        private final ResourcesHolder resourcesHolder;

        @NonNull
        private final CalendarView calendarView;

        private Paint calendarGridPaint;
        private Paint calendarGridBackgroundPaint;
        private Paint calendarBorderPaint;
        private Paint infoPaint;
        private Paint timeScaleGridPaint;
        private Paint timeScaleBackgroundPaint;

        public Builder(@NonNull CalendarView calendarView) {
            this.calendarView = calendarView;
            this.config = calendarView.getConfig();
            this.resourcesHolder = config.getResourcesHolder();

            calendarGridPaint = resourcesHolder.calendarGridPaint;
            calendarGridBackgroundPaint = resourcesHolder.calendarGridBackgroundPaint;
            calendarBorderPaint = resourcesHolder.calendarBorderPaint;
            infoPaint = resourcesHolder.infoPaint;
            timeScaleGridPaint = resourcesHolder.timeScaleGridPaint;
            timeScaleBackgroundPaint = resourcesHolder.timeScaleBackgroundPaint;
        }

        public Builder gridPaint(@NonNull Paint paint) {
            this.calendarGridPaint = paint;
            return this;
        }

        public Builder info(@NonNull Paint paint) {
            this.infoPaint = paint;
            return this;
        }

        public Builder gridBackgroundPaint(@NonNull Paint paint) {
            this.calendarGridBackgroundPaint = paint;
            return this;
        }

        public Builder borderPaint(@NonNull Paint paint) {
            this.calendarBorderPaint = paint;
            return this;
        }

        public Builder timeScaleGridPaint(@NonNull Paint paint) {
            this.timeScaleGridPaint = paint;
            return this;
        }

        public Builder timeScaleBackgroundPaint(@NonNull Paint paint) {
            this.timeScaleBackgroundPaint = paint;
            return this;
        }

        public Config.Builder set() {
            return calendarView.config().resources(build());
        }

        private ResourcesHolder build() {
            ResourcesHolder resourcesHolder = new ResourcesHolder(this.resourcesHolder.context);

            resourcesHolder.calendarGridPaint = calendarGridPaint;
            resourcesHolder.calendarGridBackgroundPaint = calendarGridBackgroundPaint;
            resourcesHolder.calendarBorderPaint = calendarBorderPaint;
            resourcesHolder.infoPaint = infoPaint;
            resourcesHolder.timeScaleGridPaint = timeScaleGridPaint;
            resourcesHolder.timeScaleBackgroundPaint = timeScaleBackgroundPaint;

            return resourcesHolder;
        }
    }
}
