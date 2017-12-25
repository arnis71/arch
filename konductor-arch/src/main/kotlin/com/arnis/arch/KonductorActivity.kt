package com.arnis.arch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arnis.konductor.ChangeHandlerFrameLayout
import com.arnis.konductor.Router
import com.arnis.konductor.RouterTransaction
import com.arnis.konductor.attachRouter
import kotlin.reflect.KClass

/** Created by arnis on 07/12/2017 */

abstract class KonductorActivity: AppCompatActivity() {
    private lateinit var router: Router
    var changeHandler = KonductorChangeHandler()

    abstract fun rootController(): ViewKontroller<*>

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