package com.arnis.arch

import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.changehandler.FadeChangeHandler

/** Created by arnis on 07/12/2017 */

class KonductorChangeHandler(val pushHandler: ControllerChangeHandler = FadeChangeHandler(),
                             val popHandler: ControllerChangeHandler = FadeChangeHandler())