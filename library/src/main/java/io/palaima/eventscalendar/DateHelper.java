package io.palaima.eventscalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateHelper {

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return isSameDay(calendar1, calendar2);
    }

    public static Date startOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return startOfTheDay(calendar).getTime();
    }

    public static Date endOfTheDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return endOfTheDay(calendar).getTime();
    }

    public static Calendar copy(Calendar calendar) {
        Calendar instance = Calendar.getInstance(calendar.getTimeZone());
        instance.setTime(calendar.getTime());
        return instance;
    }

    public static boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
            && calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    public static Calendar startOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar endOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }


    public static boolean isToday(Calendar calendar) {
        return isSameDay(calendar, Calendar.getInstance());
    }

    public static Date previousDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return previousDay(calendar).getTime();
    }

    public static Calendar previousDay(Calendar calendar) {
        return startOfTheDay(addTime(calendar, -1, TimeUnit.DAYS));
    }

    public static Date nextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return nextDay(calendar).getTime();
    }

    public static Calendar nextDay(Calendar calendar) {
        return startOfTheDay(addTime(calendar, 1, TimeUnit.DAYS));
    }

    /**
     * Given a <code>Calendar</code>, adds the given amount of time to the
     * calendar. If the given amount of time is negative, it subtracts the time,
     * producing a date earlier than the given date.
     *
     * @param cal      - The Calendar to add time to.
     * @param amount   - The amount of time to add, in the time unit specified in
     *                 <code>timeUnit</code>.
     * @param timeUnit - The unit of time to add.
     * @return A Calendar with the new date/time.
     */
    public static Calendar addTime(Calendar cal, int amount, TimeUnit timeUnit) {
        int field = Calendar.DAY_OF_MONTH;
        switch (timeUnit) {
            case MILLISECONDS:
                field = Calendar.MILLISECOND;
                break;
                //cal.setTimeInMillis(cal.getTimeInMillis() + amount);
                //return cal;
            case SECONDS:
                field = Calendar.SECOND;
                break;
                //long secondsToAdd = cal.getTimeInMillis() + amount * TimeUnit.SECONDS.toMillis(1);
                //cal.setTimeInMillis(secondsToAdd);
                //return cal;
            case MINUTES:
                field = Calendar.MINUTE;
                break;
                //long minsToAdd = cal.getTimeInMillis() + amount * TimeUnit.MINUTES.toMillis(1);
                //cal.setTimeInMillis(minsToAdd);
                //return cal;
            case HOURS:
                field = Calendar.HOUR;
                break;
                //long hoursToAdd = cal.getTimeInMillis() + amount * TimeUnit.HOURS.toMillis(1);
                //cal.setTimeInMillis(hoursToAdd);
                //return cal;
            case DAYS:
                field = Calendar.DAY_OF_MONTH;
                break;
                //long daysToAdd = cal.getTimeInMillis() + amount * TimeUnit.DAYS.toMillis(1);
                //cal.setTimeInMillis(daysToAdd);
                //return cal;
        }

        cal.add(field, amount);
        return cal;
    }
}
