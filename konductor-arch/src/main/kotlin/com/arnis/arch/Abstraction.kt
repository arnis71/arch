package com.arnis.arch

import android.util.ArrayMap
import android.view.View
import kotlin.reflect.KClass

/** Created by arnis on 13/12/2017 */

abstract class Abstraction {
    val providers: ArrayMap<KClass<*>, KontrollerDataFlow<*>> = ArrayMap()

    inline fun <reified T> register(dataFlow: KontrollerDataFlow<T>)
            = providers.put(T::class, dataFlow)

    inline fun <reified T> get(): KontrollerDataFlow<T> {
        return providers.asSequence().filter {
            it.key == T::class
        }.first().value as KontrollerDataFlow<T>
    }

    abstract fun handle(viewId: Int)

    infix fun clicks(view: View) {
        view.setOnClickListener {
            handle(view.id)
        }
    }

    inline fun <reified T> dispatch()
            = providers.asSequence().find { it.key == T::class }!!.value.flow()

    fun clear() = providers.clear()
}