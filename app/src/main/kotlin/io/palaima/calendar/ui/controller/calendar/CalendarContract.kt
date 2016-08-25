package io.palaima.calendar.ui.controller.calendar

import io.palaima.calendar.data.CalendarCategory
import io.palaima.calendar.data.CalendarTask
import io.palaima.calendar.ui.controller.base.MvpView
import io.palaima.calendar.ui.controller.base.rx.RxPresenter
import java.util.*

interface CalendarContract {

    interface View : MvpView {
        fun bindCategories(categories: List<CalendarCategory>)
        fun bindTasks(tasks: List<CalendarTask>)
    }


    abstract class Presenter : RxPresenter<View>() {

        abstract fun addCategory(name: String)

        abstract fun addTask(categoryId: Long, name: String, description: String?, startDate: Date, endDate: Date)

        abstract fun removeCategory(categoryId: Long)
    }
}
