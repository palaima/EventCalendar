package io.palaima.calendar.ui.controller

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.kennyc.bottomsheet.BottomSheet
import com.kennyc.bottomsheet.BottomSheetListener
import com.rey.material.widget.Spinner
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.palaima.calendar.R
import io.palaima.calendar.data.CalendarCategory
import io.palaima.calendar.data.CalendarTask
import io.palaima.calendar.data.Task
import io.palaima.calendar.data.Type
import io.palaima.calendar.ui.controller.base.BaseController
import io.palaima.eventscalendar.CalendarView
import io.palaima.eventscalendar.DateHelper
import io.palaima.eventscalendar.data.CalendarEvent
import io.realm.Realm
import rx.subscriptions.Subscriptions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class HomeController: BaseController() {

    @BindView(R.id.calendar_view) lateinit var calendarView: CalendarView

    @BindView(R.id.previous_day) lateinit var previousDay: ImageView

    @BindView(R.id.next_day) lateinit var nextDay: ImageView

    @BindView(R.id.currentDate) lateinit var currentDateLabel: TextView

    private var categoriesUpdateSubscription = Subscriptions.empty()

    private val categories = ArrayList<CalendarCategory>()
    private val events = ArrayList<CalendarTask>()

    init {
        setHasOptionsMenu(true)
    }

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.activity_calendar, container)
    }

    override fun bind(view: View) {
        super.bind(view)

        bindActiveDate()

        val realm = Realm.getDefaultInstance()
        categoriesUpdateSubscription = realm.where(Type::class.java)
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
                .subscribe({
                    val (categories, tasks) = it
                    reloadCategories(categories)
                    reloadEvents(tasks)

                }, {
                    Timber.e(it)
                })
    }

    override fun unbind(view: View) {
        super.unbind(view)
        categoriesUpdateSubscription.unsubscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_calendar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calendar_add) {
            calendarOptionsMenu()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @OnClick(R.id.previous_day)
    fun onPreviousDayClick() {
        Timber.d("onPreviousDayClick")
        val date = DateHelper.previousDay(calendarView.config.activeDate)
        calendarView.config().activeDate(date).set()
        bindActiveDate()
    }

    @OnClick(R.id.next_day)
    fun onNextDayClick() {
        Timber.d("onNextDayClick")
        val date = DateHelper.nextDay(calendarView.config.activeDate)
        calendarView.config().activeDate(date).set()
        bindActiveDate()
    }

    @OnClick(R.id.fab)
    fun onFabClick() {
        Timber.d("onFabClick")

    }

    private fun calendarOptionsMenu() {
        BottomSheet.Builder(activity)
                .setSheet(R.menu.menu_calendar_options)
                .setTitle("Actions")
                .setListener(object : BottomSheetListener {
                    override fun onSheetShown() {

                    }

                    override fun onSheetItemSelected(menuItem: MenuItem) {
                        val itemId = menuItem.itemId

                        if (itemId == R.id.action_calendar_add_category) {
                            openAddCategoryDialog()
                        } else if (itemId == R.id.action_calendar_add_event) {
                            openAddEventDialog()
                        } else if (itemId == R.id.action_calendar_remove_category) {
                            openRemoveCategoryDialog()
                        } else if (itemId == R.id.action_calendar_remove_event) {
                            calendarView.config().events(emptyList<CalendarEvent<*>>()).set()
                        } else {
                            throw IllegalStateException("Menu action is not supported")
                        }
                    }

                    override fun onSheetDismissed(i: Int) {

                    }
                })
                .show()
    }

    private fun reloadCategories(categories: List<CalendarCategory>) {
        Timber.d("reloadCategories ${categories.size}")
        this.categories.clear()
        this.categories.addAll(categories)
        this.calendarView.config().categories(categories).set()
    }

    private fun reloadEvents(events: List<CalendarTask>) {
        Timber.d("reloadEvents ${events.size}")
        this.events.clear()
        this.events.addAll(events)
        this.calendarView.config().events(events).set()
    }

    private fun openAddCategoryDialog() {
        MaterialDialog.Builder(activity)
                .cancelable(true)
                .autoDismiss(true)
                .title("Create category")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRangeRes(2, 10, R.color.md_edittext_error)
                .input("Type name", "", false)
                { dialog, input ->
                    Realm.getDefaultInstance().executeTransaction {
                        val type = it.createObject(Type::class.java)
                        type.name = input.toString()
                    }
                }
                .positiveText("Create")
                .negativeText("Cancel")
                .build()
                .show()
    }

    private fun openAddEventDialog() {
        val now = Calendar.getInstance()
        val selectedStartDate = Calendar.getInstance()
        val selectedEndDate = Calendar.getInstance()
        var selectedCategoryId = categories[0].id

        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_event_create, null)
        val nameInput = ButterKnife.findById<EditText>(view, R.id.dialog_create_event_name_input)
        val descriptionInput = ButterKnife.findById<EditText>(view, R.id.dialog_create_event_description_input)
        val dateInput = ButterKnife.findById<TextView>(view, R.id.dialog_create_event_date_input)
        val timeInput = ButterKnife.findById<TextView>(view, R.id.dialog_create_event_time_input)
        val durationSpinner = ButterKnife.findById<Spinner>(view, R.id.dialog_create_event_duration_spinner)
        val categorySpinner = ButterKnife.findById<Spinner>(view, R.id.dialog_create_event_category_spinner)

        val materialDialog = MaterialDialog.Builder(activity)
                .cancelable(true)
                .autoDismiss(true)
                .title("Create event")
                .customView(view, true)
                .onPositive { dialog, which ->
                    Realm.getDefaultInstance().executeTransaction {
                        val task = it.createObject(Task::class.java)
                        task.id = Random().nextLong()
                        task.title = nameInput.text.toString().trim { it <= ' ' }
                        task.description = descriptionInput.text.toString().trim { it <= ' ' }
                        task.startTime = selectedStartDate.time
                        task.endTime = selectedEndDate.time

                        it.where(Type::class.java)
                                .equalTo("id", selectedCategoryId)
                                .findFirst()
                                .tasks.add(task)
                    }
                }
                .positiveText("Create")
                .negativeText("Cancel")
                .build()

        val actionButton = materialDialog.getActionButton(DialogAction.POSITIVE)
        actionButton.isEnabled = false

        nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                actionButton.isEnabled = !s.toString().trim { it <= ' ' }.isEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val simpleTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        dateInput.text = simpleDateFormat.format(selectedStartDate.time)
        timeInput.text = simpleTimeFormat.format(selectedStartDate.time)

        dateInput.setOnClickListener {
            openDatePickerDialog(now, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                selectedStartDate.set(year, monthOfYear, dayOfMonth)
                selectedEndDate.set(year, monthOfYear, dayOfMonth)
                dateInput.text = simpleDateFormat.format(selectedStartDate.time)
                timeInput.text = simpleTimeFormat.format(selectedStartDate.time)
            })
        }

        timeInput.setOnClickListener {
            openTimePickerDialog(now, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                selectedStartDate.set(
                        selectedStartDate.get(Calendar.YEAR),
                        selectedStartDate.get(Calendar.MONTH),
                        selectedStartDate.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute,
                        second)
                dateInput.text = simpleDateFormat.format(selectedStartDate.time)
                timeInput.text = simpleTimeFormat.format(selectedStartDate.time)
            })
        }

        val durations = Arrays.asList(5, 10, 30, 60, 90)
        val durationsTitles = ArrayList<String>()

        val firstDuration = durations[0]
        val hours = firstDuration / 60
        val minutes = firstDuration % 60

        selectedEndDate.set(
                selectedStartDate.get(Calendar.YEAR),
                selectedStartDate.get(Calendar.MONTH),
                selectedStartDate.get(Calendar.DAY_OF_MONTH),
                selectedStartDate.get(Calendar.HOUR_OF_DAY) + hours,
                selectedStartDate.get(Calendar.MINUTE) + minutes,
                selectedStartDate.get(Calendar.SECOND)
        )

        for (duration in durations) {
            durationsTitles.add("$duration min")
        }

        val durationAdapter = ArrayAdapter(activity, R.layout.row_spinner, durationsTitles)
        durationAdapter.setDropDownViewResource(R.layout.row_spinner_dropdown)
        durationSpinner.adapter = durationAdapter
        durationSpinner.setOnItemSelectedListener { parent, view, position, id ->
            val duration = durations[position]

            selectedEndDate.set(
                    selectedStartDate.get(Calendar.YEAR),
                    selectedStartDate.get(Calendar.MONTH),
                    selectedStartDate.get(Calendar.DAY_OF_MONTH),
                    selectedStartDate.get(Calendar.HOUR_OF_DAY) + duration / 60,
                    selectedStartDate.get(Calendar.MINUTE) + duration % 60,
                    selectedStartDate.get(Calendar.SECOND))
        }

        val titles = ArrayList<String?>()

        for (category in categories) {
            titles.add(category.name)
        }

        val categoriesAdapter = ArrayAdapter(activity, R.layout.row_spinner, titles)
        categoriesAdapter.setDropDownViewResource(R.layout.row_spinner_dropdown)
        categorySpinner.adapter = categoriesAdapter
        categorySpinner.setOnItemSelectedListener { parent, view, position, id ->
            selectedCategoryId = categories[position].id
        }

        materialDialog.show()
    }

    private fun openDatePickerDialog(calendar: Calendar, onDateSetListener: DatePickerDialog.OnDateSetListener) {
        val dpd = DatePickerDialog.newInstance(
                onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        dpd.show(activity.fragmentManager, "Datepickerdialog")
    }

    private fun openTimePickerDialog(calendar: Calendar, onTimeSetListener: TimePickerDialog.OnTimeSetListener) {
        val dpd = TimePickerDialog.newInstance(
                onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
        dpd.show(activity.fragmentManager, "TimePickerDialog")
    }

    private fun openRemoveCategoryDialog() {
        val titles = ArrayList<String?>()

        for (category in categories) {
            titles.add(category.name)
        }

        MaterialDialog.Builder(activity)
                .cancelable(true)
                .autoDismiss(true)
                .title("Select category to delete")
                .items(titles)
                .itemsCallbackSingleChoice(-1) { dialog, itemView, which, text ->
                    Realm.getDefaultInstance().executeTransaction {
                        it.where(Type::class.java)
                            .equalTo("id", categories[which].id)
                            .findFirst()
                            .deleteFromRealm()
                    }

                    true
                }
                .positiveText("Delete")
                .negativeText("Cancel")
                .build()
                .show()
    }

    private fun bindActiveDate() {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = simpleDateFormat.format(calendarView.config.activeDate)
        currentDateLabel.text = date
    }

}