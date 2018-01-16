//package com.arnis.arch
//
//import android.util.ArrayMap
//import kotlin.reflect.KClass
//
///** Created by arnis on 16/01/2018 */
//
//class DataFlowFactory {
//
//    fun <T: DataFlowProvider> addDataFlowProvider(isReusable: Boolean = false, maker: () -> T) {
//
//    }
//
//
//    companion object {
//        internal val map: ArrayMap<KClass<*>, FactoryInstance> = ArrayMap()
//
//        internal inline fun <reified T: DataFlowProvider> get(): T {
//            return map[T::class]?.run {
//                if (instance == null)
//                    instance = maker()
//                instance as T
//            }!!
//        }
//
//    }
//}
//
//class FactoryInstance(val maker: () -> Any, var instance: Any? = null)