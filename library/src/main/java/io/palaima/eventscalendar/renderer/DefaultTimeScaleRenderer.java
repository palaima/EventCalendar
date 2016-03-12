package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public final class DefaultTimeScaleRenderer extends TimeScaleRenderer {

    private final Path  gridLinePath    = new Path();

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // pre allocate to save performance (dont allocate in loop)
    private float[] position = new float[] {
        0f, 0f
    };

    @Override public void renderTimeScale(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    ) {

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);

        float width = config.getResourcesHolder().dpToPx(config.getTimeScaleWidth());
        float offsetLeft = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraLeftOffset()));

        canvas.drawRect(offsetLeft, viewPortHandler.contentTop(), offsetLeft + width, viewPortHandler.contentBottom(), config.getResourcesHolder().getTimeScaleBackgroundPaint());

        float ticksPerHour = 60 / config.getTimeScaleTicksEveryMinutes();

        float ticksCount = config.getHoursCount() * ticksPerHour;

        for (int i = 0; i < ticksCount; i++) {

            position[1] = -((i+1) * 60 / ticksPerHour);
            transformer.pointValuesToPixel(position);

            if (viewPortHandler.isInBoundsY(position[1])) {

                if (((i+1) * 60 / ticksPerHour) % 60 == 0) {
                    canvas.drawText("time", width/2 + offsetLeft, position[1], textPaint);
                }

                gridLinePath.moveTo(offsetLeft, position[1]);
                gridLinePath.lineTo(offsetLeft + width, position[1]);

                // draw a path because lines don't support dashing on lower android versions
                canvas.drawPath(gridLinePath, config.getResourcesHolder().getTimeScaleGridPaint());
                gridLinePath.reset();
            }
        }
    }
}
