package flib.impl

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.KWMsg
import org.xrpn.flib.adt.KWriter
import org.xrpn.flib.impl.KWf
import org.xrpn.flib.impl.bind
import org.xrpn.flib.impl.chain
import org.xrpn.flib.impl.compose

class KWfApiTest : ExpectSpec({


    context("constructor") {

        fun i2x(i:Int) = "x"+i.toString(16)
        val c2code: (Char) -> Int = { c:Char -> c.code }
        val i2hex: (Int) -> String = { i:Int -> i2x(i) }

        // msg templates
        val kw0msg = "'a' is start value"
        val kw1msg = "'a' to ${'a'.code}" // "'a' to 97"
        val kw2msg = "${'a'.code} to ${i2x('a'.code)}" // "97 to x61"

        expect("KWf.of") {
            val kwf = KWf.of(c2code, kw1msg)
            val kw: KWMsg<Int> = kwf('a')
            kw.item shouldBe 97
            kw.msg shouldBe kw1msg
        }

        expect("simple bind") {
            // msg templates
            val kw0msg = "'a' is start value"
            val kw1msg = "'a' to ${'a'.code}" // "'a' to 97"
            val kw2msg = "${'a'.code} to ${i2x('a'.code)}" // "97 to x61"

            // dummy
            val kw0: KWriter<Char> = KWriter.of('a', kw0msg)

            // first function
            val kwf1: KWf<Char, Int> = KWf.of(c2code, kw1msg) // 1
            val kw1: KWMsg<Int> = kwf1('a')

            // bind to dummy
            val kw10: KWriter<Int> = kwf1.bind(kw0)
            kw10.item shouldBe 97 // same as first
            (kw10 as KWMsg<Int>).msg shouldBe kw1msg // same as first

            // second function
            val kwf2: KWf<Int, String> = KWf.of(i2hex, kw2msg) // 2
            val kw2: KWMsg<String> = kwf2('a'.code)
            kw2.item shouldBe "x61"
            kw2.msg shouldBe kw2msg

            // bind to (first bound to dummy)
            val kw210: KWriter<String> = kwf2.bind(kw10)
            kw210.item shouldBe "x61" // same as second
            (kw210 as KWMsg<String>).msg shouldBe kw2msg // same as second

            kw210.toString() shouldBe "KWTrace(item=x61, trace=FList@{3}:($kw2msg, #($kw1msg, #($kw0msg, #*)*)*))"
        }

        expect("simple chain and compose") {

            // first function
            val kwf1: KWf<Char, Int> = KWf.of(c2code, kw1msg) // 1

            // second function
            val kwf2: KWf<Int, String> = KWf.of(i2hex, kw2msg) // 2

            // chain, f1 and then f2
            val chain12: (Char) -> KWriter<String> = kwf1.chain<Char,Int,String>(kwf2)
            // compose, f2(f1)
            val compo21: (Char) -> KWriter<String> = kwf2.compose<Char,Int,String>(kwf1)

            val kw12 = chain12('A').toString()
            val kw21 = compo21('A').toString()
            kw21.toString() shouldBe kw12.toString()
            kw21.toString() shouldBe "KWTrace(item=x41, trace=FList@{2}:($kw2msg, #($kw1msg, #*)*))"
        }
    }
})