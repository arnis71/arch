package com.arnis.arch

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.internal.LifecycleHandler

/** Created by arnis on 07/12/2017 */

abstract class KonductorActivity: AppCompatActivity() {
    private lateinit var router: Router

    abstract fun rootController(): ViewKontroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = ChangeHandlerFrameLayout(this)
        setContentView(container)
        router = attachRouter(container, savedInstanceState)
        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(rootController()))
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}

fun Activity.attachRouter(container: ViewGroup, savedInstanceState: Bundle?): Router {
    return LifecycleHandler.install(this).getRouter(container, savedInstanceState).apply {
        rebindIfNeeded()
    }
}