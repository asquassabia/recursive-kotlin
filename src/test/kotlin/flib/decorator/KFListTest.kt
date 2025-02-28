package flib.decorator

import flib.LARGE_DEPTH
import flib.XLARGE_DEPTH
import flib.flistBuilder
import flib.listKindBuilder
import flib.listKindReverseBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FNel
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.decorator.prepend
import org.xrpn.flib.decorator.reverse

class KFListTest: ExpectSpec({

    context("listBuilder") {
        expect ("direct") {
            listKindBuilder(0,10).show shouldBe "FList@{10}:(0, #(1, #(2, #(3, #(4, #(5, #(6, #(7, #(8, #(9, #*)*)*)*)*)*)*)*)*)*)"
        }
        expect ("reverse") {
            listKindReverseBuilder(0,10).show shouldBe "FList@{10}:(9, #(8, #(7, #(6, #(5, #(4, #(3, #(2, #(1, #(0, #*)*)*)*)*)*)*)*)*)*)"
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
            SFList.of<Int>().toString() shouldBe "FList@{0}:"
        }
        expect("list of one") {
            SFList.of<Int>().prepend(1).toString() shouldBe "FList@{1}:(1, #*)"
        }
        expect("list of three") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).toString() shouldBe "FList@{3}:(3, #(2, #(1, #*)*)*)"
        }
        expect("large list") {
            listKindReverseBuilder(0, LARGE_DEPTH).toString().length shouldBe 48903
        }

    }

    context("hashCode()") {
        expect("empty list") {
            SFList.of<Int>().hashCode() shouldBe 1549
        }
        expect("list of one") {
            SFList.of<Int>().prepend(1).hashCode() shouldBe 48020
        }
        expect("list of three") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).hashCode() shouldBe 46149205
        }
        expect("list of three reversed") {
            SFList.of<Int>().prepend(1).prepend(2).prepend(3).reverse().hashCode() shouldBe 46147285
        }
        expect("large list") {
            val ll = listKindBuilder(0,LARGE_DEPTH)
            ll.hashCode() shouldBe -1644694549
            ll.reverse().hashCode() shouldBe -577767472
        }
        expect("very large list") {
            val xll = listKindBuilder(0,XLARGE_DEPTH)
            xll.hashCode() shouldBe -565784471
            xll.reverse().hashCode() shouldBe -447314914
        }
        expect("enormous list") {
            val xxll = listKindBuilder(0,1000000)
            (xxll.hashCode() == xxll.reverse().hashCode()) shouldBe false
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
            val a = listKindReverseBuilder(0,LARGE_DEPTH)
            val b = listKindReverseBuilder(0,LARGE_DEPTH)
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
        expect("equals of xlarge") {
            val a = listKindReverseBuilder(0,XLARGE_DEPTH)
            val b = listKindReverseBuilder(0,XLARGE_DEPTH)
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
            (kfl.fix() === FLNil<Int>()) shouldBe true
        }
    }

    context("fnel") {
        expect("return internal list") {
            val fl = flistBuilder(0,10)
            val kfl = SFList.of(fl)
            (kfl.fnel() as FNel).nel shouldBe fl
            (kfl.fnel() as FNel).kind shouldBe kfl
        }
        expect("return ???") {
            val kfl = SFList.of<Int>()
            shouldThrow<IllegalStateException> {
                kfl.fnel()
            }
        }
    }

})