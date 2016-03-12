package io.palaima.eventscalendar;

import android.graphics.PointF;
import android.graphics.RectF;

public interface CalendarInterface {

    float getMinX();

    float getMaxX();

    float getMinY();

    float getMaxY();

   /* int getXValCount();*/

    int getWidth();

    int getHeight();

    PointF getCenterOfView();

    PointF getCenterOffsets();

    RectF getContentRect();
}
