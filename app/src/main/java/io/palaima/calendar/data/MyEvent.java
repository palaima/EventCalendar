package io.palaima.calendar.data;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Date;

import io.palaima.eventscalendar.data.CalendarEvent;

public class MyEvent extends CalendarEvent {

    public MyEvent(@NonNull Date startTime, @NonNull Date endTime, long categoryId) {
        super(startTime, endTime, categoryId);
    }

    @Override protected void onDraw(@NonNull Canvas canvas, @NonNull RectF eventRect) {

    }

    public static MyEvent from(EventEntity eventEntity) {
        return new MyEvent(eventEntity.startDate().getTime(), eventEntity.endDate().getTime(), eventEntity.categoryId());
    }
}
