package io.palaima.calendar.data;

import com.google.auto.value.AutoValue;

import java.util.Calendar;

@AutoValue
public abstract class EventEntity implements EventEntityModel {

    private static final DateAdapter DATE_ADAPTER = new DateAdapter();

    public static final Mapper<EventEntity> MAPPER = new Mapper<>(new Mapper.Creator<EventEntity>() {
        @Override public EventEntity create(long _id, String title, String description, Calendar startDate, Calendar endDate) {
            return new AutoValue_EventEntity(_id, title, description, startDate, endDate);
        }
    }, DATE_ADAPTER, DATE_ADAPTER);


    public static final class Marshal extends EventEntityMarshal<Marshal> {
        public Marshal() {
            super(DATE_ADAPTER, DATE_ADAPTER);
        }
    }
}
