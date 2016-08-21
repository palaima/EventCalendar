package io.palaima.calendar.data

import android.content.ContentValues
import android.database.Cursor

import com.squareup.sqldelight.ColumnAdapter

import java.util.Calendar

class DateAdapter : ColumnAdapter<Calendar> {
    override fun marshal(contentValues: ContentValues, columnName: String, date: Calendar) {
        contentValues.put(columnName, date.timeInMillis)
    }

    override fun map(cursor: Cursor, columnIndex: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = cursor.getLong(columnIndex)
        return calendar
    }
}
