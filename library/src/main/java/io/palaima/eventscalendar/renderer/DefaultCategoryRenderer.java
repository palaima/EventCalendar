package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public class DefaultCategoryRenderer extends CategoryRenderer {

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // pre allocate to save performance (dont allocate in loop)
    private float[] position = new float[] {
        0f, 0f
    };

    @Override public void renderCategories(
        Canvas canvas, Config config, ViewPortHandler viewPortHandler, Transformer transformer
    ) {

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);

        float offsetTop = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraTopOffset()));
        float offsetLeft = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraLeftOffset()));
        float offsetRight = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraRightOffset()));
        float height = config.getResourcesHolder().dpToPx(config.getCategoriesHeight());

        canvas.drawRect(offsetLeft, offsetTop, viewPortHandler.getChartWidth() - offsetRight, offsetTop + height , config.getResourcesHolder().getTimeScaleBackgroundPaint());

        int columns = config.getCategoriesCount() * config.getDaysCount();

        for (int i = 0; i < columns; i++) {

            position[0] = (float) ((i + 1) * 1.5);
            transformer.pointValuesToPixel(position);

            if (viewPortHandler.isInBoundsX(position[0])) {
                canvas.drawText("c", position[0], offsetTop + height/2, textPaint);
            }
        }
    }
}
