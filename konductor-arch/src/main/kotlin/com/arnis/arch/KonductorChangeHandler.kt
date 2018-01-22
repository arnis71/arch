package com.arnis.arch

import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler

/** Created by arnis on 07/12/2017 */

object KonductorChangeHandler {
    var pushHandler: ControllerChangeHandler = FadeChangeHandler()
    var popHandler: ControllerChangeHandler = FadeChangeHandler()
}
