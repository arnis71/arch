@file:JvmName("Konductor")
package com.arnis.konductor

import android.app.Activity
import android.os.Bundle
import android.support.annotation.UiThread
import android.view.ViewGroup
import com.arnis.konductor.internal.LifecycleHandler

/**
 * Point of initial interaction with Konductor. Used to attach a [Router] to your Activity.
 */
//object Konductor {
//
//    /**
//     * Konductor will create a [Router] that has been initialized for your Activity and containing ViewGroup.
//     * If an existing [Router] is already associated with this Activity/ViewGroup pair, either in memory
//     * or in the savedInstanceState, that router will be used and rebound instead of creating a new one with
//     * an empty backstack.
//     *
//     * @param activity The Activity that will host the [Router] being attached.
//     * @param container The ViewGroup in which the [Router]'s [Controller] views will be hosted
//     * @param savedInstanceState The savedInstanceState passed into the hosting Activity's onCreate method. Used
//     * for restoring the Router's state if possible.
//     * @return A fully configured [Router] instance for use with this Activity/ViewGroup pair.
//     */
//    @UiThread
//    @Deprecated("", ReplaceWith("activity.attachRouter(container, savedInstanceState)"))
//    fun attachRouter(activity: Activity, container: ViewGroup, savedInstanceState: Bundle?): Router {
//        return activity.attachRouter2(container, savedInstanceState)
//    }
//}


fun Activity.attachRouter(container: ViewGroup, savedInstanceState: Bundle?): Router {
    return LifecycleHandler.install(this).getRouter(container, savedInstanceState).apply {
        rebindIfNeeded()
    }
}
