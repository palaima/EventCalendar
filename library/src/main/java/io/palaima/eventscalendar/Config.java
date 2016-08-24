package io.palaima.eventscalendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.palaima.eventscalendar.data.CalendarEvent;
import io.palaima.eventscalendar.data.Category;
import io.palaima.eventscalendar.data.DefaultCategory;
import io.palaima.eventscalendar.data.EventRect;

public class Config {

    private ResourcesHolder resourcesHolder;

    private CalendarView.Mode mode = CalendarView.Mode.DAY;

    /**
     * Sets the minimum offset (padding) around the chart, defaults to 15
     */
    private float minOffset = 0f;

    /**
     * Extra offsets to be appended to the viewport
     */
    private float extraTopOffset = 0.f,
        extraRightOffset = 0.f,
        extraBottomOffset = 0.f,
        extraLeftOffset = 0.f;

    private boolean drawGridBackground = true;

    private boolean drawBorders = false;

    private List<? extends CalendarEvent> calendarEvents = new ArrayList<>();

    private List<? extends Category> categories = Collections.singletonList(DefaultCategory.INSTANCE);

    private SparseArray<SparseArray<List<EventRect>>> eventsMap = new SparseArray<>();

    /**
     * text that is displayed when the chart is empty
     */
    private String noDataText = "No chart data available.";

    /**
     * text that is displayed when the chart is empty that describes why the
     * chart is empty
     */
    private String noDataTextDescription = "no provided data";

    private float startHour = 0f;

    private float endHour = 24f;

    private boolean timeScaleEnabled = true;

    private float timeScaleWidth = 48;

    private int timeScaleTicksEveryMinutes = 30; // TODO enum

    private boolean categoriesEnabled = true;

    private float categoriesHeight = 48;

    private float maxZoomInHours = 2;

    private float maxZoomOutHours = 8;

    private Date activeDate = new Date();

    private Calendar today = Calendar.getInstance();

    private boolean timeIndicatorEnabled = true;

    private float timeIndicatorHeight = 24;

    private Config() {

    }

    public Config(@NonNull Context context) {
        resourcesHolder = new ResourcesHolder(context);
    }

    /**
     * BELOW CODE PERFORMS SETTERS AND GETTERS
     */

    public CalendarView.Mode getMode() {
        return mode;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public int getCategoriesCount() {
        return categories.size();
    }

    public float getMinOffset() {
        return minOffset;
    }

    public float getExtraLeftOffset() {
        return extraLeftOffset;
    }

    public float getExtraRightOffset() {
        return extraRightOffset;
    }

    public float getExtraTopOffset() {
        return extraTopOffset;
    }

    public float getExtraBottomOffset() {
        return extraBottomOffset;
    }

    public boolean isDrawBorders() {
        return drawBorders;
    }

    public boolean isDrawGridBackground() {
        return drawGridBackground;
    }

    public List<? extends CalendarEvent> getCalendarEvents() {
        return calendarEvents;
    }

    public String getNoDataText() {
        return noDataText;
    }

    public String getNoDataTextDescription() {
        return noDataTextDescription;
    }

    public float getStartHour() {
        return startHour;
    }

    public float getEndHour() {
        return endHour;
    }

    public boolean isTimeScaleEnabled() {
        return timeScaleEnabled;
    }

    public float getTimeScaleWidth() {
        return timeScaleWidth;
    }

    public int getTimeScaleTicksEveryMinutes() {
        return timeScaleTicksEveryMinutes;
    }

    public boolean isCategoriesEnabled() {
        return categoriesEnabled;
    }

    public float getCategoriesHeight() {
        return categoriesHeight;
    }

    public float getHoursCount() {
        return getEndHour() - getStartHour();
    }

    public float getMaxZoomInHours() {
        return maxZoomInHours;
    }

    public float getMaxZoomOutHours() {
        return maxZoomOutHours;
    }

    public boolean isTimeIndicatorEnabled() {
        return timeIndicatorEnabled;
    }

    public float getTimeIndicatorHeight() {
        return timeIndicatorHeight;
    }

    public Calendar getToday() {
        return today;
    }

    public int getDaysCount() {
        switch (getMode()) {
            case DAY:
                return 1;
            case WEEK:
                return 7;
            default:
                throw new IllegalStateException("Mode is not supported");
        }
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public List<EventRect> getEventsBy(Date date, Category category) {
        int key = date.hashCode();
        if (eventsMap.get(key) != null && eventsMap.get(key).get(((int) category.getId())) != null) {
            return eventsMap.get(key).get(((int) category.getId()));
        }

        return Collections.emptyList();
    }

    public ResourcesHolder getResourcesHolder() {
        return resourcesHolder;
    }

    public static final class Builder {

        @NonNull
        private final CalendarView calendarView;

        @NonNull
        private final Config config;

        private CalendarView.Mode mode;
        private float startHour;
        private float endHour;
        private float minOffset;
        private float extraTopOffset, extraRightOffset, extraBottomOffset, extraLeftOffset;
        private boolean drawGridBackground;
        private boolean drawBorders;
        private List<? extends CalendarEvent> calendarEvents;
        private List<? extends Category> categories;
        private String noDataText;
        private String noDataTextDescription;
        private Date activeDate;
        private ResourcesHolder resourcesHolder;
        private SparseArray<SparseArray<List<EventRect>>> eventsMap;

        private boolean isEventsDirty = false;

        public Builder(@NonNull CalendarView calendarView, @NonNull Config config) {
            this.calendarView = calendarView;
            this.config = config;

            mode = config.mode;
            categories = new ArrayList<>(config.categories);
            startHour = config.startHour;
            endHour = config.endHour;
            minOffset = config.minOffset;
            extraLeftOffset = config.extraLeftOffset;
            extraRightOffset = config.extraRightOffset;
            extraTopOffset = config.extraTopOffset;
            extraBottomOffset = config.extraBottomOffset;
            drawGridBackground = config.drawGridBackground;
            drawBorders = config.drawBorders;
            calendarEvents = new ArrayList<>(config.calendarEvents);
            noDataText = config.noDataText;
            noDataTextDescription = config.noDataTextDescription;
            activeDate = config.activeDate;
            resourcesHolder = config.resourcesHolder;
            eventsMap = config.eventsMap;
        }

        public CalendarView set() {
            calendarView.setConfig(build());
            return calendarView;
        }

        public Config build() {
            validateValues();

            Config config = new Config();
            config.resourcesHolder = resourcesHolder;
            config.mode = mode;
            config.categories = Collections.unmodifiableList(categories);
            config.startHour = startHour;
            config.endHour = endHour;
            config.minOffset = minOffset;
            config.extraLeftOffset = extraLeftOffset;
            config.extraRightOffset = extraRightOffset;
            config.extraTopOffset = extraTopOffset;
            config.extraBottomOffset = extraBottomOffset;
            config.drawGridBackground = drawGridBackground;
            config.drawBorders = drawBorders;
            config.calendarEvents = Collections.unmodifiableList(calendarEvents);
            config.noDataText = noDataText;
            config.noDataTextDescription = noDataTextDescription;
            config.activeDate = activeDate;

            if (isEventsDirty) {
                SparseArray<SparseArray<List<EventRect>>> eventsMap = new SparseArray<>();

                List<EventRect> eventRects = sortAndCacheEvents(calendarEvents);

                if (!eventRects.isEmpty()) {

                    for (EventRect eventRect : eventRects) {

                        CalendarEvent event = eventRect.event;

                        Date start = DateHelper.startOfTheDay(event.getStart());

                        int key = start.hashCode();
                        if (eventsMap.get(key) == null) {
                            eventsMap.put(key, new SparseArray<List<EventRect>>());
                        }

                        if (eventsMap.get(key).get(((int) event.getCategoryId())) == null) {
                            eventsMap.get(key).put(((int) event.getCategoryId()), new ArrayList<EventRect>());
                        }

                        if (eventsMap.get(key) != null) {
                            if (eventsMap.get(key).get(((int) event.getCategoryId())) != null) {
                                eventsMap.get(key).get(((int) event.getCategoryId())).add(eventRect);
                            }
                        }
                    }

                }

                this.eventsMap = eventsMap;
            }

            config.eventsMap = eventsMap;

            return config;
        }

        public Config.Builder resources(ResourcesHolder resourcesHolder) {
            this.resourcesHolder = resourcesHolder;
            return this;
        }

        public Config.Builder mode(CalendarView.Mode mode) {
            this.mode = mode;
            return this;
        }

        public Config.Builder categories(List<? extends Category> categories) {
            this.categories = categories;
            return this;
        }

        public Config.Builder hoursRange(float startHour, float endHour) {
            startHour(startHour);
            endHour(endHour);
            return this;
        }

        public Config.Builder startHour(float hour) {
            this.startHour = hour;
            return this;
        }

        public Config.Builder endHour(float hour) {
            this.endHour = hour;
            return this;
        }

        public Config.Builder activeDate(@NonNull Date date) {
            activeDate = date;
            return this;
        }

        /**
         * Sets the minimum offset (padding) around the calendar, defaults to 15
         */
        public Config.Builder minOffset(float offset) {
            minOffset = offset;
            return this;
        }

        /**
         * Sets extra offsets (around the calendar view) to be appended to the
         * auto-calculated offsets.
         */
        public Config.Builder extraOffset(float left, float top, float right, float bottom) {
            extraLeftOffset(left);
            extraTopOffset(top);
            extraRightOffset(right);
            extraBottomOffset(bottom);
            return this;
        }

        /**
         * Set an extra offset to be appended to the viewport's left
         */
        public Config.Builder extraLeftOffset(float offset) {
            extraLeftOffset = offset;
            return this;
        }

        /**
         * Set an extra offset to be appended to the viewport's right
         */
        public Config.Builder extraRightOffset(float offset) {
            extraRightOffset = offset;
            return this;
        }

        /**
         * Set an extra offset to be appended to the viewport's top
         */
        public Config.Builder extraTopOffset(float offset) {
            extraTopOffset = offset;
            return this;
        }

        /**
         * Set an extra offset to be appended to the viewport's bottom
         */
        public Config.Builder extraBottomOffset(float offset) {
            extraBottomOffset = offset;
            return this;
        }

        public Config.Builder drawGridBackground(boolean enabled) {
            this.drawGridBackground = enabled;
            return this;
        }

        public Config.Builder drawBorders(boolean enabled) {
            this.drawBorders = enabled;
            return this;
        }

        public Config.Builder events(List<? extends CalendarEvent> events) {
            if (events == null) {
                events = new ArrayList<>();
            }

            isEventsDirty = true;

            this.calendarEvents = events;
            return this;
        }

        public ResourcesHolder.Builder resources() {
            return new ResourcesHolder.Builder(calendarView);
        }

        private void validateValues() {
            if (categories == null || categories.isEmpty()) {
                categories = Collections.singletonList(DefaultCategory.INSTANCE);
            }

            if (categories.size() > 1 && categories.contains(DefaultCategory.INSTANCE)) {
                // TODO handle multiple default categories
                throw new IllegalArgumentException("Categories must not contain default category");
            }

            if (startHour < 0 || startHour > 24) {
                throw new IllegalArgumentException("start hour range 0-24");
            }

            if (endHour < 0 || endHour > 24) {
                throw new IllegalArgumentException("start hour range 0-24");
            }

            if (startHour >= endHour) {
                throw new IllegalArgumentException("start hour cannot be equal or greater than end hour");
            }
        }

        /**
         * Sort and cache events.
         * @param events The events to be sorted and cached.
         */
        private List<EventRect> sortAndCacheEvents(List<? extends CalendarEvent> events) {
            if (events.isEmpty()) {
                return Collections.emptyList();
            }

            Collections.sort(events);
            List<EventRect> eventRects = new ArrayList<>();
            for (CalendarEvent event : events) {
                eventRects.add(cacheEvent(event));
            }

            return computePositionOfEvents(eventRects);
        }

        /**
         * Cache the event for smooth scrolling functionality.
         * @param event The event to cache.
         */
        private EventRect cacheEvent(CalendarEvent event) {
            if (event.getStartMillis() >= event.getEndMillis()) {
                throw new IllegalStateException("start cannot be after end time");
            }

            if (!DateHelper.isSameDay(event.getStart(), event.getEnd())) {
                throw new IllegalStateException("events passing several days is not supported");
                // Add first day.
            /*Calendar endTime = (Calendar) activeCalendarDate.clone();
            endTime.setTime(event.getStart());
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 59);
            event.clone();
            WeekViewEvent event1 = new WeekViewEvent(event.getId(), event.getName(), event.getLocation(), event.getStartTime(), endTime);
            event1.setColor(event.getColor());
            eventsRect.add(new EventRect(event1, event));

            // Add other days.
            Calendar otherDay = (Calendar) event.getStartTime().clone();
            otherDay.add(Calendar.DATE, 1);
            while (!isSameDay(otherDay, event.getEndTime())) {
                Calendar overDay = (Calendar) otherDay.clone();
                overDay.set(Calendar.HOUR_OF_DAY, 0);
                overDay.set(Calendar.MINUTE, 0);
                Calendar endOfOverDay = (Calendar) overDay.clone();
                endOfOverDay.set(Calendar.HOUR_OF_DAY, 23);
                endOfOverDay.set(Calendar.MINUTE, 59);
                WeekViewEvent eventMore = new WeekViewEvent(event.getId(), event.getName(), overDay, endOfOverDay);
                eventMore.setColor(event.getColor());
                eventsRect.add(new EventRect(eventMore, event, null));

                // Add next day.
                otherDay.add(Calendar.DATE, 1);
            }

            // Add last day.
            Calendar startTime = (Calendar) event.getEndTime().clone();
            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            WeekViewEvent event2 = new WeekViewEvent(event.getId(), event.getName(), event.getLocation(), startTime, event.getEndTime());
            event2.setColor(event.getColor());
            eventsRect.add(new EventRect(event2, event));*/
            } else {
                return new EventRect(event, event);
            }
        }

        /**
         * Calculates the left and right positions of each events. This comes handy specially if events
         * are overlapping.
         * @param eventRects The events along with their wrapper class.
         */
        private List<EventRect> computePositionOfEvents(List<EventRect> eventRects) {
            if (eventRects.isEmpty()) {
                return Collections.emptyList();
            }

            List<EventRect> eventRectsSorted = new ArrayList<>();


            // Make "collision groups" for all events that collide with others.
            List<List<EventRect>> collisionGroups = new ArrayList<List<EventRect>>();
            for (EventRect eventRect : eventRects) {
                boolean isPlaced = false;
                outerLoop:
                for (List<EventRect> collisionGroup : collisionGroups) {
                    for (EventRect groupEvent : collisionGroup) {
                        if (isEventsCollide(groupEvent.event, eventRect.event)) {
                            collisionGroup.add(eventRect);
                            isPlaced = true;
                            break outerLoop;
                        }
                    }
                }
                if (!isPlaced) {
                    List<EventRect> newGroup = new ArrayList<EventRect>();
                    newGroup.add(eventRect);
                    collisionGroups.add(newGroup);
                }
            }

            for (List<EventRect> collisionGroup : collisionGroups) {
                eventRectsSorted.addAll(expandEventsToMaxWidth(collisionGroup));
            }

            return eventRectsSorted;
        }

        /**
         * Expands all the events to maximum possible width. The events will try to occupy maximum
         * space available horizontally.
         * @param collisionGroup The group of events which overlap with each other.
         */
        private List<EventRect> expandEventsToMaxWidth(List<EventRect> collisionGroup) {
            if (collisionGroup.isEmpty()) {
                return Collections.emptyList();
            }

            List<EventRect> eventRects = new ArrayList<>();
            // Expand the events to maximum possible width.
            List<List<EventRect>> columns = new ArrayList<>();
            columns.add(new ArrayList<EventRect>());
            for (EventRect eventRect : collisionGroup) {
                boolean isPlaced = false;
                for (List<EventRect> column : columns) {
                    if (column.size() == 0) {
                        column.add(eventRect);
                        isPlaced = true;
                    }
                    else if (!isEventsCollide(eventRect.event, column.get(column.size()-1).event)) {
                        column.add(eventRect);
                        isPlaced = true;
                        break;
                    }
                }
                if (!isPlaced) {
                    List<EventRect> newColumn = new ArrayList<EventRect>();
                    newColumn.add(eventRect);
                    columns.add(newColumn);
                }
            }


            // Calculate left and right position for all the events.
            // Get the maxRowCount by looking in all columns.
            int maxRowCount = 0;
            for (List<EventRect> column : columns){
                maxRowCount = Math.max(maxRowCount, column.size());
            }
            for (int i = 0; i < maxRowCount; i++) {
                // Set the left and right values of the event.
                float j = 0;
                for (List<EventRect> column : columns) {
                    if (column.size() >= i+1) {
                        EventRect eventRect = column.get(i);
                        eventRect.startWidthCoef = j / columns.size();
                        eventRect.endWidthCoef = 1f / columns.size();
                        eventRects.add(eventRect);
                    }
                    j++;
                }
            }

            return eventRects;
        }

        /**
         * Checks if two events overlap.
         * @param event1 The first event.
         * @param event2 The second event.
         * @return true if the events overlap.
         */
        private boolean isEventsCollide(CalendarEvent event1, CalendarEvent event2) {
            long start1 = event1.getStartMillis();
            long end1 = event1.getEndMillis();
            long start2 = event2.getStartMillis();
            long end2 = event2.getEndMillis();
            return !((start1 >= end2) || (end1 <= start2));
        }
    }
}
