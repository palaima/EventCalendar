package io.palaima.calendar.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, "calendar_events.db", null, DatabaseOpenHelper.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CategoryEntity.CREATE_TABLE)
        db.execSQL(EventEntity.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {

        private val DATABASE_VERSION = 1
    }
}
