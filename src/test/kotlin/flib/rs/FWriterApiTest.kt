package flib.rs

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import org.xrpn.flib.ERR_BY_TAG
import org.xrpn.flib.ERR_TAG
import org.xrpn.flib.adt.FWrit
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.api.andThen
import org.xrpn.flib.api.bind
import org.xrpn.flib.api.makeFWriter
import org.xrpn.flib.api.fwStartValue
import org.xrpn.flib.api.startValueMsg
import org.xrpn.flib.internal.impl.FWBuilder.Companion.unexpectedTerminationMsg
import java.io.ByteArrayOutputStream
import java.io.OutputStream


class FWriterApiTest : ExpectSpec({

    context("constructor") {

        fun i2x(i: Int) = "x" + i.toString(16)
        val c2code: (Char) -> Int = { c: Char -> c.code }
        val c2codeThrowMsg = "thrown by c2codeThrow with arg 'a'"
        val c2codeThrow: (Char) -> Int = { c -> if ('a' == c) throw IllegalArgumentException(c2codeThrowMsg) else c.code }
        val i2hex: (Int) -> String = { i: Int -> i2x(i) }
        val i2hexThrowMsg = "thrown by i2hexThrow with arg ${'a'.code}"
        val i2hexThrow: (Int) -> String = { i: Int -> if ('a'.code == i) throw NoSuchElementException(i2hexThrowMsg) else i2x(i) }

        // msg templates
        val kw0msg = "'a' is start value"
        val kw1msg = "'a' to ${'a'.code}" // "'a' to 97"
        val kw2msg = "${'a'.code} to ${i2x('a'.code)}" // "97 to x61"

        expect("KWf.of") {
            val kwf = c2code.makeFWriter(kw1msg)
            val kw = kwf('a')
            kw.item shouldBe 97
            kw.msg shouldBe kw1msg
        }

        expect("start value") {
            val kw0a: FWrtMsg<Char> = fwStartValue('a')
            kw0a.toString() shouldBe "FWrtMsg(item=a, msg='start value')"
        }

        expect("simple bind") {

            val kw0: FWrtMsg<Char> = fwStartValue('a', kw0msg)

            // first function
            val kwf1: (Char) -> FWrtMsg<Int> = c2code.makeFWriter(kw1msg) // 1
            val kw10: FWriter<Int> = kw0.bind(kwf1)
            val kw10a = kw0.andThen(kw1msg, c2code)
            kw10.item shouldBe 97 // same as first
            kw10a.item shouldBe 97 // same as first
            (kw10 as FWrit).msg shouldBe kw1msg // same as first
            (kw10a as FWrit).msg shouldBe kw1msg // same as first

            // second function
            val kwf2 = i2hex.makeFWriter(kw2msg) // 2
            val kw2 = kwf2('a'.code)
            kw2.item shouldBe "x61"
            kw2.msg shouldBe kw2msg

            // bind to (first bound to dummy)
            val kw210 = kw10.bind(kwf2)
            kw210.item shouldBe "x61" // same as second
            (kw210 as FWrit).msg shouldBe kw2msg // same as second

            kw210.toString() shouldBe "FWrtMsgs(item=x61, log=SFList@{3}:(97 to x61, #('a' to 97, #('a' is start value, #*)*)*))"
        }

        expect("single bind with throw") {

            val kw0: FWrtMsg<Char> = fwStartValue('a')
            val errorLog: OutputStream = ByteArrayOutputStream()
            val errMsg = "<bind with throw unit test>"
            errorLog.use {
                // first function
                val kwf1: (Char) -> FWrtMsg<Int> = c2codeThrow.makeFWriter(errMsg, errorLog)
                val kw10: FWriter<Int> = kw0.bind(kwf1, errorLog)
                shouldThrow<IllegalArgumentException> {
                    kw10.item
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

            val kw0: FWrtMsg<Char> = fwStartValue('a')
            val errorLog: OutputStream = ByteArrayOutputStream()
            val errMsg = "<bind with early throw unit test>"
            errorLog.use {
                // first function
                val kwf1: (Char) -> FWrtMsg<Int> = c2codeThrow.makeFWriter(errMsg, errorLog)
                val kw10: FWriter<Int> = kw0.bind(kwf1, errorLog)
                // second function
                val kwf2: (Int) -> FWrtMsg<String> = i2hex.makeFWriter(kw2msg, errorLog) // 2
                val kw210 = kw10.bind(kwf2, errorLog)
                shouldThrow<IllegalArgumentException> {
                    kw210.item
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

            val kw0: FWrtMsg<Char> = fwStartValue('a')
            val errorLog: OutputStream = ByteArrayOutputStream()
            val errMsg = "<bind with late throw unit test>"
            errorLog.use {
                // first function
                val kwf1: (Char) -> FWrtMsg<Int> = c2code.makeFWriter(kw1msg, errorLog)
                val kw10: FWriter<Int> = kw0.bind(kwf1, errorLog)
                // second function
                val kwf2: (Int) -> FWrtMsg<String> = i2hexThrow.makeFWriter(errMsg,errorLog) // 2
                val kw210 = kw10.bind(kwf2,errorLog)
                shouldThrow<NoSuchElementException> {
                    kw210.item
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