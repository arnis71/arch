package com.arnis.arch

import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.changehandler.FadeChangeHandler

/** Created by arnis on 07/12/2017 */

object KonductorChangeHandler {
    var pushHandler: ControllerChangeHandler = FadeChangeHandler()
    var popHandler: ControllerChangeHandler = FadeChangeHandler()
}
