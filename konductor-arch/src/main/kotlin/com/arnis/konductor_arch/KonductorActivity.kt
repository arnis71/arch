package com.arnis.konductor_arch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arnis.konductor.ChangeHandlerFrameLayout
import com.arnis.konductor.Router
import com.arnis.konductor.RouterTransaction
import com.arnis.konductor.attachRouter
import com.arnis.konductor_arch.Konductor.Companion.HOME

/** Created by arnis on 07/12/2017 */

abstract class KonductorActivity: AppCompatActivity() {
    private lateinit var router: Router
    abstract val konductor: Konductor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = ChangeHandlerFrameLayout(this)
        setContentView(container)
        router = attachRouter(container, savedInstanceState)
        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(konductor.route(HOME)))
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}