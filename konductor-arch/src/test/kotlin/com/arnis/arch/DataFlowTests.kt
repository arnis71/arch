package com.arnis.arch

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/** Created by arnis on 13/12/2017 */

class DataFlowBasics : Spek({
    given("dataflow of string") {
        val dataFlow = DataFlow { "Hello" }
        on("flow") {
            var result = ""
            dataFlow.receiver = { result = it }
            dataFlow.flow(null)
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
            dataFlow.receiver = { result = it }
            dataFlow.flow(null)
            it("should return object of type as specified lambda") {
                result `should be instance of` CustomObject::class
            }
        }
    }

    given("dataflow with params") {
        var result = ""
        val dataFlow = DataFlow {
            result = it as String
            "Hi"
        }
        on("flow with params") {
            val param = "test"
            dataFlow.receiver = { }
            dataFlow.flow(param)
            it("should pass in the params") {
                result `should be equal to` param
            }
        }
    }
})

//class DeferredDataFLowTests: Spek({
//    given("deferred data flow") {
//        val dataFlow = DeferredDataFlow { async(UI) { "test" }.await() }
//        on("flow") {
//            var result = ""
//            dataFlow.receiver = { result = it }
//            dataFlow.flow(null)
//            it("should return value inside the lambda") {
//                result shouldBeEqualTo "test"
//            }
//        }
//    }
//})

class CustomObject