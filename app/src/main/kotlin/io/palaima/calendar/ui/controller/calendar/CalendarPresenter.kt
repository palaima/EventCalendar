package io.palaima.calendar.ui.controller.calendar

import io.palaima.calendar.data.CalendarCategory
import io.palaima.calendar.data.CalendarTask
import io.palaima.calendar.data.Task
import io.palaima.calendar.data.Type
import io.palaima.calendar.extention.executeSafe
import io.realm.Realm
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class CalendarPresenter : CalendarContract.Presenter() {

    private var categoriesUpdateSubscription = Subscriptions.empty()

    override fun bind(view: CalendarContract.View) {
        super.bind(view)
        categoriesUpdateSubscription = load()
                .subscribe({
                    val (categories, tasks) = it
                    view().bindCategories(categories)
                    view().bindTasks(tasks)
                }, {
                    Timber.e(it)
                })
    }

    override fun unbind(view: CalendarContract.View) {
        super.unbind(view)
    }

    override fun destroy() {
        categoriesUpdateSubscription.unsubscribe()
        super.destroy()
    }

    override fun removeCategory(categoryId: Long) {
        Realm.getDefaultInstance().executeSafe {
            where(Type::class.java)
                    .equalTo("id", categoryId)
                    .findFirst()
                    .deleteFromRealm()
        }
    }

    override fun addTask(categoryId: Long, name: String, description: String?, startDate: Date, endDate: Date) {
        Realm.getDefaultInstance().executeSafe {
            val task = createObject(Task::class.java)
            task.id = Random().nextLong()
            task.title = name
            task.description = description
            task.startTime = startDate
            task.endTime = endDate

            where(Type::class.java)
                    .equalTo("id", categoryId)
                    .findFirst()
                    .tasks.add(task)
        }
    }

    override fun addCategory(name: String) {
        Realm.getDefaultInstance().executeSafe {
            val type = createObject(Type::class.java)
            type.id = Random().nextLong()
            type.name = name
        }
    }

    override fun reload() {
        categoriesUpdateSubscription = load()
            .debounce(10, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val (categories, tasks) = it
                view().bindCategories(categories)
                view().bindTasks(tasks)
            }, {
                Timber.e(it)
            })
    }

    fun load(): Observable<Pair<List<CalendarCategory>, List<CalendarTask>>> {
        return Realm.getDefaultInstance().where(Type::class.java)
                .findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .map {
                    val categories = mutableListOf<CalendarCategory>()
                    val tasks = mutableListOf<CalendarTask>()
                    it.forEach {
                        val type = it
                        categories.add(CalendarCategory.from(type))
                        it.tasks.forEach {
                            tasks.add(CalendarTask.from(it, type))
                        }
                    }

                    Pair(categories.toList(), tasks.toList())
                }
    }

}
