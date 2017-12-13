package com.arnis.konductor_arch

import com.arnis.konductor.Controller
import com.arnis.konductor.changehandler.FadeChangeHandler

/** Created by arnis on 07/12/2017 */

class Konductor(val route: (screen: String) -> Controller) {
    var pushHandler = FadeChangeHandler()
    var popHandler = FadeChangeHandler()

    companion object {
        const val HOME = "home"
    }
}