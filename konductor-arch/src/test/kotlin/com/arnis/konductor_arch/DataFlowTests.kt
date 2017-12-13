package com.arnis.konductor_arch

import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/** Created by arnis on 13/12/2017 */

class DataFlowReturnValues: Spek({
    given("dataflow of string") {
        val dataFlow = DataFlow { "Hello" }
        on("flow") {
            var result = ""
            dataFlow.updateUi = {
                result = it
            }
            dataFlow.flow()
            it("should return value inside the lambda") {
                result shouldBeEqualTo "Hello"
            }
        }
    }

    given("dataflow of custom object") {
        val customObject = CustomObject()
        val dataFlow = DataFlow { customObject }
        on("flow") {
            lateinit var result: CustomObject
            dataFlow.updateUi = {
                result = it
            }
            dataFlow.flow()
            it("should return object of type as specified lambda") {
                result `should be instance of` CustomObject::class
            }
        }
    }
})

class CustomObject

//class DataFlowBinding: Spek({
//    given("dataflow") {
//        val dataFlow = DataFlow { "test" }
//        on("bind to view kontroller") {
//            var result = ""
//            val mockKontroller = mock<ViewKontroller>()
//            dataFlow.bindTo(mockKontroller, true, { result = it })
//            Verify on mockKontroller that mockKontroller.addLifecycleListener(any()) was called
//            it("should update data immideately") {
//                result `should be equal to` "test"
//            }
//        }
//    }
//})