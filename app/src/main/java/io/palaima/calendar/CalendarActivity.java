package io.palaima.calendar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.rey.material.widget.Spinner;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.palaima.calendar.data.CategoryEntity;
import io.palaima.calendar.data.DatabaseOpenHelper;
import io.palaima.calendar.data.EventEntity;
import io.palaima.calendar.data.MyCategory;
import io.palaima.calendar.data.MyEvent;
import io.palaima.eventscalendar.CalendarView;
import io.palaima.eventscalendar.DateHelper;
import io.palaima.eventscalendar.data.CalendarEvent;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class CalendarActivity extends AppCompatActivity {

    @Bind(R.id.calendar_view) CalendarView calendarView;
    @Bind(R.id.previous_day) ImageView previousDay;
    @Bind(R.id.next_day) ImageView nextDay;
    @Bind(R.id.currentDate) TextView currentDateLabel;

    private BriteDatabase database;
    private Subscription categoriesUpdateSubscription = Subscriptions.empty();

    private List<MyCategory> categories = new ArrayList<>();
    private List<MyEvent> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        SqlBrite sqlBrite = SqlBrite.create();
        database = sqlBrite.wrapDatabaseHelper(new DatabaseOpenHelper(this), Schedulers.io());

        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        bindActiveDate();

        QueryObservable categoriesQuery = database.createQuery(CategoryEntity.TABLE_NAME, CategoryEntity.SELECT_ALL);
        categoriesUpdateSubscription = categoriesQuery
            .map(new Func1<SqlBrite.Query, List<MyCategory>>() {
                @Override public List<MyCategory> call(SqlBrite.Query query) {
                    Cursor cursor = query.run();
                    List<MyCategory> entities = new ArrayList<>();
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            entities.add(MyCategory.from(CategoryEntity.MAPPER.map(cursor)));
                        }
                    }
                    return entities;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<MyCategory>>() {
                @Override public void call(List<MyCategory> categories) {
                    reloadCategories(categories);
                }
            });

        QueryObservable eventsQuery = database.createQuery(EventEntity.TABLE_NAME, EventEntity.SELECT_ALL);

        categoriesUpdateSubscription = eventsQuery
            .map(new Func1<SqlBrite.Query, List<MyEvent>>() {
                @Override public List<MyEvent> call(SqlBrite.Query query) {
                    Cursor cursor = query.run();
                    List<MyEvent> entities = new ArrayList<>();
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            entities.add(MyEvent.from(EventEntity.MAPPER.map(cursor)));
                        }
                    }
                    return entities;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<MyEvent>>() {
                @Override public void call(List<MyEvent> events) {
                    reloadEvents(events);
                }
            });
    }

    @Override protected void onDestroy() {
        categoriesUpdateSubscription.unsubscribe();
        super.onDestroy();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calendar_add) {
            calendarOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.previous_day)
    public void onPreviousDayClick() {
        Timber.d("onPreviousDayClick");
        Date date = DateHelper.previousDay(calendarView.getConfig().getActiveDate());
        calendarView.config().activeDate(date).set();
        bindActiveDate();
    }

    @OnClick(R.id.next_day)
    public void onNextDayClick() {
        Timber.d("onNextDayClick");
        Date date = DateHelper.nextDay(calendarView.getConfig().getActiveDate());
        calendarView.config().activeDate(date).set();
        bindActiveDate();
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        Timber.d("onFabClick");

    }

    public void calendarOptionsMenu() {
        new BottomSheet.Builder(this)
            .setSheet(R.menu.menu_calendar_options)
            .setTitle("Actions")
            .setListener(new BottomSheetListener() {
                @Override public void onSheetShown() {

                }

                @Override public void onSheetItemSelected(MenuItem menuItem) {
                    int itemId = menuItem.getItemId();

                    if (itemId == R.id.action_calendar_add_category) {
                        openAddCategoryDialog();
                    } else if (itemId == R.id.action_calendar_add_event) {
                        openAddEventDialog();
                    } else if (itemId == R.id.action_calendar_remove_category) {
                        openRemoveCategoryDialog();
                    } else if (itemId == R.id.action_calendar_remove_event) {
                        calendarView.config()
                            .events(Collections.<CalendarEvent>emptyList())
                            .set();
                    } else {
                        throw new IllegalStateException("Menu action is not supported");
                    }
                }

                @Override public void onSheetDismissed(int i) {

                }
            })
            .show();
    }

    private void reloadCategories(List<MyCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        this.calendarView.config()
            .categories(categories)
            .set();
    }

    private void reloadEvents(List<MyEvent> events) {
        this.events.clear();
        this.events.addAll(events);
        this.calendarView.config()
            .events(events)
            .set();
    }

    private void openAddCategoryDialog() {
        new MaterialDialog.Builder(this)
            .cancelable(true)
            .autoDismiss(true)
            .title("Create category")
            .inputType(InputType.TYPE_CLASS_TEXT)
            .inputRangeRes(2, 10, R.color.md_edittext_error)
            .input("Category name", "", false, new MaterialDialog.InputCallback() {
                @Override public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                    database.insert(CategoryEntity.TABLE_NAME, new CategoryEntity.Marshal()
                        .name(input.toString())
                        .asContentValues());
                }
            })
            .positiveText("Create")
            .negativeText("Cancel")
            .build()
            .show();
    }

    private void openAddEventDialog() {
        final Calendar now = Calendar.getInstance();
        final Calendar selectedStartDate = Calendar.getInstance();
        final Calendar selectedEndDate = Calendar.getInstance();
        final EventEntity.Marshal eventMarshal = new EventEntity.Marshal();

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_event_create, null);

        final EditText nameInput = ButterKnife.findById(view, R.id.dialog_create_event_name_input);
        final EditText descriptionInput = ButterKnife.findById(view, R.id.dialog_create_event_description_input);
        final TextView dateInput = ButterKnife.findById(view, R.id.dialog_create_event_date_input);
        final TextView timeInput = ButterKnife.findById(view, R.id.dialog_create_event_time_input);
        final Spinner durationSpinner = ButterKnife.findById(view, R.id.dialog_create_event_duration_spinner);
        final Spinner categorySpinner = ButterKnife.findById(view, R.id.dialog_create_event_category_spinner);

        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
            .cancelable(true)
            .autoDismiss(true)
            .title("Create event")
            .customView(view, true)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    String name = nameInput.getText().toString().trim();

                    database.insert(EventEntity.TABLE_NAME, eventMarshal
                        .title(name)
                        .description(descriptionInput.getText().toString().trim())
                        .startDate(selectedStartDate)
                        .endDate(selectedEndDate)
                        .asContentValues());
                }
            })
            .positiveText("Create")
            .negativeText("Cancel")
            .build();

        final MDButton actionButton = materialDialog.getActionButton(DialogAction.POSITIVE);
        actionButton.setEnabled(false);

        nameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                actionButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        final SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        dateInput.setText(simpleDateFormat.format(selectedStartDate.getTime()));
        timeInput.setText(simpleTimeFormat.format(selectedStartDate.getTime()));



        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                openDatePickerDialog(now, new DatePickerDialog.OnDateSetListener() {
                    @Override public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        selectedStartDate.set(year, monthOfYear, dayOfMonth);
                        selectedEndDate.set(year, monthOfYear, dayOfMonth);
                        dateInput.setText(simpleDateFormat.format(selectedStartDate.getTime()));
                        timeInput.setText(simpleTimeFormat.format(selectedStartDate.getTime()));
                    }
                });
            }
        });

        timeInput.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                openTimePickerDialog(now, new TimePickerDialog.OnTimeSetListener() {
                    @Override public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                        selectedStartDate.set(
                            selectedStartDate.get(Calendar.YEAR),
                            selectedStartDate.get(Calendar.MONTH),
                            selectedStartDate.get(Calendar.DAY_OF_MONTH),
                            hourOfDay,
                            minute,
                            second
                        );
                        dateInput.setText(simpleDateFormat.format(selectedStartDate.getTime()));
                        timeInput.setText(simpleTimeFormat.format(selectedStartDate.getTime()));
                    }
                });
            }
        });


        final List<Integer> durations = Arrays.asList(5, 10, 30, 60, 90);
        final List<String> durationsTitles = new ArrayList<>();

        int firstDuration = durations.get(0);
        int hours = firstDuration / 60;
        int minutes = firstDuration % 60;

        selectedEndDate.set(
            selectedStartDate.get(Calendar.YEAR),
            selectedStartDate.get(Calendar.MONTH),
            selectedStartDate.get(Calendar.DAY_OF_MONTH),
            selectedStartDate.get(Calendar.HOUR_OF_DAY) + hours,
            selectedStartDate.get(Calendar.MINUTE) + minutes,
            selectedStartDate.get(Calendar.SECOND)
        );

        eventMarshal.startDate(selectedStartDate);
        eventMarshal.endDate(selectedStartDate);


        for (int duration : durations) {
            durationsTitles.add(duration + " min");
        }

        final ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, R.layout.row_spinner, durationsTitles);
        durationAdapter.setDropDownViewResource(R.layout.row_spinner_dropdown);
        durationSpinner.setAdapter(durationAdapter);
        durationSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override public void onItemSelected(Spinner parent, View view, int position, long id) {
                int duration = durations.get(position);
                int hours = duration / 60;
                int minutes = duration % 60;

                selectedEndDate.set(
                    selectedStartDate.get(Calendar.YEAR),
                    selectedStartDate.get(Calendar.MONTH),
                    selectedStartDate.get(Calendar.DAY_OF_MONTH),
                    selectedStartDate.get(Calendar.HOUR_OF_DAY) + hours,
                    selectedStartDate.get(Calendar.MINUTE) + minutes,
                    selectedStartDate.get(Calendar.SECOND)
                );
            }
        });

        final List<String> titles = new ArrayList<>();

        for (MyCategory category : categories) {
            titles.add(category.getName());
        }

        eventMarshal.categoryId(categories.get(0).getId());

        final ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, R.layout.row_spinner, titles);
        categoriesAdapter.setDropDownViewResource(R.layout.row_spinner_dropdown);
        categorySpinner.setAdapter(categoriesAdapter);
        categorySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override public void onItemSelected(Spinner parent, View view, int position, long id) {
                eventMarshal.categoryId(categories.get(position).getId());
            }
        });


        materialDialog.show();
    }

    private void openDatePickerDialog(Calendar calendar, DatePickerDialog.OnDateSetListener onDateSetListener) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
            onDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    private void openTimePickerDialog(Calendar calendar, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        TimePickerDialog dpd = TimePickerDialog.newInstance(
            onTimeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        dpd.show(getFragmentManager(), "TimePickerDialog");
    }

    private void openRemoveCategoryDialog() {
        List<String> titles = new ArrayList<>();

        for (MyCategory category : categories) {
            titles.add(category.getName());
        }

        new MaterialDialog.Builder(this)
            .cancelable(true)
            .autoDismiss(true)
            .title("Select category to delete")
            .items(titles)
            .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                @Override public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                    database.delete(CategoryEntity.TABLE_NAME, CategoryEntity._ID + " = " + categories.get(which).getId());
                    return true;
                }
            })
            .positiveText("Delete")
            .negativeText("Cancel")
            .build()
            .show();
    }

    private void bindActiveDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(calendarView.getConfig().getActiveDate());
        currentDateLabel.setText(date);
    }
}
