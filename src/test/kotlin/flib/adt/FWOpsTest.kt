package flib.adt

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FWLog
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.internal.impl.FWBuilder

class FWOpsTest : ExpectSpec({
    val kw1: FWrtMsg<Int> = FWBuilder.startWith(1,"A")
    val kw2: FWrtMsgs<Int> = FWBuilder.startWith(1,"B", (kw1 as FWLog<*>).log )
    context("constructors") {
        expect("of(a:A, msg:Strint)") {
            kw1.item shouldBe 1
            kw1.msg shouldBe "A"
        }
        expect("of(a:A, msg1:String, msg2:String)") {
            kw2.item shouldBe 1
            kw2.msg shouldBe "B"
            val l = (kw2 as FWLog<*>).log
            l.size shouldBe 2
            l.toString() shouldBe "SFList@{2}:(B, #(A, #*)*)"
        }
        expect("push(b:B, newMsg: String, mts:KWTrace<A>)") {
            val kw3 = FWBuilder.startWith("2","C",(kw2 as FWLog<*>).log )
            kw3.item shouldBe "2"
            kw3.msg shouldBe "C"
            val l = (kw3 as FWLog<*>).log
            l.size shouldBe 3
            l.toString() shouldBe "SFList@{3}:(C, #(B, #(A, #*)*)*)"
        }
    }
    context("hashcode") {
        expect("hash") {
            val kw = FWBuilder.startWith(2,"A")
            kw1.hashCode() shouldBe 1569087408 // 48115 // 1569087408
            kw.hashCode() shouldBe 1569087439 // 48146 // 1569087439
        }
    }
    expect("equals") {
        val kw2_: FWrtMsgs<Int> = FWBuilder.startWith(1,"B",(kw1 as FWLog<*>).log)
        val kw2a: FWrtMsgs<Int> = FWBuilder.startWith('a'.code,"B",(kw1 as FWLog<*>).log)
        val kw2b: FWrtMsgs<Char> = FWBuilder.startWith('a',"B",(kw1 as FWLog<*>).log)
        val kw2b_: FWrtMsgs<Char> = FWBuilder.startWith('a',"B",(kw1 as FWLog<*>).log)
        kw2.equals(kw2_) shouldBe true
        kw2.equals(kw2a) shouldBe false
        kw2a.equals(kw2b) shouldBe false
        kw2b.equals(kw2b_) shouldBe true
    }
    expect("toString") {
        kw1.toString() shouldBe "FWrtMsg(item=1, msg='A')"
        kw2.toString() shouldBe "FWrtMsgs(item=1, log=SFList@{2}:(B, #(A, #*)*))"
    }
})