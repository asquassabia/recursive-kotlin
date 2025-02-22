package flib.decorator

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FWrit
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.impl.andThen
import org.xrpn.flib.impl.bind
import org.xrpn.flib.impl.fwrit
import org.xrpn.flib.impl.fwStartValue


class FWApiTest : ExpectSpec({


    context("constructor") {

        fun i2x(i:Int) = "x"+i.toString(16)
        val c2code: (Char) -> Int = { c:Char -> c.code }
        val i2hex: (Int) -> String = { i:Int -> i2x(i) }

        // msg templates
        val kw0msg = "'a' is start value"
        val kw1msg = "'a' to ${'a'.code}" // "'a' to 97"
        val kw2msg = "${'a'.code} to ${i2x('a'.code)}" // "97 to x61"

        expect("KWf.of") {
            val kwf = c2code.fwrit(kw1msg)
            val kw = kwf('a')
            kw.item shouldBe 97
            kw.msg shouldBe kw1msg
        }

        expect("simple bind") {
            // msg templates
            val kw0msg = "'a' is start value"
            val kw1msg = "'a' to ${'a'.code}" // "'a' to 97"
            val kw2msg = "${'a'.code} to ${i2x('a'.code)}" // "97 to x61"

            val kw0: FWrtMsg<Char> = fwStartValue('a', kw0msg)

            // first function
            val kwf1: (Char) -> FWrtMsg<Int> = c2code.fwrit(kw1msg) // 1
            val kw10: FWriter<Int> = kw0.bind(kwf1)
            val kw10a = kw0.andThen(kw1msg, c2code)
            kw10.item shouldBe 97 // same as first
            kw10a.item shouldBe 97 // same as first
            (kw10 as FWrit).msg shouldBe kw1msg // same as first
            (kw10a as FWrit).msg shouldBe kw1msg // same as first

            // second function
            val kwf2 = i2hex.fwrit(kw2msg) // 2
            val kw2 = kwf2('a'.code)
            kw2.item shouldBe "x61"
            kw2.msg shouldBe kw2msg

            // bind to (first bound to dummy)
            val kw210 = kw10.bind(kwf2)
            kw210.item shouldBe "x61" // same as second
            (kw210 as FWrit).msg shouldBe kw2msg // same as second

            // kw210.toString() shouldBe "KWTrace(item=x61, trace=FList@{3}:($kw2msg, #($kw1msg, #($kw0msg, #*)*)*))"
        }

//        expect("simple chain and compose") {
//
//            // first function
//            val kwf1: KWriterKind<Char, Int> = KWriterKind.of(c2code, kw1msg) // 1
//
//            // second function
//            val kwf2: KWriterKind<Int, String> = KWriterKind.of(i2hex, kw2msg) // 2
//
//            // chain, f1 and then f2
//            val chain12: (Char) -> KWriter<String> = kwf1.chain<Char,Int,String>(kwf2)
//            // compose, f2(f1)
//            val compo21: (Char) -> KWriter<String> = kwf2.compose<Char,Int,String>(kwf1)
//
//            val kw12 = chain12('A').toString()
//            val kw21 = compo21('A').toString()
//            kw21.toString() shouldBe kw12.toString()
//            kw21.toString() shouldBe "KWTrace(item=x41, trace=FList@{2}:($kw2msg, #($kw1msg, #*)*))"
//        }
    }
})