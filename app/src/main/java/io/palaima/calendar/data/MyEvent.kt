package io.palaima.calendar.data

import android.graphics.Canvas
import android.graphics.RectF
import io.palaima.eventscalendar.data.CalendarEvent
import java.util.*

class MyEvent(startTime: Date, endTime: Date, categoryId: Long) : CalendarEvent<String>("hello world", startTime, endTime, categoryId) {

    override fun onDraw(canvas: Canvas, eventRect: RectF) {

    }

    companion object {

        fun from(eventEntity: EventEntity): MyEvent {
            return MyEvent(Date(eventEntity.startDate()), Date(eventEntity.endDate()), eventEntity.categoryId())
        }
    }
}
