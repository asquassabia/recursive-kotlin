package flib.impl

import flib.LARGE_DEPTH
import flib.XLARGE_DEPTH
import flib.listKindBuilder
import flib.listKindReverseBuilder
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.impl.FListKind
import org.xrpn.flib.impl.prepend
import org.xrpn.flib.impl.reverse

class FListKindTest: ExpectSpec({

    context("listBuilder") {
        expect ("direct") {
            listKindBuilder(FListKind.empty<Int>(),0,10).show shouldBe "FList@{10}:(0, #(1, #(2, #(3, #(4, #(5, #(6, #(7, #(8, #(9, #*)*)*)*)*)*)*)*)*)*)"
        }
        expect ("reverse") {
            listKindReverseBuilder(FListKind.empty<Int>(),0,10).show shouldBe "FList@{10}:(9, #(8, #(7, #(6, #(5, #(4, #(3, #(2, #(1, #(0, #*)*)*)*)*)*)*)*)*)*)"
        }
    }

    context("size") {
        val aut0 = FListKind.empty<Int>()
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
        val aut = FListKind.empty<Int>()
        expect("true for empty") {
            aut.empty shouldBe true
        }
        expect("false for not empty") {
            aut.prepend(1).empty shouldBe false
        }
    }

    context("toString()") {
        expect("empty list") {
            FListKind.empty<Int>().toString() shouldBe "FList@{0}:"
        }
        expect("list of one") {
            FListKind.empty<Int>().prepend(1).toString() shouldBe "FList@{1}:(1, #*)"
        }
        expect("list of three") {
            FListKind.empty<Int>().prepend(1).prepend(2).prepend(3).toString() shouldBe "FList@{3}:(3, #(2, #(1, #*)*)*)"
        }
        expect("large list") {
            listKindReverseBuilder(FListKind.empty<Int>(),0, LARGE_DEPTH).toString().length shouldBe 48903
        }

    }

    context("hashCode()") {
        expect("empty list") {
            FListKind.empty<Int>().hashCode() shouldBe 1549
        }
        expect("list of one") {
            FListKind.empty<Int>().prepend(1).hashCode() shouldBe 48020
        }
        expect("list of three") {
            FListKind.empty<Int>().prepend(1).prepend(2).prepend(3).hashCode() shouldBe 46149205
        }
        expect("list of three reversed") {
            FListKind.empty<Int>().prepend(1).prepend(2).prepend(3).reverse().hashCode() shouldBe 46147285
        }
        expect("large list") {
            val ll = listKindBuilder(FListKind.empty<Int>(),0,LARGE_DEPTH)
            ll.hashCode() shouldBe -1644694549
            ll.reverse().hashCode() shouldBe -577767472
        }
        expect("very large list") {
            val xll = listKindBuilder(FListKind.empty<Int>(),0,XLARGE_DEPTH)
            xll.hashCode() shouldBe -565784471
            xll.reverse().hashCode() shouldBe -447314914
        }
        expect("enormous list") {
            val xxll = listKindBuilder(FListKind.empty<Int>(),0,1000000)
            (xxll.hashCode() == xxll.reverse().hashCode()) shouldBe false
        }
    }

    context("equals()") {
        expect("empty list (this is unfortunate)") {
            FListKind.empty<Int>().equals(FListKind.empty<Int>()) shouldBe true
            FListKind.empty<Int>().equals(FListKind.empty<String>()) shouldBe /* unfortunately */ true
        }
        expect("list of one") {
            FListKind.empty<Int>().prepend(1).equals(FListKind.empty<Int>().prepend(1)) shouldBe true
            FListKind.empty<String>().prepend("A").equals(FListKind.empty<String>().prepend("A")) shouldBe true
            FListKind.empty<Int>().prepend(2).equals(FListKind.empty<Int>().prepend(1)) shouldBe false
            FListKind.empty<String>().prepend("B").equals(FListKind.empty<String>().prepend("A")) shouldBe false
        }
        expect("list of three") {
            FListKind.empty<Int>().prepend(1).prepend(2).prepend(3).equals(FListKind.empty<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            FListKind.empty<String>().prepend("A").prepend("B").prepend("C").equals(FListKind.empty<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
            FListKind.empty<Int>().prepend(1).prepend(1).prepend(3).equals(FListKind.empty<Int>().prepend(1).prepend(2).prepend(3)) shouldBe false
            FListKind.empty<String>().prepend("A").prepend("A").prepend("C").equals(FListKind.empty<String>().prepend("A").prepend("B").prepend("C")) shouldBe false
        }
        expect("equals of large") {
            val a = listKindReverseBuilder(FListKind.empty<Int>(),0,LARGE_DEPTH)
            val b = listKindReverseBuilder(FListKind.empty<Int>(),0,LARGE_DEPTH)
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
        expect("equals of xlarge") {
            val a = listKindReverseBuilder(FListKind.empty<Int>(),0,XLARGE_DEPTH)
            val b = listKindReverseBuilder(FListKind.empty<Int>(),0,XLARGE_DEPTH)
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
    }

    context("==, ===") {
        expect("empty list (this is unfortunate)") {
            (FListKind.empty<Int>() == FListKind.empty<Int>()) shouldBe true
            (FListKind.empty<Int>() == FListKind.empty<String>()) shouldBe /* unfortunately */ true
            (FListKind.empty<Int>() === FListKind.empty<Int>()) shouldBe false
            (FListKind.empty<Int>() === FListKind.empty<String>()) shouldBe false
        }
        expect("list of one") {
            (FListKind.empty<Int>().prepend(1) == FListKind.empty<Int>().prepend(1)) shouldBe true
            (FListKind.empty<String>().prepend("A") == FListKind.empty<String>().prepend("A")) shouldBe true
            (FListKind.empty<Int>().prepend(2) != FListKind.empty<Int>().prepend(1)) shouldBe true
            (FListKind.empty<String>().prepend("B") != FListKind.empty<String>().prepend("A")) shouldBe true
        }
        expect("list of three") {
            (FListKind.empty<Int>().prepend(1).prepend(2).prepend(3) == FListKind.empty<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            (FListKind.empty<String>().prepend("A").prepend("B").prepend("C") == FListKind.empty<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
            (FListKind.empty<Int>().prepend(1).prepend(1).prepend(3) != FListKind.empty<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            (FListKind.empty<String>().prepend("A").prepend("A").prepend("C") != FListKind.empty<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
        }
    }
})