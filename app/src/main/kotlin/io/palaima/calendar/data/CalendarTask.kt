package io.palaima.calendar.data

import android.graphics.Canvas
import android.graphics.RectF
import io.palaima.eventscalendar.data.CalendarEvent
import java.util.*

class CalendarTask(startTime: Date, endTime: Date, categoryId: Long) : CalendarEvent<String>("hello world", startTime, endTime, categoryId) {

    override fun onDraw(canvas: Canvas, eventRect: RectF) {

    }

    companion object {

        fun from(task: Task, type: Type): CalendarTask {
            return CalendarTask(task.startTime, task.endTime, type.id)
        }
    }
}
