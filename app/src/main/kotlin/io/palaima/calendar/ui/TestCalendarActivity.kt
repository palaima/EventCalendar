package io.palaima.calendar.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import butterknife.ButterKnife
import io.palaima.calendar.R
import io.palaima.calendar.ui.CalendarActivity
import io.palaima.calendar.ui.ChartActivity
import io.palaima.debugdrawer.DebugDrawer
import io.palaima.debugdrawer.actions.ActionsModule
import io.palaima.debugdrawer.commons.BuildModule
import io.palaima.debugdrawer.commons.DeviceModule
import io.palaima.debugdrawer.commons.NetworkModule
import io.palaima.debugdrawer.commons.SettingsModule
import io.palaima.eventscalendar.CalendarView
import io.palaima.eventscalendar.data.DefaultCategory

class TestCalendarActivity : AppCompatActivity() {

    private var calendarView: CalendarView? = null
    private var bottomSheet: FrameLayout? = null
    private var debugDrawer: DebugDrawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_calendar)

        ButterKnife.bind(this)

        val toolbar = ButterKnife.findById<Toolbar>(this, R.id.toolbar)
        setSupportActionBar(toolbar)

        calendarView = findViewById(R.id.calendar_view) as CalendarView
        bottomSheet = findViewById(R.id.bottom_sheet) as FrameLayout

        calendarView!!.config().minOffset(16f).set()

        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

        behavior.peekHeight = 100

        debugDrawer = DebugDrawer.Builder(this).modules(
                ActionsModule(),
                DeviceModule(this),
                BuildModule(this),
                NetworkModule(this),
                SettingsModule(this)).build()
    }

    override fun onStart() {
        super.onStart()
        debugDrawer!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        debugDrawer!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        debugDrawer!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        debugDrawer!!.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_chart) {
            startActivity(Intent(this, ChartActivity::class.java))
            return true
        } else if (id == R.id.action_calendar) {
            startActivity(Intent(this, CalendarActivity::class.java))
            return true
        } else if (id == R.id.action_toggle_mode) {
            calendarView!!.config().resources().set().categories(listOf(DefaultCategory.INSTANCE))//.hoursRange(8, 12)
                    .mode(if (calendarView!!.config.mode == CalendarView.Mode.DAY) CalendarView.Mode.WEEK else CalendarView.Mode.DAY).set().mergeProperties().pinchZoomEnabled(false).set()
        } else {
            throw IllegalStateException("menu action not supported")
        }

        return super.onOptionsItemSelected(item)
    }
}
