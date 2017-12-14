package com.arnis.arch

import org.amshove.kluent.Verify
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should not be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.called
import org.amshove.kluent.on
import org.amshove.kluent.should
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/** Created by arnis on 13/12/2017 */

class DataFlowReturnValues: Spek({
    given("dataflow of string") {
        val dataFlow = TestDataFlow { "Hello" }
        on("flow") {
            var result = ""
            dataFlow.onFlow { result = it }
            dataFlow.flow()
            it("should return value inside the lambda") {
                result shouldBeEqualTo "Hello"
            }
        }
    }

    given("dataflow of custom object") {
        val customObject = CustomObject()
        val dataFlow = TestDataFlow { customObject }
        on("flow") {
            lateinit var result: CustomObject
            dataFlow.onFlow { result = it }
            dataFlow.flow()
            it("should return object of type as specified lambda") {
                result `should be instance of` CustomObject::class
            }
        }
    }
})

class TestDataFlow<out T>(producer: DataFlowProducer<T>): BaseDataFlow<T>(producer)

class CustomObject

class DataFlowBinding: Spek({
    given("kontroller dataflow") {
        var result = "init"
        val dataFlow = KontrollerDataFlow { "test" }
        dataFlow.onFlow { result = it }

        on("bind to view kontroller with updateOnAttach set to false") {
            dataFlow.updateOnAttach = false
            it("should not update after attach") {
                dataFlow.attach()
                result `should be equal to` "init"
            }
        }
        on("bind to view kontroller with updateOnAttach set to true") {
            dataFlow.updateOnAttach = true
            it("should update after attach") {
                dataFlow.attach()
                result `should be equal to` "test"
            }
        }
        on("unbind from view kontroller") {
            dataFlow.stop()
            it("should throw NPE") {
                { dataFlow.flow() } `should throw` KotlinNullPointerException::class
            }
        }
    }
})