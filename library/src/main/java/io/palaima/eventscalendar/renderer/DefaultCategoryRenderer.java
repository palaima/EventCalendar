package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // pre allocate to save performance (dont allocate in loop)
    private float[] startPosition = new float[] {
        0f, 0f
    };

    // pre allocate to save performance (dont allocate in loop)
    private float[] endPosition = new float[] {
        0f, 0f
    };

    private boolean initialRun = true;

    private List<Rect> categoryRects = new ArrayList<>();

    private DefaultCategoryDateConverter dateConverter = new DefaultCategoryDateConverter();

    private Calendar activeCalendarDate = Calendar.getInstance();

    @Override public void renderCategories(
        @NonNull Canvas canvas, @NonNull Config config, @NonNull ViewPortHandler viewPortHandler, @NonNull Transformer transformer
    ) {

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20);

        final float offsetTop = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraTopOffset()));
        final float offsetLeft = config.getResourcesHolder().dpToPx(Math.max(config.getMinOffset(), config.getExtraLeftOffset()));
        final float height = config.getResourcesHolder().dpToPx(config.getCategoriesHeight());
        final float timeScaleWidth = config.getResourcesHolder().dpToPx(config.getTimeScaleWidth());

        final float left = offsetLeft + timeScaleWidth;
        final float right = left + viewPortHandler.contentWidth();
        final float bottom = offsetTop + height;

        canvas.drawRect(offsetLeft, offsetTop, right, bottom, config.getResourcesHolder().getTimeScaleBackgroundPaint());

        final List<Category> categories = config.getCategories();
        final int categoriesCount = categories.size();
        final int columns = categoriesCount * config.getDaysCount();

        int currentCategory = 0;

        if (columns != categoryRects.size()) {
            initialRun = true;
            categoryRects.clear();
        }

        final int clipRestoreCount = canvas.save();
        canvas.clipRect(left, offsetTop, right, bottom);

        for (int i = 0; i < columns; i++) {

            startPosition[0] = (i + 1);
            endPosition[0] = (i + 2);
            transformer.pointValuesToPixel(startPosition);
            transformer.pointValuesToPixel(endPosition);

            if (initialRun) {
                categoryRects.add(new Rect((int) startPosition[0], (int) offsetTop, (int) endPosition[0], (int) bottom));
            }

            final Rect categoryRect = categoryRects.get(i);

            if (!initialRun) {
                categoryRect.set((int) startPosition[0], categoryRect.top, (int) endPosition[0], categoryRect.bottom);
            }

            if (isInBounds(viewPortHandler, left, right, categoryRect.left, categoryRect.right)) {
                if (currentCategory >= categoriesCount - 1) {
                    currentCategory = 0;
                }

                activeCalendarDate.setTime(config.getActiveDate());
                activeCalendarDate.add(Calendar.DATE, i/categoriesCount);

                final Date date = activeCalendarDate.getTime();
                final Category category = categories.get(currentCategory);

                if (category.getId() == Category.DEFAULT_ID) {
                    drawDefaultCategory(canvas, categoryRect, date, category, config);
                } else {
                    drawCategory(canvas, categoryRect, date, category, config);
                }

                currentCategory++;
            }
        }

        canvas.restoreToCount(clipRestoreCount);

        initialRun = false;
    }

    private void drawCategory(@NonNull Canvas canvas, @NonNull Rect categoryRect, @NonNull Date date, @NonNull Category category, @NonNull Config config) {
        final String categoryName = category.getName();

        if (categoryName != null) {
            // draw text to the Canvas center
            final Rect bounds = new Rect();
            textPaint.getTextBounds(categoryName, 0, categoryName.length(), bounds);
            final float x = categoryRect.left + (categoryRect.width() - bounds.width())/2;
            final float y = categoryRect.top + (categoryRect.height() - bounds.height())/2;

            canvas.drawText(categoryName, x, y, textPaint);
        }

        canvas.drawLine(categoryRect.left, categoryRect.top, categoryRect.left, categoryRect.bottom, config.getResourcesHolder().getCalendarGridPaint());

    }

    private void drawDefaultCategory(@NonNull Canvas canvas,@NonNull  Rect categoryRect, @NonNull Date date, @NonNull Category category, @NonNull Config config) {
        if (category.getId() != Category.DEFAULT_ID) {
            throw new IllegalStateException("Only default category supported");
        }

        final String categoryName = dateConverter.getValue(date, DefaultCategoryDateConverter.Type.SHORT);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        textPaint.getTextBounds(categoryName, 0, categoryName.length(), bounds);
        final float x = categoryRect.left + (categoryRect.width() - bounds.width())/2;
        final float y = categoryRect.top + (categoryRect.height() - bounds.height())/2;

        canvas.drawText(categoryName, x, y, textPaint);

        canvas.drawLine(categoryRect.left, categoryRect.top, categoryRect.left, categoryRect.bottom, config.getResourcesHolder().getCalendarGridPaint());
    }

    private boolean isInBounds(@NonNull ViewPortHandler viewPortHandler, float left, float right, float startX, float endX) {
        if (endX < left || startX > right) {
            return false;
        }

        return viewPortHandler.isInBoundsX(startX) || viewPortHandler.isInBoundsX(endX) || (startX <= left && endX >= right);

    }
}
