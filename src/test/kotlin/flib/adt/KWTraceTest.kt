package flib.adt

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.KWTrace

class KWTraceTest : ExpectSpec({
    val kw1 = KWTrace.of(1,"A")
    val kw2 = KWTrace.of(1,"A", "B")
    context("constructors") {
        expect("of(a:A, msg:Strint)") {
            kw1.item shouldBe 1
            kw1.msg shouldBe "A"
        }
        expect("of(a:A, msg1:String, msg2:String)") {
            kw2.item shouldBe 1
            kw2.msg shouldBe "B"
            kw2.trace.size shouldBe 2
            kw2.trace.toString() shouldBe "FList@{2}:(B, #(A, #*)*)"
        }
        expect("push(b:B, newMsg: String, mts:KWTrace<A>)") {
            val kw3 = KWTrace.push("2","C", kw2)
            kw3.item shouldBe "2"
            kw3.msg shouldBe "C"
            kw3.trace.size shouldBe 3
            kw3.trace.toString() shouldBe "FList@{3}:(C, #(B, #(A, #*)*)*)"
        }
    }
    context("hashcode") {
        expect("hash") {
            val kw = KWTrace.of(2,"A")
            kw1.hashCode() shouldBe 48115
            kw.hashCode() shouldBe 48116
        }
    }
    expect("equals") {
        val kw2_: KWTrace<Int> = KWTrace.of(1,"A", "B")
        val kw2a: KWTrace<Int> = KWTrace.of('a'.code,"A", "B")
        val kw2b: KWTrace<Char> = KWTrace.of('a',"A", "B")
        val kw2b_: KWTrace<Char> = KWTrace.of('a',"A", "B")
        kw2.equals(kw2_) shouldBe true
        kw2.equals(kw2a) shouldBe false
        kw2a.equals(kw2b) shouldBe false
        kw2b.equals(kw2b_) shouldBe true
    }
    expect("toString") {
        kw2.toString() shouldBe "KWTrace(item=1, trace=FList@{2}:(B, #(A, #*)*))"
    }
})