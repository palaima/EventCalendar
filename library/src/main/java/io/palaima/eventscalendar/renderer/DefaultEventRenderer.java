package io.palaima.eventscalendar.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.palaima.eventscalendar.Config;
import io.palaima.eventscalendar.DateHelper;
import io.palaima.eventscalendar.DefaultCategoryDateConverter;
import io.palaima.eventscalendar.Transformer;
import io.palaima.eventscalendar.ViewPortHandler;
import io.palaima.eventscalendar.data.CalendarEvent;
import io.palaima.eventscalendar.data.Category;
import io.palaima.eventscalendar.data.EventRect;
import timber.log.Timber;

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

    private final List<EventRect> eventsRect = new ArrayList<>();

    private final DefaultCategoryDateConverter dateConverter = new DefaultCategoryDateConverter();

    private final Calendar activeCalendarDate = Calendar.getInstance();

    private final RectF textBounds = new RectF();

    @Override public void renderCategories(
        @NonNull Canvas canvas,
        @NonNull Config config,
        @NonNull ViewPortHandler viewPortHandler,
        @NonNull Transformer transformer
    ) {
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

            if (currentCategory > categoriesCount - 1) {
                currentCategory = 0;
            }

            final Category category = categories.get(currentCategory);

            activeCalendarDate.setTime(config.getActiveDate());
            activeCalendarDate.add(Calendar.DATE, i / categoriesCount);

            final List<EventRect> categoryEvents = config.getEventsBy(DateHelper.startOfTheDay(activeCalendarDate).getTime(), category);

            if (!categoryEvents.isEmpty()) {

                EventRect eventRect;

                for (int j = 0; j < categoryEvents.size(); j++) {
                    eventRect = categoryEvents.get(j);

                    activeCalendarDate.setTime(eventRect.event.getStart());
                    final int startHours = activeCalendarDate.get(Calendar.HOUR_OF_DAY);
                    final int startMinutes = activeCalendarDate.get(Calendar.MINUTE);

                    activeCalendarDate.setTime(eventRect.event.getEnd());
                    final int endHours = activeCalendarDate.get(Calendar.HOUR_OF_DAY);
                    final int endMinutes = activeCalendarDate.get(Calendar.MINUTE);

                    final int startTime = startHours * 60 + startMinutes;
                    final int endTime = endHours * 60 + endMinutes;

                    if (initialRun) {
                        // Initialize category rect on the first run
                        rects.add(new RectF());
                    }

                    final RectF eventRectF = updateRect(rects.get(i), i, eventRect.startWidthCoef, eventRect.endWidthCoef, startTime, endTime - startTime, transformer);

                    if (isInBounds(viewPortHandler, left, right, eventRectF)) {
                        canvas.drawRect(eventRectF, config.getResourcesHolder().getInfoPaint());
                    } else {
                        Timber.d("not in bounds " + eventRect);
                    }
                }
            }

            currentCategory++;
        }

        canvas.restoreToCount(clipRestoreCount);

        initialRun = false;
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

    private RectF updateRect(final RectF rect, final int column, final float startWidthCoef, final float endWidthCoef, final float startTime, final float duration, final Transformer transformer) {
        final float columnStart = (column + 1);
        final float columnEnd = (column + 2);
        final float columnWidth = columnEnd - columnStart;

        startPosition[0] = columnStart + columnWidth * startWidthCoef;
        endPosition[0] = startPosition[0] + columnWidth * endWidthCoef;

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
