package io.palaima.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Collections;

import butterknife.ButterKnife;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ActionsModule;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.eventscalendar.CalendarView;
import io.palaima.eventscalendar.data.DefaultCategory;

public class TestCalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private FrameLayout bottomSheet;
    private DebugDrawer debugDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_calendar);

        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        calendarView = ((CalendarView) findViewById(R.id.calendar_view));
        bottomSheet = ((FrameLayout) findViewById(R.id.bottom_sheet));

        calendarView.config()
            .minOffset(16)
            .set();

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        behavior.setPeekHeight(100);

        debugDrawer = new DebugDrawer.Builder(this)
            .modules(
                new ActionsModule(),
                new DeviceModule(this),
                new BuildModule(this),
                new NetworkModule(this),
                new SettingsModule(this)
            ).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        debugDrawer.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        debugDrawer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        debugDrawer.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        debugDrawer.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chart) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        } else if (id == R.id.action_calendar) {
            startActivity(new Intent(this, CalendarActivity.class));
            return true;
        } else if (id == R.id.action_toggle_mode) {
            calendarView
                .config()
                    .resources()

                    .set()
                    //.hoursRange(8, 12)
                    .categories(Collections.singletonList(DefaultCategory.INSTANCE))
                    .mode(calendarView.getConfig().getMode() == CalendarView.Mode.DAY ? CalendarView.Mode.WEEK : CalendarView.Mode.DAY)
                .set()
                .mergeProperties()
                    .pinchZoomEnabled(false)
                .set();
        } else {
            throw new IllegalStateException("menu action not supported");
        }

        return super.onOptionsItemSelected(item);
    }
}
