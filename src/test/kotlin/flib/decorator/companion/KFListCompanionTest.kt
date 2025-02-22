package flib.decorator.companion

import flib.flistReverseBuilder
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.impl.KFList
import org.xrpn.flib.impl.prepend

class KFListCompanionTest : ExpectSpec({

    context("build empty") {
        val autS = KFList.of<String>()
        val autI = KFList.of<Int>()
        expect("an empty list") {
            autS.empty shouldBe true
            autS shouldBe /*unfortunately */ autI
        }
    }

    context("build not empty") {
        val fl = flistReverseBuilder(FLNil,0, 10)
        val aut1 = KFList.of(fl)
        val aut2 = KFList.of(fl)
        expect("a non empty list") {
            aut1.ne shouldBe true
            aut1.fix() shouldBe fl
            (aut1.fix() === fl) shouldBe true
            aut2.fix() shouldBe fl
            (aut2.fix() === fl) shouldBe true
            aut1 shouldBe aut2
            (aut1 === aut2) shouldBe false
            (aut1.fnel().kind === aut2.fnel().kind) shouldBe false
            (aut1.fnel().kind === aut1) shouldBe true
            (aut2.fnel().kind === aut2) shouldBe true
        }
    }

    context("continuousMatches") {
        expect("0 for empty lists") {
            KFList.continuousMatches(KFList.of<Int>(), KFList.of<Int>()) shouldBe 0
            KFList.continuousMatches(KFList.of<Int>(), KFList.of<String>()) shouldBe 0
        }
        expect("1 for equal lists of 1 element") {
            KFList.continuousMatches(
                KFList.of<Int>().prepend(1),
                KFList.of<Int>().prepend(1)
            ) shouldBe 1
            KFList.continuousMatches(
                KFList.of<String>().prepend("A"),
                KFList.of<String>().prepend("A")
            ) shouldBe 1
        }
        expect("0 for homogeneous but different lists of 1 element") {
            KFList.continuousMatches(KFList.of<Int>().prepend(1), KFList.of<Int>().prepend(2)) shouldBe 0
            KFList.continuousMatches(
                KFList.of<String>().prepend("A"),
                KFList.of<String>().prepend("B")
            ) shouldBe 0
        }
        expect("0 for heterogeneous lists of 1") {
            KFList.continuousMatches(
                KFList.of<Int>().prepend(1),
                KFList.of<String>().prepend("1")
            ) shouldBe 0
        }
        expect("2 for equal lists of 2 elements") {
            KFList.continuousMatches(
                KFList.of<Int>().prepend(1).prepend(2),
                KFList.of<Int>().prepend(1).prepend(2)
            ) shouldBe 2
            KFList.continuousMatches(
                KFList.of<String>().prepend("A").prepend("B"),
                KFList.of<String>().prepend("A").prepend("B")
            ) shouldBe 2
        }
        expect("3 for equal lists of 3 elements") {
            KFList.continuousMatches(
                KFList.of<Int>().prepend(1).prepend(2).prepend(3),
                KFList.of<Int>().prepend(1).prepend(2).prepend(3)
            ) shouldBe 3
            KFList.continuousMatches(
                KFList.of<String>().prepend("A").prepend("B").prepend("C"),
                KFList.of<String>().prepend("A").prepend("B").prepend("C")
            ) shouldBe 3
        }
        expect("2 for lists of 3 elements differing in third place") {
            KFList.continuousMatches(
                KFList.of<Int>().prepend(1).prepend(2).prepend(3),
                KFList.of<Int>().prepend(3).prepend(2).prepend(3)
            ) shouldBe 2
            KFList.continuousMatches(
                KFList.of<String>().prepend("A").prepend("B").prepend("C"),
                KFList.of<String>().prepend("C").prepend("B").prepend("C")
            ) shouldBe 2
        }
        expect("1 for lists of 3 elements differing in second place") {
            KFList.continuousMatches(
                KFList.of<Int>().prepend(1).prepend(2).prepend(3),
                KFList.of<Int>().prepend(1).prepend(4).prepend(3)
            ) shouldBe 1
            KFList.continuousMatches(
                KFList.of<String>().prepend("A").prepend("B").prepend("C"),
                KFList.of<String>().prepend("A").prepend("D").prepend("C")
            ) shouldBe 1
        }
    }
})