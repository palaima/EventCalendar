package io.palaima.eventscalendar;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.palaima.eventscalendar.data.CalendarEvent;
import io.palaima.eventscalendar.data.Category;
import io.palaima.eventscalendar.data.DefaultCategory;

public class Config {

    private ResourcesHolder resourcesHolder;

    private CalendarView.Mode mode = CalendarView.Mode.DAY;

    /**
     * Sets the minimum offset (padding) around the chart, defaults to 15
     */
    private float minOffset = 16f;

    /**
     * Extra offsets to be appended to the viewport
     */
    private float extraTopOffset = 0.f,
        extraRightOffset = 0.f,
        extraBottomOffset = 0.f,
        extraLeftOffset = 0.f;

    private boolean drawGridBackground = true;

    private boolean drawBorders = true;

    private List<CalendarEvent> calendarEvents = new ArrayList<>();

    private List<? extends Category> categories = Collections.singletonList(DefaultCategory.INSTANCE);

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

    public List<? extends Category> getCategories() {
        return categories;
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

    public List<CalendarEvent> getCalendarEvents() {
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
        private List<CalendarEvent> calendarEvents;
        private List<? extends Category> categories;
        private String noDataText;
        private String noDataTextDescription;
        private ResourcesHolder resourcesHolder;


        public Builder(@NonNull CalendarView calendarView, @NonNull Config config) {
            this.calendarView = calendarView;
            this.config = config;

            mode = config.mode;
            categories = config.categories;
            startHour = config.startHour;
            endHour = config.endHour;
            minOffset = config.minOffset;
            extraLeftOffset = config.extraLeftOffset;
            extraRightOffset = config.extraRightOffset;
            extraTopOffset = config.extraTopOffset;
            extraBottomOffset = config.extraBottomOffset;
            drawGridBackground = config.drawGridBackground;
            drawBorders = config.drawBorders;
            calendarEvents = config.calendarEvents;
            noDataText = config.noDataText;
            noDataTextDescription = config.noDataTextDescription;
            resourcesHolder = config.resourcesHolder;
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

        public Config.Builder events(List<CalendarEvent> events) {
            if (events == null) {
                events = new ArrayList<>();
            } else {
                events = new ArrayList<>(events);
            }
            this.calendarEvents = events;
            return this;
        }

        public Config.Builder addEvents(List<? extends CalendarEvent> events) {
            if (events == null) {
                events = new ArrayList<>();
            }
            this.calendarEvents.addAll(events);
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
    }
}
