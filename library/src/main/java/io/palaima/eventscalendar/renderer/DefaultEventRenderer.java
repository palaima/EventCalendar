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
import io.palaima.eventscalendar.DateHelper;
import io.palaima.eventscalendar.DefaultCategoryDateConverter;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;
import io.palaima.eventscalendar.data.CalendarEvent;
import io.palaima.eventscalendar.data.Category;

public class DefaultEventRenderer extends CategoryRenderer {

    // pre allocate to save performance (dont allocate in loop)
    private final float[] startTimePosition = new float[] {
        0f, 0f
    };

    // pre allocate to save performance (dont allocate in loop)
    private final float[] endTimePosition = new float[] {
        0f, 0f
    };

    // pre allocate to save performance (dont allocate in loop)
    private final float[] startPosition = new float[] {
        0f, 0f
    };

    // pre allocate to save performance (dont allocate in loop)
    private final float[] endPosition = new float[] {
        0f, 0f
    };

    private boolean initialRun = true;

    private final List<RectF> rects = new ArrayList<>();

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

        final float top = viewPortHandler.contentTop();
        final float left = viewPortHandler.contentLeft();
        final float right = left + viewPortHandler.contentWidth();
        final float bottom = top + viewPortHandler.contentHeight();

        final List<? extends CalendarEvent> calendarEvents = config.getCalendarEvents();
        final int eventsCount = calendarEvents.size();

        final List<Category> categories = config.getCategories();
        final int categoriesCount = categories.size();
        final int columns = categoriesCount * config.getDaysCount();

        int currentCategory = 0;

        if (eventsCount != rects.size()) {
            initialRun = true;
            rects.clear();
        }

        //Timber.d("eventsCount " + eventsCount);

        final int clipRestoreCount = canvas.save();
        canvas.clipRect(left, top, right, bottom);

        for (int i = 0; i < columns; i++) {

            startPosition[0] = (i + 1);
            endPosition[0] = (i + 2);
            transformer.pointValuesToPixel(startPosition);
            transformer.pointValuesToPixel(endPosition);

            if (currentCategory > categoriesCount - 1) {
                currentCategory = 0;
            }

            final Category category = categories.get(currentCategory);

            activeCalendarDate.setTime(config.getActiveDate());
            activeCalendarDate.add(Calendar.DATE, i/categoriesCount);

            final List<CalendarEvent> categoryEvents = config.getEventsBy(DateHelper.startOfTheDay(activeCalendarDate).getTime(), category);

            if (!calendarEvents.isEmpty()) {
                CalendarEvent event;
                for (int j = 0; j < categoryEvents.size(); j++) {
                    event = categoryEvents.get(j);

                    activeCalendarDate.setTime(event.getStart());
                    int startHours = activeCalendarDate.get(Calendar.HOUR_OF_DAY);
                    int startMinutes = activeCalendarDate.get(Calendar.MINUTE);

                    activeCalendarDate.setTime(event.getEnd());
                    int endHours = activeCalendarDate.get(Calendar.HOUR_OF_DAY);
                    int endMinutes = activeCalendarDate.get(Calendar.MINUTE);

                    int startTime = startHours * 60 + startMinutes;
                    int endTime = endHours * 60 + endMinutes;

                    if (initialRun) {
                        // Initialize category rect on the first run
                        rects.add(newRect(i, 1, 1, startTime, endTime - startTime, transformer));
                    }

                    RectF eventRect = rects.get(i);

                    if (!initialRun) {
                        // Every run adjust categories width
                        eventRect = updateRect(eventRect, i, 1, 1f, startTime, endTime - startTime, transformer);
                    }

                    if (isInBounds(viewPortHandler, left, right, eventRect)) {
                        canvas.drawRect(eventRect, config.getResourcesHolder().getInfoPaint());
                    }
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

        int clipRestoreCount = canvas.save();
        canvas.clipRect(areaRect);

        canvas.drawText(text, textBounds.left, textBounds.top - textPaint.ascent(), textPaint);

        canvas.restoreToCount(clipRestoreCount);
    }

    private boolean isInBounds(@NonNull ViewPortHandler viewPortHandler, float left, float right, RectF rect) {
        if (rect.right < left
            || rect.left > right) {
            return false;
        }

        return viewPortHandler.isInBoundsX(rect.left)
            || viewPortHandler.isInBoundsX(rect.right)
            || (rect.left <= left && rect.right >= right);
    }

    private RectF newRect(final int column, final float startWidthCoef, final float endWidthCoef, final float startTime, final float duration, final Transformer transformer) {
        return updateRect(new RectF(), column, startWidthCoef, endWidthCoef, startTime, duration, transformer);
    }

    private RectF updateRect(final RectF rect, final int column, final float startWidthCoef, final float endWidthCoef, final float startTime, final float duration, final Transformer transformer) {
        startPosition[0] = (column + 1) * startWidthCoef;
        endPosition[0] = (column + 2) * endWidthCoef;
        transformer.pointValuesToPixel(startPosition);
        transformer.pointValuesToPixel(endPosition);

        startTimePosition[1] = -startTime;
        endTimePosition[1] = -(startTime + duration);
        transformer.pointValuesToPixel(startTimePosition);
        transformer.pointValuesToPixel(endTimePosition);

        rect.set(startPosition[0], startTimePosition[1], endPosition[0], endTimePosition[1]);

        return rect;
    }
}
