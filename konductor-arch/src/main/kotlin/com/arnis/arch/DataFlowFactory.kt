package com.arnis.arch

import android.util.ArrayMap
import kotlin.reflect.KClass

/** Created by arnis on 16/01/2018 */

internal object DataFlowFactory {
    private val map: ArrayMap<KClass<*>, FactoryInstance> = ArrayMap()

    internal fun <T: DataFlowProvider> get(clazz: KClass<T>): T {
        return map[clazz]?.run {
            if (instance == null)
                instance = maker()
            instance as T
        } ?: throw Exception("no provider found for class $clazz")
    }
}
internal class FactoryInstance(val maker: () -> Any, var instance: Any? = null)