package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public abstract class GridRenderer implements Renderer {

    public abstract void renderBackground(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    );

    public abstract void renderVerticalLines(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    );

    public abstract void renderTimeSteps(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    );
}
