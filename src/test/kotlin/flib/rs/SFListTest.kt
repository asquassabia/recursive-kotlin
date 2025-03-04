package flib.rs

import flib.LARGE_DEPTH
import flib.XLARGE_DEPTH
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.rs.SFList
import org.xrpn.flib.internal.tool.flistBuilder

class SFListTest: ExpectSpec({

    val sfl10 = SFList.ofIntSeq(0,10)
    val sfl10Rev = SFList.ofIntSeqRev(0,10)
    val sflLarge = SFList.ofIntSeq(0,LARGE_DEPTH)
    val sflLargeRev = SFList.ofIntSeqRev(0,LARGE_DEPTH)
    val sflXLarge = SFList.ofIntSeq(0,XLARGE_DEPTH)
    val sflXLargeRev = SFList.ofIntSeqRev(0,XLARGE_DEPTH)

    context("listBuilder") {
        expect ("direct") {
            sfl10.show shouldBe "SFList@{10}:(0, #(1, #(2, #(3, #(4, #(5, #(6, #(7, #(8, #(9, #*)*)*)*)*)*)*)*)*)*)"
        }
        expect ("reverse") {
            sfl10Rev.show shouldBe "SFList@{10}:(9, #(8, #(7, #(6, #(5, #(4, #(3, #(2, #(1, #(0, #*)*)*)*)*)*)*)*)*)*)"
        }
    }

    context("size") {
        val aut0 = SFList.of<Int>()
        val aut1 = aut0.prepend(1)
        val aut2 = aut1.prepend(2)
        val aut3 = aut2.prepend(3)
        expect("for empty") {
            aut0.size shouldBe 0
        }
        expect("for one") {
            aut1.size shouldBe 1
        }
        expect("for two") {
            aut2.size shouldBe 2
        }
        expect("for three") {
            aut3.size shouldBe 3
        }
    }

    context("empty") {
        val aut = SFList.of<Int>()
        expect("true for empty") {
            aut.empty shouldBe true
        }
        expect("false for not empty") {
            aut.prepend(1).empty shouldBe false
        }
    }

    context("toString()") {
        expect("empty list") {
            SFList.of<Int>().toString() shouldBe "SFList@{0}:"
        }
        expect("list of one") {
            SFList.of<Int>().prepend(1).toString() shouldBe "SFList@{1}:(1, #*)"
        }
        expect("list of three") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).toString() shouldBe "SFList@{3}:(3, #(2, #(1, #*)*)*)"
        }
        expect("large list") {
            sflLargeRev.toString().length shouldBe 48904
            sflLarge.toString().length shouldBe 48904
        }

    }

    context("hashCode()") {
        expect("empty list") {
            SFList.of<Int>().hashCode() shouldBe 1569133934
        }
        expect("list of one") {
            SFList.of<Int>().prepend(1).hashCode() shouldBe 1569087441
        }
        expect("list of three") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).hashCode() shouldBe 1522986262
        }
        expect("list of three reversed") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).reverse().hashCode() shouldBe 1522988182
        }
        expect("large list") {
            val ll = sflLarge
            ll.hashCode() shouldBe -1081137272
            ll.reverse().hashCode() shouldBe 2146902957
        }
        expect("very large list") {
            val xll = sflXLarge
            xll.hashCode() shouldBe -500671030
            xll.reverse().hashCode() shouldBe -1092483384
        }
        expect("enormous list") {
            val xxll1 = SFList.ofIntSeq(0,1000000)
            val xxll2 = SFList.ofIntSeqRev(0,1000000)
            val xxll1r = xxll1.reverse()
            xxll1.hashCode() shouldNotBe xxll1r.hashCode()
            xxll2.hashCode() shouldBe xxll1r.hashCode()
        }
    }

    context("eq: KWriter<B>uals()") {
        expect("empty list (this is unfortunate)") {
            SFList.of<Int>().equals(SFList.of<Int>()) shouldBe true
            SFList.of<Int>().equals(SFList.of<String>()) shouldBe /* unfortunately */ true
        }
        expect("list of one") {
            SFList.of<Int>().prepend(1).equals(SFList.of<Int>().prepend(1)) shouldBe true
            SFList.of<String>().prepend("A").equals(SFList.of<String>().prepend("A")) shouldBe true
            SFList.of<Int>().prepend(2).equals(SFList.of<Int>().prepend(1)) shouldBe false
            SFList.of<String>().prepend("B").equals(SFList.of<String>().prepend("A")) shouldBe false
        }
        expect("list of three") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).equals(SFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            SFList.of<String>().prepend("A").prepend("B").prepend("C").equals(SFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
            SFList.of<Int>().prepend(1).prepend(1).prepend(3).equals(SFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe false
            SFList.of<String>().prepend("A").prepend("A").prepend("C").equals(SFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe false
        }
        expect("equals of large") {
            val a = sflLargeRev
            val b = sflLargeRev
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
        expect("equals of xlarge") {
            val a = sflXLargeRev
            val b = sflXLargeRev
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
    }

    context("==, ===") {
        expect("empty list (this is unfortunate)") {
            (SFList.of<Int>() == SFList.of<Int>()) shouldBe true
            (SFList.of<Int>() == SFList.of<String>()) shouldBe /* unfortunately */ true
            (SFList.of<Int>() === SFList.of<Int>()) shouldBe false
            (SFList.of<Int>() === SFList.of<String>()) shouldBe false
        }
        expect("list of one") {
            (SFList.of<Int>().prepend(1) == SFList.of<Int>().prepend(1)) shouldBe true
            (SFList.of<String>().prepend("A") == SFList.of<String>().prepend("A")) shouldBe true
            (SFList.of<Int>().prepend(2) != SFList.of<Int>().prepend(1)) shouldBe true
            (SFList.of<String>().prepend("B") != SFList.of<String>().prepend("A")) shouldBe true
        }
        expect("list of three") {
            (SFList.of<Int>().prepend(1).prepend(2).prepend(3) == SFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            (SFList.of<String>().prepend("A").prepend("B").prepend("C") == SFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
            (SFList.of<Int>().prepend(1).prepend(1).prepend(3) != SFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            (SFList.of<String>().prepend("A").prepend("A").prepend("C") != SFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
        }
    }

    context("fix") {
        expect("return internal list") {
            val fl = flistBuilder(0,10)
            val kfl = SFList.of(fl)
            kfl.fix() shouldBe fl
            (kfl.fix() === fl) shouldBe true
        }
        expect("return empty") {
            val kfl = SFList.of<Int>()
            kfl.fix() shouldBe FLNil<Int>()
            (kfl.fix() === FLNil<Int>()) shouldBe false // generates new()
        }
    }
})