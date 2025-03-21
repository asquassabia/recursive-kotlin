package flib.rs

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import org.xrpn.flib.ERR_BY_TAG
import org.xrpn.flib.ERR_TAG
import org.xrpn.flib.adt.FWK
import org.xrpn.flib.adt.FWrit
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.api.FLibEvaluationException
import org.xrpn.flib.api.andThen
import org.xrpn.flib.api.bind
import org.xrpn.flib.api.toFWriter
import org.xrpn.flib.api.fwStartValue
import org.xrpn.flib.api.startValueMsg
import org.xrpn.flib.internal.impl.FWBuilder.Companion.FWInitial
import org.xrpn.flib.internal.impl.FWBuilder.Companion.FWLNext
import org.xrpn.flib.internal.impl.FWBuilder.Companion.FWTT
import org.xrpn.flib.internal.impl.FWBuilder.Companion.traceHeader
import org.xrpn.flib.internal.impl.FWBuilder.Companion.unexpectedTerminationMsg
import java.io.ByteArrayOutputStream
import java.io.OutputStream


class FWriterApiTest : ExpectSpec({

    val c2code: (Char) -> Int = { c: Char -> c.code }
    fun i2x(i: Int) = "x" + i.toString(16)
    val i2hex: (Int) -> String = { i: Int -> i2x(i) }
    // msg templates
    val kw0msg = "'a' is start value"
    val kw1msg = "'a' to ${'a'.code}" // "'a' to 97"
    val kw2msg = "${'a'.code} to ${i2x('a'.code)}" // "97 to x61"
    val cnFwrtLambda = "FWriterApiKt$\$Lambda"

    context("constructor") {

        expect("toFWriter") {
            val kwf = c2code.toFWriter(kw1msg)
            kwf::class.simpleName!! shouldStartWith cnFwrtLambda

            val kw = kwf('a')
            kw::class.simpleName!! shouldBe FWLNext::class.simpleName!!

            val kwfx = kw.fix()
            kwfx::class.simpleName!! shouldBe FWLNext::class.simpleName!!

            (kw === kwfx) shouldBe true

            kwfx.item shouldBe 97
            kwfx.msg shouldBe kw1msg
        }

        expect("start value") {
            val kw0a = fwStartValue('a')
            kw0a.toString() shouldBe "FWrtMsg(item=*, msg='start value')"
        }
    }

    context("simple ops") {

        // msg templates
        val ttOracle2 = "$traceHeader\n\t$kw1msg\n\t$kw0msg\n"
        val ttOracle3 = "$traceHeader\n\t$kw2msg\n\t$kw1msg\n\t$kw0msg\n"
        val sOracle3 = "${FWrtMsgs::class.simpleName}(item=x61, tt='$ttOracle3')"
        val cnFwrtLambda = "FWriterApiKt$\$Lambda"

        // setup
        val kw0 = fwStartValue('a', kw0msg)
        // first function
        val kwf1: (Char) -> FWK<Int> = c2code.toFWriter(kw1msg) // 1
        val kw1 = kwf1('a')
        // second function
        val kwf2: (Int) -> FWK<String> = i2hex.toFWriter(kw2msg) // 2
        val kw2: FWK<String> = kwf2('a'.code)

        expect("first function validation") {
            // setup validation kw0
            kw0::class.simpleName!! shouldBe FWInitial::class.simpleName!!
            kw0.msg shouldBe kw0msg
            kw0.item shouldBe 'a'
            // setup validation kwf1
            kwf1::class.simpleName!! shouldStartWith cnFwrtLambda
            kw1::class.simpleName!! shouldStartWith FWLNext::class.simpleName!!
            val kw1fx = kw1.fix()
            kw1fx::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            kw1fx.item shouldBe 97
            kw1fx.msg shouldBe kw1msg
        }

        expect("simple bind first function") {

            val kw01: FWK<Int> = kw0.bind(kwf1)

            kw01::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            (kw01 as FWTT).tt.toString() shouldBe ttOracle2
            (kw01 as FWrtMsgs).msg shouldBe kw1msg
            (kw01 as FWrtMsgs).item shouldBe 97 // same as first
            (kw01 as FWrit).msg shouldBe kw1msg // same as first

            val kw01fx: FWriter<Int> = kw01.fix()
            kw01fx::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            (kw01fx as FWTT).tt.toString() shouldBe ttOracle2
            kw01fx.msg shouldBe kw1msg
            kw01fx.item shouldBe 97 // same as first
            (kw01fx as FWrit).msg shouldBe kw1msg // same as first
        }

        expect("simple andThen first function") {

            val kw01at: FWK<Int> = kw0.andThen(kw1msg, c2code)

            kw01at::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            (kw01at as FWTT).tt.toString() shouldBe ttOracle2
            (kw01at as FWrtMsgs).msg shouldBe kw1msg
            (kw01at as FWrtMsgs).item shouldBe 97 // same as first
            (kw01at as FWrit).msg shouldBe kw1msg // same as first

            val kw01atfx: FWriter<Int> = kw01at.fix()
            kw01atfx::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            kw01atfx.msg shouldBe kw1msg
            (kw01atfx as FWrit).msg shouldBe kw1msg // same as first
            kw01atfx.item shouldBe 97 // same as first
            (kw01atfx as FWTT).tt.toString() shouldBe ttOracle2
        }

        expect("second function validation") {
            // setup validation kwf2
            kwf2::class.simpleName!! shouldStartWith cnFwrtLambda
            kw2::class.simpleName!! shouldStartWith FWLNext::class.simpleName!!
            val kw2fx = kw2.fix()
            kw2fx::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            kw2fx.item shouldBe "x61"
            kw2fx.msg shouldBe kw2msg
        }

        expect("simple bind second function") {

            val kw01: FWK<Int> = kw0.bind(kwf1)
            val kw012 = kw01.bind(kwf2)

            kw012::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            (kw012 as FWrit).msg shouldBe kw2msg // same as second
            (kw012 as FWTT).tt.toString() shouldBe ttOracle3

            val kw012fx: FWriter<String> = kw012.fix()
            kw012fx::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            kw012fx.item shouldBe "x61" // same as second
            (kw012fx as FWrit).msg shouldBe kw2msg // same as second
            (kw012fx as FWTT).tt.toString() shouldBe ttOracle3

            kw012.toString() shouldBe sOracle3
        }

        expect("simple andThen second function") {

            val kw01: FWK<Int> = kw0.bind(kwf1)
            val kw012a = kw01.andThen(kw2msg, i2hex)

            kw012a::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            (kw012a as FWrit).msg shouldBe kw2msg // same as second
            (kw012a as FWTT).tt.toString() shouldBe ttOracle3

            val kw012afx = kw012a.fix()
            kw012afx.item shouldBe "x61" // same as second
            kw012afx::class.simpleName!! shouldBe FWLNext::class.simpleName!!
            (kw012afx as FWrit).msg shouldBe kw2msg // same as second
            (kw012afx as FWTT).tt.toString() shouldBe ttOracle3

            kw012a.toString() shouldBe sOracle3
        }
    }

    context ("throw ops") {

        val c2codeThrowMsg = "thrown by c2codeThrow with arg 'a'"
        val c2codeThrow: (Char) -> Int = { c -> if ('a' == c) throw IllegalArgumentException(c2codeThrowMsg) else c.code }
        val i2hexThrowMsg = "thrown by i2hexThrow with arg ${'a'.code}"
        val i2hexThrow: (Int) -> String = { i: Int -> if ('a'.code == i) throw NoSuchElementException(i2hexThrowMsg) else i2x(i) }

        expect("single bind with throw") {

            val kw0 = fwStartValue('a')
            val errorLog: OutputStream = ByteArrayOutputStream()
            val errMsg = "<bind with throw unit test>"
            errorLog.use {
                // first function
                val kwf1: (Char) -> FWK<Int> = c2codeThrow.toFWriter(errMsg, errorLog)
                val kw01: FWK<Int> = kw0.bind(kwf1, errorLog)
                shouldThrow<IllegalArgumentException> {
                    try {
                        kw01.fix().item
                    } catch (e: FLibEvaluationException) {
                        throw e.cause!!
                    }
                }
            }
            val errLog = errorLog.toString()
            errLog shouldStartWith "$ERR_TAG [ $ERR_TAG at function evaluation $ERR_BY_TAG $errMsg ] $ERR_BY_TAG \n" +
                    "\tclass flib.rs.FWriterApiTest"
            errLog shouldContain c2codeThrowMsg
            errLog shouldContain startValueMsg
            errLog shouldNotContain unexpectedTerminationMsg
            errLog.length shouldBe 28438
        }

        expect("multiple composition with early throw") {

            val kw0 = fwStartValue('a')
            val errorLog: OutputStream = ByteArrayOutputStream()
            val errMsg = "<bind with early throw unit test>"
            errorLog.use {
                // first function
                val kwf1: (Char) -> FWK<Int> = c2codeThrow.toFWriter(errMsg, errorLog)
                val kw01: FWK<Int> = kw0.bind(kwf1, errorLog)
                // second function
                val kwf2: (Int) -> FWK<String> = i2hex.toFWriter(kw2msg, errorLog) // 2
                val kw012 = kw01.bind(kwf2, errorLog)
                shouldThrow<IllegalArgumentException> {
                    kw012.fix().item
                }
            }
            val errLog = errorLog.toString()
            errLog shouldStartWith "$ERR_TAG [ $ERR_TAG at function evaluation $ERR_BY_TAG $errMsg ] $ERR_BY_TAG \n" +
                    "\tclass flib.rs.FWriterApiTest"
            errLog shouldContain c2codeThrowMsg
            errLog shouldContain startValueMsg
            errLog shouldNotContain kw2msg
            errLog shouldNotContain unexpectedTerminationMsg
            (59133 <= errLog.length && errLog.length <= 59135) shouldBe true
        }

        expect("multiple composition with late throw") {

            val kw0 = fwStartValue('a')
            val errorLog: OutputStream = ByteArrayOutputStream()
            val errMsg = "<bind with late throw unit test>"
            errorLog.use {
                // first function
                val kwf1: (Char) -> FWK<Int> = c2code.toFWriter(kw1msg, errorLog)
                val kw01: FWK<Int> = kw0.bind(kwf1, errorLog)
                // second function
                val kwf2: (Int) -> FWK<String> = i2hexThrow.toFWriter(errMsg,errorLog) // 2
                val kw012 = kw01.bind(kwf2,errorLog)
                shouldThrow<NoSuchElementException> {
                    kw012.fix().item
                }
            }
            val errLog = errorLog.toString()
            errLog shouldStartWith "$ERR_TAG [ $ERR_TAG at function evaluation $ERR_BY_TAG $errMsg ] $ERR_BY_TAG \n" +
                    "\tclass flib.rs.FWriterApiTest"
            errLog shouldContain kw1msg
            (28440 <= errLog.length && errLog.length <= 28443) shouldBe true
        }
    }
})