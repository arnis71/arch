package com.arnis.arch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.arnis.konductor.ChangeHandlerFrameLayout
import com.arnis.konductor.Router
import com.arnis.konductor.RouterTransaction
import com.arnis.konductor.attachRouter
import com.arnis.arch.KonductorChangeHandler.Companion.HOME
import kotlin.reflect.KClass

/** Created by arnis on 07/12/2017 */

abstract class KonductorActivity: AppCompatActivity() {
    private lateinit var router: Router
    internal lateinit var abstractions: (clazz: KClass<*>) -> Abstraction
    var changeHandler = KonductorChangeHandler()
        private set

    abstract fun routing(): ScreenById

    abstract fun <T: Abstraction> abstractionFactory(): (clazz: KClass<*>) -> T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = ChangeHandlerFrameLayout(this)
        setContentView(container)
        router = attachRouter(container, savedInstanceState)
        changeHandler.route = routing()
        abstractions = abstractionFactory()
        if (!router.hasRootController())
            router.setRoot(RouterTransaction.with(changeHandler.route(HOME)))
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}