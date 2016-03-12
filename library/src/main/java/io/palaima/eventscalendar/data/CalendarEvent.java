package io.palaima.eventscalendar.data;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Date;

public abstract class CalendarEvent implements Comparable<CalendarEvent> {

    public static final int DEFAULT_CATEGORY_ID = 1;

    private final Date startTime;
    private final Date endTime;
    private final long fullDuration;
    private final long startMillis;
    private final long endMillis;

    protected CalendarEvent(@NonNull Date startTime, @NonNull Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMillis = startTime.getTime();
        this.endMillis = endTime.getTime();
        this.fullDuration = endMillis - startMillis;
    }

    protected abstract void onDraw(@NonNull Canvas canvas, @NonNull RectF eventRect);

    public Date getStart() {
        return startTime;
    }

    public Date getEnd() {
        return endTime;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public long getCategoryId() {
        return DEFAULT_CATEGORY_ID;
    }

    /**
     * @return duration in millis
     */
    public long getFullDuration() {
        return fullDuration;
    }

    @Override
    public int compareTo(@NonNull CalendarEvent event) {
        if (getStart().equals(event.getStart())) {
            return getFullDuration() < event.getFullDuration() ? -1 : 1;
        } else {
            return getStart().before(event.getStart()) ? -1 : 1;
        }
    }

}
