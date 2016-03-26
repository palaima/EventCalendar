package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.DefaultTimeConverter;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public final class DefaultTimeScaleRenderer extends TimeScaleRenderer {

    private final Path  gridLinePath    = new Path();
    private final DefaultTimeConverter defaultTimeConverter = new DefaultTimeConverter();
    private final Rect textBounds = new Rect();

    // pre allocate to save performance (dont allocate in loop)
    private final float[] position = new float[] {
        0f, 0f
    };

    @Override public void renderTimeScale(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    ) {

        final float offsetTop = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraTopOffset()));
        final float offsetLeft = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraLeftOffset()));
        final float width = config.getResourcesHolder().dpToPx(config.getTimeScaleWidth());
        final float categoriesHeight = config.isCategoriesEnabled() ? config.getResourcesHolder().dpToPx(config.getCategoriesHeight()) : 0;


        final float top = offsetTop + categoriesHeight;
        final float right = offsetLeft + width;
        final float bottom = top + viewPortHandler.contentHeight();

        float ticksPerHour = 60 / config.getTimeScaleTicksEveryMinutes();

        float ticksCount = config.getHoursCount() * ticksPerHour;

        canvas.drawRect(offsetLeft, offsetTop, right, bottom, config.getResourcesHolder().getTimeScaleBackgroundPaint());

        final int clipRestoreCount = canvas.save();
        canvas.clipRect(offsetLeft, top, right, bottom);

        for (int i = 0; i < ticksCount; i++) {

            float minutes = ((i + 1) * 60 / ticksPerHour);
            position[1] = -minutes;
            transformer.pointValuesToPixel(position);

            if (viewPortHandler.isInBoundsY(position[1])) {

                if (minutes % 60 == 0) {
                    float hours = (minutes / 60);
                    String s = defaultTimeConverter.interpretHour((int) hours, DefaultTimeConverter.Type.HOUR_24);
                    drawTextCentred(canvas, config.getResourcesHolder().getTimeScaleTextPaint(), s, width/2 + offsetLeft, position[1]);
                }

                gridLinePath.moveTo(offsetLeft, position[1]);
                gridLinePath.lineTo(offsetLeft + width, position[1]);

                // draw a path because lines don't support dashing on lower android versions
                canvas.drawPath(gridLinePath, config.getResourcesHolder().getTimeScaleGridPaint());
                gridLinePath.reset();
            }
        }

        canvas.restoreToCount(clipRestoreCount);
    }

    public void drawTextCentred(Canvas canvas, Paint paint, String text, float cx, float cy){
        paint.getTextBounds(text, 0, text.length(), textBounds);
        canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint);
    }
}
