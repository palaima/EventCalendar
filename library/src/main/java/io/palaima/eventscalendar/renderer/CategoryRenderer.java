package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public abstract class CategoryRenderer implements Renderer {

    public abstract void renderCategories(
        Canvas canvas,
        Config config,
        ViewPortHandler viewPortHandler,
        Transformer transformer
    );
}
