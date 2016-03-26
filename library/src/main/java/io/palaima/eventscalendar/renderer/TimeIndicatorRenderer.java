package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.Calendar;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.DefaultTimeConverter;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public class TimeIndicatorRenderer implements Renderer {

    // pre allocate to save performance (dont allocate in loop)
    private final float[] position = new float[] {
        0f, 0f
    };
    private final RectF timeRect = new RectF();
    private final RectF textBounds = new RectF();

    private final PointF trianglePoint1 = new PointF();
    private final PointF trianglePoint2 = new PointF();
    private final PointF trianglePoint3 = new PointF();

    private final Path trianglePath = new Path();

    private final DefaultTimeConverter defaultTimeConverter = new DefaultTimeConverter();
    private Calendar calendar;

    public void renderTimeIndicator(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    ) {

        final float offsetTop = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraTopOffset()));
        final float offsetLeft = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraLeftOffset()));
        final float categoriesHeight = config.isCategoriesEnabled() ? config.getResourcesHolder().dpToPx(config.getCategoriesHeight()) : 0;
        final float timeScaleWidth = config.isTimeScaleEnabled() ? config.getResourcesHolder().dpToPx(config.getTimeScaleWidth()) : 0;
        final float indicatorHeight = config.getResourcesHolder().dpToPx(config.getTimeIndicatorHeight());

        final float top = offsetTop + categoriesHeight;
        final float right = offsetLeft + timeScaleWidth + viewPortHandler.contentWidth();
        final float bottom = top + viewPortHandler.contentHeight();


        final int clipRestoreCount = canvas.save();
        canvas.clipRect(offsetLeft, top, right, bottom);

        if (calendar != null) {

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);

            position[1] = - (hours * 60 + minutes);
            transformer.pointValuesToPixel(position);

            final float halfHeight = indicatorHeight / 2;

            final float timeRectTop = position[1] - halfHeight;
            final float timeRectBottom = position[1] + halfHeight;

            if (viewPortHandler.isInBoundsY(timeRectTop) || viewPortHandler.isInBoundsY(timeRectBottom)) {
                timeRect.set(offsetLeft, timeRectTop, offsetLeft + timeScaleWidth, timeRectBottom);

                trianglePoint1.set(offsetLeft + timeScaleWidth, timeRectTop);
                trianglePoint2.set(offsetLeft + timeScaleWidth + halfHeight, timeRect.centerY());
                trianglePoint3.set(offsetLeft + timeScaleWidth, timeRectBottom);

                trianglePath.reset();
                trianglePath.setFillType(Path.FillType.EVEN_ODD);
                trianglePath.moveTo(trianglePoint1.x,trianglePoint1.y);
                trianglePath.lineTo(trianglePoint2.x,trianglePoint2.y);
                trianglePath.lineTo(trianglePoint3.x,trianglePoint3.y);
                trianglePath.lineTo(trianglePoint1.x,trianglePoint1.y);
                trianglePath.close();

                canvas.drawRect(timeRect, config.getResourcesHolder().getTimeIndicatorBackgroundPaint());

                canvas.drawPath(trianglePath, config.getResourcesHolder().getTimeIndicatorBackgroundPaint());

                canvas.drawLine(offsetLeft + timeScaleWidth, timeRect.centerY(), right, timeRect.centerY(), config.getResourcesHolder().getTimeIndicatorBackgroundPaint());

                drawCenteredText(canvas, defaultTimeConverter.interpretTime(hours, minutes, DefaultTimeConverter.Type.HOUR_24), timeRect, config.getResourcesHolder().getTimeIndicatorTextPaint());
            }
        }

        canvas.restoreToCount(clipRestoreCount);
    }

    private void drawCenteredText(@NonNull Canvas canvas, @NonNull String text, @NonNull RectF areaRect, @NonNull Paint textPaint) {
        textBounds.set(areaRect);
        // measure text width
        textBounds.right = textPaint.measureText(text, 0, text.length());
        // measure text height
        textBounds.bottom = textPaint.descent() - textPaint.ascent();

        textBounds.left += (areaRect.width() - textBounds.right) / 2.0f;
        textBounds.top += (areaRect.height() - textBounds.bottom) / 2.0f;

        int clipRestoreCount = canvas.save();
        canvas.clipRect(areaRect);

        canvas.drawText(text, textBounds.left, textBounds.top - textPaint.ascent(), textPaint);

        canvas.restoreToCount(clipRestoreCount);
    }


    public void setDate(Calendar calendar) {
        this.calendar = calendar;
    }
}
