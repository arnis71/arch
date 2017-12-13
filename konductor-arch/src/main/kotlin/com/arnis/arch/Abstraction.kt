package com.arnis.arch

import android.util.ArrayMap
import android.view.View
import kotlin.reflect.KClass

/** Created by arnis on 13/12/2017 */

abstract class Abstraction {
    val providers: ArrayMap<KClass<*>, DataFlow<*>> = arrayMapOf()

    inline fun <reified T> register(dataFlow: DataFlow<T>)
            = providers.put(T::class, dataFlow)

    inline fun <reified T> get(): DataFlow<T> {
        return providers.asSequence().filter {
            it.key equal T::class
        }.first().value as DataFlow<T>
    }

    abstract fun handle(viewId: Int)

    infix fun clicks(view: View) {
        view.setOnClickListener {
            handle(view.id)
        }
    }

    inline fun <reified T> dispatch()
            = providers.asSequence().find { it.key equal T::class }!!.value.flow()

    fun clear() = providers.clear()
}