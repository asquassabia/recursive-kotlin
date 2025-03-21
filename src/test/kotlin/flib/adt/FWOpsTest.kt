package flib.adt

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FWK
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.api.fwStartValue
import org.xrpn.flib.internal.impl.FWBuilder

class FWOpsTest : ExpectSpec({

    val kw1 = fwStartValue(1,"A")
    val kw2 = fwStartValue(1,"B")
    val kw3 = fwStartValue(2,"A")

    context("constructors") {
        expect("of(a:A, msg:String)") {
            kw1.item shouldBe 1
            kw1.msg shouldBe "A"
            kw2.item shouldBe 1
            kw2.msg shouldBe "B"
            kw3.item shouldBe 2
            kw3.msg shouldBe "B"
        }
    }
    context("hashcode") {
        expect("hash") {
            kw1.hashCode() shouldBe 1569087408 // 48115 // 1569087408
            kw2.hashCode() shouldBe 1569087439 // 48146 // 1569087439
            kw3.hashCode() shouldBe 1569087408 // 48115 // 1569087408
        }
    }
//    expect("equals") {
//        val kw2_: FWrtMsgs<Int> = FWBuilder.startWith(1,"B",(kw1 as FWLog<*>).log)
//        val kw2a: FWrtMsgs<Int> = FWBuilder.startWith('a'.code,"B",(kw1 as FWLog<*>).log)
//        val kw2b: FWrtMsgs<Char> = FWBuilder.startWith('a',"B",(kw1 as FWLog<*>).log)
//        val kw2b_: FWrtMsgs<Char> = FWBuilder.startWith('a',"B",(kw1 as FWLog<*>).log)
//        kw2.equals(kw2_) shouldBe true
//        kw2.equals(kw2a) shouldBe false
//        kw2a.equals(kw2b) shouldBe false
//        kw2b.equals(kw2b_) shouldBe true
//    }
    expect("toString") {
        kw1.toString() shouldBe "FWrtMsg(item=1, msg='A')"
        kw2.toString() shouldBe "FWrtMsgs(item=1, log=SFList@{2}:(B, #(A, #*)*))"
    }
})