package flib.impl

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.impl.FListKind
import org.xrpn.flib.impl.prepend

class FListKindCompanionTest : ExpectSpec({

    context("empty()") {
        val autS = FListKind.empty<String>()
        val autI = FListKind.empty<Int>()
        expect("an empty list") {
            autS.empty shouldBe true
            autS shouldBe /*unfortunately */ autI
        }
    }

    context("continuousMatches") {
        expect("0 for empty lists") {
            FListKind.continuousMatches(FListKind.empty<Int>(), FListKind.empty<Int>()) shouldBe 0
            FListKind.continuousMatches(FListKind.empty<Int>(), FListKind.empty<String>()) shouldBe 0
        }
        expect("1 for equal lists of 1 element") {
            FListKind.continuousMatches(
                FListKind.empty<Int>().prepend(1),
                FListKind.empty<Int>().prepend(1)
            ) shouldBe 1
            FListKind.continuousMatches(
                FListKind.empty<String>().prepend("A"),
                FListKind.empty<String>().prepend("A")
            ) shouldBe 1
        }
        expect("0 for homogeneous but different lists of 1 element") {
            FListKind.continuousMatches(FListKind.empty<Int>().prepend(1), FListKind.empty<Int>().prepend(2)) shouldBe 0
            FListKind.continuousMatches(
                FListKind.empty<String>().prepend("A"),
                FListKind.empty<String>().prepend("B")
            ) shouldBe 0
        }
        expect("0 for heterogeneous lists of 1") {
            FListKind.continuousMatches(
                FListKind.empty<Int>().prepend(1),
                FListKind.empty<String>().prepend("1")
            ) shouldBe 0
        }
        expect("2 for equal lists of 2 elements") {
            FListKind.continuousMatches(
                FListKind.empty<Int>().prepend(1).prepend(2),
                FListKind.empty<Int>().prepend(1).prepend(2)
            ) shouldBe 2
            FListKind.continuousMatches(
                FListKind.empty<String>().prepend("A").prepend("B"),
                FListKind.empty<String>().prepend("A").prepend("B")
            ) shouldBe 2
        }
        expect("3 for equal lists of 3 elements") {
            FListKind.continuousMatches(
                FListKind.empty<Int>().prepend(1).prepend(2).prepend(3),
                FListKind.empty<Int>().prepend(1).prepend(2).prepend(3)
            ) shouldBe 3
            FListKind.continuousMatches(
                FListKind.empty<String>().prepend("A").prepend("B").prepend("C"),
                FListKind.empty<String>().prepend("A").prepend("B").prepend("C")
            ) shouldBe 3
        }
        expect("2 for lists of 3 elements differing in third place") {
            FListKind.continuousMatches(
                FListKind.empty<Int>().prepend(1).prepend(2).prepend(3),
                FListKind.empty<Int>().prepend(3).prepend(2).prepend(3)
            ) shouldBe 2
            FListKind.continuousMatches(
                FListKind.empty<String>().prepend("A").prepend("B").prepend("C"),
                FListKind.empty<String>().prepend("C").prepend("B").prepend("C")
            ) shouldBe 2
        }
        expect("1 for lists of 3 elements differing in second place") {
            FListKind.continuousMatches(
                FListKind.empty<Int>().prepend(1).prepend(2).prepend(3),
                FListKind.empty<Int>().prepend(1).prepend(4).prepend(3)
            ) shouldBe 1
            FListKind.continuousMatches(
                FListKind.empty<String>().prepend("A").prepend("B").prepend("C"),
                FListKind.empty<String>().prepend("A").prepend("D").prepend("C")
            ) shouldBe 1
        }
    }
})