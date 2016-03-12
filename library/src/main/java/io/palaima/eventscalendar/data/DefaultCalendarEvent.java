package io.palaima.eventscalendar.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Date;

public class DefaultCalendarEvent extends CalendarEvent implements Clickable {

    private final Paint bgPaint;

    public DefaultCalendarEvent(@NonNull Date startDate, @NonNull Date endDate) {
        super(startDate, endDate);

        bgPaint = new Paint();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas, @NonNull RectF eventRect) {

        // draw default background
        canvas.drawRect(
            eventRect.left,
            eventRect.top,
            eventRect.right,
            eventRect.bottom,
            bgPaint
        );
    }

    @Override public boolean isClickable() {
        return false;
    }
}
