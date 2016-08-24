package io.palaima.calendar

import android.app.Application
import io.palaima.calendar.data.Task
import io.palaima.calendar.data.Type
import io.realm.Realm
import io.realm.RealmConfiguration

import timber.log.Timber
import java.util.*

class App : Application() {

    companion object {

        private fun randBetween(start: Int, end: Int): Int {
            return start + Math.round(Math.random() * (end - start)).toInt()
        }
    }

    override fun onCreate() {
        super.onCreate()

        val config = RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build()

        Realm.setDefaultConfiguration(config)
        Realm.deleteRealm(config)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createTestData()
    }

    private fun createTestData() {

        val r = Random(42)
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction { realm ->

            val type = realm.createObject(Type::class.java)
            type.id = r.nextLong()
            type.name = "Home work"

            for (i in 0..25) {
                val gc = GregorianCalendar(2016, 8, 1)
                val day = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH))

                val hour = randBetween(9, 22) //Hours will be displayed in between 9 to 22
                val min = randBetween(0, 59)
                val duration = randBetween(30, 90)

                gc.set(GregorianCalendar.DAY_OF_MONTH, day)
                gc.set(GregorianCalendar.HOUR_OF_DAY, hour)
                gc.set(GregorianCalendar.MINUTE, min)

                val id = r.nextLong()
                val task = realm.createObject(Task::class.java)
                task.id = id
                task.startTime = gc.time
                task.endTime = Date(gc.timeInMillis.plus(duration * 60 * 1000))
                task.title = "task {$id}"
                task.description = "description {$id}"

                type.tasks.add(task)
            }
        }
        realm.close()
    }
}