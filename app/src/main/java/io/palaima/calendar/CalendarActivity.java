package io.palaima.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Collections;

import io.palaima.eventscalendar.CalendarView;
import io.palaima.eventscalendar.data.DefaultCategory;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private FrameLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = ((CalendarView) findViewById(R.id.calendar_view));
        bottomSheet = ((FrameLayout) findViewById(R.id.bottom_sheet));

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
                .set();
        } else {
            throw new IllegalStateException("menu action not supported");
        }

        return super.onOptionsItemSelected(item);
    }
}
