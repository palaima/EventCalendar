package io.palaima.calendar.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import io.palaima.calendar.R
import io.palaima.calendar.ui.controller.HomeController

class MainActivity: AppCompatActivity() {

    @BindView(R.id.controller_container) lateinit var container: ViewGroup
    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(HomeController()))
        }
    }

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }
}