package flib.decorator.companion

import flib.flistReverseBuilder
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FNel
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.decorator.prepend

class KFListCompanionTest : ExpectSpec({

    context("build empty") {
        val autS = SFList.of<String>()
        val autI = SFList.of<Int>()
        expect("an empty list") {
            autS.empty shouldBe true
            autS shouldBe /*unfortunately */ autI
        }
    }

    context("build not empty") {
        val fl = flistReverseBuilder(0, 10)
        val aut1 = SFList.of(fl)
        val aut2 = SFList.of(fl)
        expect("a non empty list") {
            aut1.ne shouldBe true
            aut1.fix() shouldBe fl
            (aut1.fix() === fl) shouldBe true
            aut2.fix() shouldBe fl
            (aut2.fix() === fl) shouldBe true
            aut1 shouldBe aut2
            (aut1 === aut2) shouldBe false
            val fnel1 = aut1.fnel() as FNel
            val fnel2 = aut2.fnel() as FNel
            (fnel1.kind === fnel2.kind) shouldBe false
            (fnel1.kind === aut1) shouldBe true
            (fnel2.kind === aut2) shouldBe true
        }
    }

    context("continuousMatches") {
        expect("0 for empty lists") {
            SFList.continuousMatches(SFList.of<Int>(), SFList.of<Int>()) shouldBe 0
            SFList.continuousMatches(SFList.of<Int>(), SFList.of<String>()) shouldBe 0
        }
        expect("1 for equal lists of 1 element") {
            SFList.continuousMatches(
                SFList.of<Int>().prepend(1),
                SFList.of<Int>().prepend(1)
            ) shouldBe 1
            SFList.continuousMatches(
                SFList.of<String>().prepend("A"),
                SFList.of<String>().prepend("A")
            ) shouldBe 1
        }
        expect("0 for homogeneous but different lists of 1 element") {
            SFList.continuousMatches(SFList.of<Int>().prepend(1), SFList.of<Int>().prepend(2)) shouldBe 0
            SFList.continuousMatches(
                SFList.of<String>().prepend("A"),
                SFList.of<String>().prepend("B")
            ) shouldBe 0
        }
        expect("0 for heterogeneous lists of 1") {
            SFList.continuousMatches(
                SFList.of<Int>().prepend(1),
                SFList.of<String>().prepend("1")
            ) shouldBe 0
        }
        expect("2 for equal lists of 2 elements") {
            SFList.continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2),
                SFList.of<Int>().prepend(1).prepend(2)
            ) shouldBe 2
            SFList.continuousMatches(
                SFList.of<String>().prepend("A").prepend("B"),
                SFList.of<String>().prepend("A").prepend("B")
            ) shouldBe 2
        }
        expect("3 for equal lists of 3 elements") {
            SFList.continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2).prepend(3),
                SFList.of<Int>().prepend(1).prepend(2).prepend(3)
            ) shouldBe 3
            SFList.continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").prepend("C"),
                SFList.of<String>().prepend("A").prepend("B").prepend("C")
            ) shouldBe 3
        }
        expect("2 for lists of 3 elements differing in third place") {
            SFList.continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2).prepend(3),
                SFList.of<Int>().prepend(3).prepend(2).prepend(3)
            ) shouldBe 2
            SFList.continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").prepend("C"),
                SFList.of<String>().prepend("C").prepend("B").prepend("C")
            ) shouldBe 2
        }
        expect("1 for lists of 3 elements differing in second place") {
            SFList.continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2).prepend(3),
                SFList.of<Int>().prepend(1).prepend(4).prepend(3)
            ) shouldBe 1
            SFList.continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").prepend("C"),
                SFList.of<String>().prepend("A").prepend("D").prepend("C")
            ) shouldBe 1
        }
    }
})