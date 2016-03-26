package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.DefaultCategoryDateConverter;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;
import io.palaima.eventscalendar.data.Category;

public class DefaultCategoryRenderer extends CategoryRenderer {

    // pre allocate to save performance (dont allocate in loop)
    private final float[] startPosition = new float[] {
        0f, 0f
    };

    // pre allocate to save performance (dont allocate in loop)
    private final float[] endPosition = new float[] {
        0f, 0f
    };

    private boolean initialRun = true;

    private final List<Rect> rects = new ArrayList<>();

    private final DefaultCategoryDateConverter dateConverter = new DefaultCategoryDateConverter();

    private final Calendar activeCalendarDate = Calendar.getInstance();

    private final RectF textBounds = new RectF();

    @Override public void renderCategories(
        @NonNull Canvas canvas,
        @NonNull Config config,
        @NonNull ViewPortHandler viewPortHandler,
        @NonNull Transformer transformer
    ) {

        final float offsetTop = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraTopOffset()));
        final float offsetLeft = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraLeftOffset()));
        final float height = config.getResourcesHolder().dpToPx(config.getCategoriesHeight());
        final float timeScaleWidth = config.isTimeScaleEnabled() ? config.getResourcesHolder().dpToPx(config.getTimeScaleWidth()) : 0;

        final float left = offsetLeft + timeScaleWidth;
        final float right = left + viewPortHandler.contentWidth();
        final float bottom = offsetTop + height;

        final List<Category> categories = config.getCategories();
        final int categoriesCount = categories.size();
        final int columns = categoriesCount * config.getDaysCount();

        int currentCategory = 0;

        if (columns != rects.size()) {
            initialRun = true;
            rects.clear();
        }

        final int clipRestoreCount = canvas.save();
        canvas.clipRect(left, offsetTop, right, bottom);

        // Draw background
        canvas.drawRect(left, offsetTop, right, bottom, config.getResourcesHolder().getTimeScaleBackgroundPaint());

        for (int i = 0; i < columns; i++) {

            startPosition[0] = (i + 1);
            endPosition[0] = (i + 2);
            transformer.pointValuesToPixel(startPosition);
            transformer.pointValuesToPixel(endPosition);

            if (initialRun) {
                // Initialize category rect on the first run
                rects.add(new Rect((int) startPosition[0], (int) offsetTop, (int) endPosition[0], (int) bottom));
            }

            final Rect categoryRect = rects.get(i);

            if (!initialRun) {
                // Every run adjust categories width
                categoryRect.set((int) startPosition[0], categoryRect.top, (int) endPosition[0], categoryRect.bottom);
            }

            if (currentCategory > categoriesCount - 1) {
                currentCategory = 0;
            }

            if (isInBounds(viewPortHandler, left, right, categoryRect.left, categoryRect.right)) {

                activeCalendarDate.setTime(config.getActiveDate());
                activeCalendarDate.add(Calendar.DATE, i/categoriesCount);

                final Date date = activeCalendarDate.getTime();
                final Category category = categories.get(currentCategory);

                if (category.getId() == Category.DEFAULT_ID) {
                    drawDefaultCategory(canvas, categoryRect, date, category, config);
                } else {
                    drawCategory(canvas, categoryRect, date, category, config);
                }

            }

            currentCategory++;
        }

        canvas.restoreToCount(clipRestoreCount);

        initialRun = false;
    }

    private void drawCategory(@NonNull Canvas canvas, @NonNull Rect categoryRect, @NonNull Date date, @NonNull Category category, @NonNull Config config) {
        final String categoryName = category.getName();

        if (categoryName != null) {
            // draw text to the Canvas center
            drawCenteredText(canvas, categoryName, categoryRect, config.getResourcesHolder().getCategoryTextPaint());
        }

        canvas.drawLine(categoryRect.left, categoryRect.top, categoryRect.left, categoryRect.bottom, config.getResourcesHolder().getCalendarGridPaint());
    }

    private void drawDefaultCategory(@NonNull Canvas canvas,@NonNull  Rect categoryRect, @NonNull Date date, @NonNull Category category, @NonNull Config config) {
        if (category.getId() != Category.DEFAULT_ID) {
            throw new IllegalStateException("Only default category supported");
        }

        final String categoryName = dateConverter.getValue(date, DefaultCategoryDateConverter.Type.SHORT);

        drawCenteredText(canvas, categoryName, categoryRect, config.getResourcesHolder().getCategoryTextPaint());
        canvas.drawLine(categoryRect.left, categoryRect.top, categoryRect.left, categoryRect.bottom, config.getResourcesHolder().getCalendarGridPaint());
    }

    private void drawCenteredText(@NonNull Canvas canvas, @NonNull String text, @NonNull Rect areaRect, @NonNull Paint textPaint) {
        textBounds.set(areaRect);
        // measure text width
        textBounds.right = textPaint.measureText(text, 0, text.length());
        // measure text height
        textBounds.bottom = textPaint.descent() - textPaint.ascent();

        textBounds.left += (areaRect.width() - textBounds.right) / 2.0f;
        textBounds.top += (areaRect.height() - textBounds.bottom) / 2.0f;

        //TODO clip canvas
        canvas.drawText(text, textBounds.left, textBounds.top - textPaint.ascent(), textPaint);
    }

    private boolean isInBounds(@NonNull ViewPortHandler viewPortHandler, float left, float right, float startX, float endX) {
        if (endX < left || startX > right) {
            return false;
        }

        return viewPortHandler.isInBoundsX(startX) || viewPortHandler.isInBoundsX(endX) || (startX <= left && endX >= right);
    }
}
