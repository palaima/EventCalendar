package io.palaima.eventscalendar.data;

/**
 * A class to hold reference to the events and their visual representation. An EventRect is
 * actually the rectangle that is drawn on the calendar for a given event. There may be more
 * than one rectangle for a single event (an event that expands more than one day). In that
 * case two instances of the EventRect will be used for a single event. The given event will be
 * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
 * instance will be stored in "event".
 */
public class EventRect {
    public final CalendarEvent event;
    public final CalendarEvent originalEvent;
    public float startWidthCoef;
    public float endWidthCoef;

    /**
     * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
     * on the calendar for a given event. There may be more than one rectangle for a single
     * event (an event that expands more than one day). In that case two instances of the
     * EventRect will be used for a single event. The given event will be stored in
     * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
     * be stored in "event".
     * @param event Represents the event which this instance of rectangle represents.
     * @param originalEvent The original event that was passed by the user.
     */
    public EventRect(
        CalendarEvent event,
        CalendarEvent originalEvent
    ) {
        this.event = event;
        this.originalEvent = originalEvent;
    }

    @Override public String toString() {
        return "EventRect{" +
            "event=" + event +
            ", originalEvent=" + originalEvent +
            ", startWidthCoef=" + startWidthCoef +
            ", endWidthCoef=" + endWidthCoef +
            '}';
    }
}
