package io.palaima.calendar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.palaima.calendar.data.CategoryEntity;
import io.palaima.calendar.data.DatabaseOpenHelper;
import io.palaima.calendar.data.MyCategory;
import io.palaima.eventscalendar.CalendarView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class CalendarActivity extends AppCompatActivity {

    @Bind(R.id.calendar_view) CalendarView calendarView;

    private BriteDatabase database;
    private Subscription categoriesUpdateSubscription = Subscriptions.empty();

    private List<MyCategory> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        SqlBrite sqlBrite = SqlBrite.create();
        database = sqlBrite.wrapDatabaseHelper(new DatabaseOpenHelper(this), Schedulers.io());

        ButterKnife.bind(this);

        QueryObservable query = database.createQuery(CategoryEntity.TABLE_NAME, CategoryEntity.SELECT_ALL);
        categoriesUpdateSubscription = query
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

                    } else if (itemId == R.id.action_calendar_remove_category) {
                        openRemoveCategoryDialog();
                    } else if (itemId == R.id.action_calendar_remove_event) {

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
}
