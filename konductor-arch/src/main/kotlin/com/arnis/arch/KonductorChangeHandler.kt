package com.arnis.arch

import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.changehandler.FadeChangeHandler

/** Created by arnis on 07/12/2017 */

class KonductorChangeHandler {
    var route: ScreenById = { throw UnsupportedOperationException("Routing is not defined") }
        internal set
    var pushHandler: ControllerChangeHandler = FadeChangeHandler()
    var popHandler: ControllerChangeHandler = FadeChangeHandler()

    companion object {
        const val HOME = "home"
    }
}

typealias ScreenById = (id: String) -> Controller