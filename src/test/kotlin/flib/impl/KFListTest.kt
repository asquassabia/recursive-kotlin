package flib.impl

import arrow.core.nel
import flib.LARGE_DEPTH
import flib.XLARGE_DEPTH
import flib.flistBuilder
import flib.listKindBuilder
import flib.listKindReverseBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FLNel
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.impl.KFList
import org.xrpn.flib.impl.prepend
import org.xrpn.flib.impl.reverse

class KFListTest: ExpectSpec({

    context("listBuilder") {
        expect ("direct") {
            listKindBuilder(KFList.of<Int>(),0,10).show shouldBe "FList@{10}:(0, #(1, #(2, #(3, #(4, #(5, #(6, #(7, #(8, #(9, #*)*)*)*)*)*)*)*)*)*)"
        }
        expect ("reverse") {
            listKindReverseBuilder(KFList.of<Int>(),0,10).show shouldBe "FList@{10}:(9, #(8, #(7, #(6, #(5, #(4, #(3, #(2, #(1, #(0, #*)*)*)*)*)*)*)*)*)*)"
        }
    }

    context("size") {
        val aut0 = KFList.of<Int>()
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
        val aut = KFList.of<Int>()
        expect("true for empty") {
            aut.empty shouldBe true
        }
        expect("false for not empty") {
            aut.prepend(1).empty shouldBe false
        }
    }

    context("toString()") {
        expect("empty list") {
            KFList.of<Int>().toString() shouldBe "FList@{0}:"
        }
        expect("list of one") {
            KFList.of<Int>().prepend(1).toString() shouldBe "FList@{1}:(1, #*)"
        }
        expect("list of three") {
            KFList.of<Int>().prepend(1).prepend(2).prepend(3).toString() shouldBe "FList@{3}:(3, #(2, #(1, #*)*)*)"
        }
        expect("large list") {
            listKindReverseBuilder(KFList.of<Int>(),0, LARGE_DEPTH).toString().length shouldBe 48903
        }

    }

    context("hashCode()") {
        expect("empty list") {
            KFList.of<Int>().hashCode() shouldBe 1549
        }
        expect("list of one") {
            KFList.of<Int>().prepend(1).hashCode() shouldBe 48020
        }
        expect("list of three") {
            KFList.of<Int>().prepend(1).prepend(2).prepend(3).hashCode() shouldBe 46149205
        }
        expect("list of three reversed") {
            KFList.of<Int>().prepend(1).prepend(2).prepend(3).reverse().hashCode() shouldBe 46147285
        }
        expect("large list") {
            val ll = listKindBuilder(KFList.of<Int>(),0,LARGE_DEPTH)
            ll.hashCode() shouldBe -1644694549
            ll.reverse().hashCode() shouldBe -577767472
        }
        expect("very large list") {
            val xll = listKindBuilder(KFList.of<Int>(),0,XLARGE_DEPTH)
            xll.hashCode() shouldBe -565784471
            xll.reverse().hashCode() shouldBe -447314914
        }
        expect("enormous list") {
            val xxll = listKindBuilder(KFList.of<Int>(),0,1000000)
            (xxll.hashCode() == xxll.reverse().hashCode()) shouldBe false
        }
    }

    context("eq: KWriter<B>uals()") {
        expect("empty list (this is unfortunate)") {
            KFList.of<Int>().equals(KFList.of<Int>()) shouldBe true
            KFList.of<Int>().equals(KFList.of<String>()) shouldBe /* unfortunately */ true
        }
        expect("list of one") {
            KFList.of<Int>().prepend(1).equals(KFList.of<Int>().prepend(1)) shouldBe true
            KFList.of<String>().prepend("A").equals(KFList.of<String>().prepend("A")) shouldBe true
            KFList.of<Int>().prepend(2).equals(KFList.of<Int>().prepend(1)) shouldBe false
            KFList.of<String>().prepend("B").equals(KFList.of<String>().prepend("A")) shouldBe false
        }
        expect("list of three") {
            KFList.of<Int>().prepend(1).prepend(2).prepend(3).equals(KFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            KFList.of<String>().prepend("A").prepend("B").prepend("C").equals(KFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
            KFList.of<Int>().prepend(1).prepend(1).prepend(3).equals(KFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe false
            KFList.of<String>().prepend("A").prepend("A").prepend("C").equals(KFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe false
        }
        expect("equals of large") {
            val a = listKindReverseBuilder(KFList.of<Int>(),0,LARGE_DEPTH)
            val b = listKindReverseBuilder(KFList.of<Int>(),0,LARGE_DEPTH)
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
        expect("equals of xlarge") {
            val a = listKindReverseBuilder(KFList.of<Int>(),0,XLARGE_DEPTH)
            val b = listKindReverseBuilder(KFList.of<Int>(),0,XLARGE_DEPTH)
            (a == b) shouldBe true
            val c = a.prepend(1)
            (b == c) shouldBe false
        }
    }

    context("==, ===") {
        expect("empty list (this is unfortunate)") {
            (KFList.of<Int>() == KFList.of<Int>()) shouldBe true
            (KFList.of<Int>() == KFList.of<String>()) shouldBe /* unfortunately */ true
            (KFList.of<Int>() === KFList.of<Int>()) shouldBe false
            (KFList.of<Int>() === KFList.of<String>()) shouldBe false
        }
        expect("list of one") {
            (KFList.of<Int>().prepend(1) == KFList.of<Int>().prepend(1)) shouldBe true
            (KFList.of<String>().prepend("A") == KFList.of<String>().prepend("A")) shouldBe true
            (KFList.of<Int>().prepend(2) != KFList.of<Int>().prepend(1)) shouldBe true
            (KFList.of<String>().prepend("B") != KFList.of<String>().prepend("A")) shouldBe true
        }
        expect("list of three") {
            (KFList.of<Int>().prepend(1).prepend(2).prepend(3) == KFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            (KFList.of<String>().prepend("A").prepend("B").prepend("C") == KFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
            (KFList.of<Int>().prepend(1).prepend(1).prepend(3) != KFList.of<Int>().prepend(1).prepend(2).prepend(3)) shouldBe true
            (KFList.of<String>().prepend("A").prepend("A").prepend("C") != KFList.of<String>().prepend("A").prepend("B").prepend("C")) shouldBe true
        }
    }

    context("fix") {
        expect("return internal list") {
            val fl = flistBuilder(FLNil,0,10)
            val kfl = KFList.of(fl)
            kfl.fix() shouldBe fl
            (kfl.fix() === fl) shouldBe true
        }
        expect("return empty") {
            val kfl = KFList.of<Int>()
            kfl.fix() shouldBe FLNil
            (kfl.fix() === FLNil) shouldBe true
        }
    }

    context("fnel") {
        expect("return internal list") {
            val fl = flistBuilder(FLNil,0,10)
            val kfl = KFList.of(fl)
            kfl.fnel().kind shouldBe kfl
            (kfl.fnel() as FLNel).fnel shouldBe fl
        }
        expect("return ???") {
            val kfl = KFList.of<Int>()
            shouldThrow<IllegalStateException> {
                kfl.fnel()
            }
        }
    }

})