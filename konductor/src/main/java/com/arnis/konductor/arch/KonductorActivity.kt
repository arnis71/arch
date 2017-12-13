package com.arnis.konductor.arch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arnis.konductor.ChangeHandlerFrameLayout
import com.arnis.konductor.Router
import com.arnis.konductor.RouterTransaction
import com.arnis.konductor.attachRouter
import com.arnis.konductor.internal.LifecycleHandler

/** Created by arnis on 11/12/2017 */

abstract class KonductorActivity: AppCompatActivity() {
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = ChangeHandlerFrameLayout(this)
        setContentView(container)
        router = attachRouter(container, savedInstanceState)
//        if (!router.hasRootController())
//            router.setRoot(RouterTransaction.with(konductor.route(HOME)))
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}