package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.support.annotation.NonNull;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;

public abstract class CategoryRenderer implements Renderer {

    public abstract void renderCategories(
        @NonNull Canvas canvas,
        @NonNull Config config,
        @NonNull ViewPortHandler viewPortHandler,
        @NonNull Transformer transformer
    );
}
