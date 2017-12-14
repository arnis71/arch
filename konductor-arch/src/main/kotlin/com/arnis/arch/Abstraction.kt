package com.arnis.arch

import android.util.ArrayMap
import android.view.View
import com.arnis.common.arrayMapOf
import com.arnis.common.equal
import kotlin.reflect.KClass

/** Created by arnis on 13/12/2017 */

abstract class Abstraction {
    val providers: ArrayMap<KClass<*>, KontrollerDataFlow<*>> = arrayMapOf()

    inline fun <reified T> register(dataFlow: KontrollerDataFlow<T>)
            = providers.put(T::class, dataFlow)

    inline fun <reified T> get(): KontrollerDataFlow<T> {
        return providers.asSequence().filter {
            it.key equal T::class
        }.first().value as KontrollerDataFlow<T>
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