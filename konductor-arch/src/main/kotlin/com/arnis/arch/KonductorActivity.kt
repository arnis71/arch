package com.arnis.arch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arnis.konductor.ChangeHandlerFrameLayout
import com.arnis.konductor.Router
import com.arnis.konductor.RouterTransaction
import com.arnis.konductor.attachRouter
import com.arnis.arch.KonductorChangeHandler.Companion.HOME

/** Created by arnis on 07/12/2017 */

abstract class KonductorActivity: AppCompatActivity() {
    private lateinit var router: Router
    var changeHandler = KonductorChangeHandler()
        private set

    abstract fun routing(): ScreenById

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = ChangeHandlerFrameLayout(this)
        setContentView(container)
        router = attachRouter(container, savedInstanceState)
        changeHandler.route = routing()
        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(changeHandler.route(HOME)))
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}