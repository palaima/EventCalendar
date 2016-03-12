package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Path;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public final class DefaultGridRenderer extends GridRenderer {

    private final Path  gridLinePath        = new Path();

    // pre allocate to save performance (dont allocate in loop)
    private float[] position = new float[] {
        0f, 0f
    };

    @Override public void renderBackground(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    ) {
        if (config.isDrawGridBackground()) {
            canvas.drawRect(viewPortHandler.getContentRect(), config.getResourcesHolder().getCalendarGridBackgroundPaint());
        }

        if (config.isDrawBorders()) {
            canvas.drawRect(viewPortHandler.getContentRect(), config.getResourcesHolder().getCalendarBorderPaint());
        }
    }

    @Override public void renderVerticalLines(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    ) {
        // todo change columns count calculation
        int columns = config.getDaysCount() * config.getCategoriesCount();

        for (int i = 1; i < columns; i++) {

            position[0] = (i + 1);
            transformer.pointValuesToPixel(position);

            if (viewPortHandler.isInBoundsX(position[0])) {
                gridLinePath.moveTo(position[0], viewPortHandler.contentTop());
                gridLinePath.lineTo(position[0], viewPortHandler.contentBottom());

                // draw a path because lines don't support dashing on lower android versions
                canvas.drawPath(gridLinePath, config.getResourcesHolder().getCalendarGridPaint());
                gridLinePath.reset();
            }
        }
    }

    @Override public void renderTimeSteps(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    ) {
        for (int i = 0; i < config.getHoursCount(); i++) {

            position[1] = -((i+1) * 60);
            transformer.pointValuesToPixel(position);

            if (viewPortHandler.isInBoundsY(position[1])) {
                gridLinePath.moveTo(viewPortHandler.contentLeft(), position[1]);
                gridLinePath.lineTo(viewPortHandler.contentRight(), position[1]);

                // draw a path because lines don't support dashing on lower android versions
                canvas.drawPath(gridLinePath, config.getResourcesHolder().getCalendarGridPaint());
                gridLinePath.reset();
            }
        }
    }
}
