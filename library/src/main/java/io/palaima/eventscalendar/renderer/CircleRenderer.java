package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import io.palaima.eventscalendar.ResourcesHolder;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public class CircleRenderer implements Renderer {

    private final ViewPortHandler viewPortHandler;
    private final Transformer     transformer;
    private final ResourcesHolder resourcesHolder;

    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float circleX;
    private float circleY;

    public CircleRenderer(ViewPortHandler viewPortHandler, Transformer transformer, ResourcesHolder resourcesHolder) {
        this.viewPortHandler = viewPortHandler;
        this.transformer = transformer;
        this.resourcesHolder = resourcesHolder;

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(resourcesHolder.dpToPx(1));

        circleX = 2f;
        circleY = -100;
    }

    public void renderCircle(Canvas canvas) {

        float[] pos = new float[]{
            circleX, circleY
        };

        transformer.pointValuesToPixel(pos);

        if (viewPortHandler.isInBounds(pos[0], pos[1])) {
            canvas.drawRect(pos[0], pos[1], pos[0] + 100, pos[1] + 100, borderPaint);
        }
    }

}
