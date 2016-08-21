package io.palaima.eventscalendar.data;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Date;

public abstract class CalendarEvent<T> implements Comparable<CalendarEvent<T>>, Cloneable {

    public static final int DEFAULT_CATEGORY_ID = 1;

    private final Date startTime;
    private final Date endTime;
    private final long fullDuration;
    private final long startMillis;
    private final long endMillis;
    private final long categoryId;
    private final T value;

    public CalendarEvent(@NonNull T value, @NonNull Date startTime, @NonNull Date endTime) {
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMillis = startTime.getTime();
        this.endMillis = endTime.getTime();
        this.categoryId = DEFAULT_CATEGORY_ID;
        this.fullDuration = endMillis - startMillis;
    }

    public CalendarEvent(@NonNull T value, @NonNull Date startTime, @NonNull Date endTime, long categoryId) {
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMillis = startTime.getTime();
        this.endMillis = endTime.getTime();
        this.categoryId = categoryId;
        this.fullDuration = endMillis - startMillis;
    }

    public CalendarEvent(@NonNull T value, @NonNull Date startTime, @NonNull Date endTime, @NonNull Category category) {
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMillis = startTime.getTime();
        this.endMillis = endTime.getTime();
        this.categoryId = category.getId();
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
        return categoryId;
    }

    public T getValue() {
        return value;
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

    @Override public String toString() {
        return "CalendarEvent{" +
            "startTime=" + startTime +
            ", endTime=" + endTime +
            ", fullDuration=" + fullDuration +
            ", startMillis=" + startMillis +
            ", endMillis=" + endMillis +
            ", categoryId=" + categoryId +
            ", value=" + value +
            '}';
    }

    /* @Override public Object clone() {
        try {
            CalendarEvent clone = (CalendarEvent) super.clone();
            clone.startTime = (Date) startTime.clone();
            clone.endTime = (Date) endTime.clone();
            clone.startMillis = startMillis.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }*/
}
