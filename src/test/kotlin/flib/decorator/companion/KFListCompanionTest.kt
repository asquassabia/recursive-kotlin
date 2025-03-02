package flib.decorator.companion

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.internal.tool.continuousMatches
import org.xrpn.flib.internal.tool.flistReverseBuilder

class KFListCompanionTest : ExpectSpec({

    val fl10 = flistReverseBuilder(0, 10)
    context("build empty") {
        val autS = SFList.of<String>()
        val autI = SFList.of<Int>()
        expect("an empty list") {
            autS.empty shouldBe true
            autS shouldBe /*unfortunately */ autI
        }
    }

    context("build not empty") {
        val aut1 = SFList.of(fl10)
        val aut2 = SFList.of(fl10)
        expect("a non empty list") {
            aut1.ne shouldBe true
            aut1.fix() shouldBe fl10
            (aut1.fix() === fl10) shouldBe true
            aut2.fix() shouldBe fl10
            (aut2.fix() === fl10) shouldBe true
            aut1 shouldBe aut2
            (aut1 === aut2) shouldBe false
        }
    }

    context("continuousMatches") {
        expect("0 for empty lists") {
            continuousMatches(SFList.of<Int>().fix(), SFList.of<Int>().fix()) shouldBe 0
            continuousMatches(SFList.of<Int>().fix(), SFList.of<String>().fix()) shouldBe 0
        }
        expect("1 for equal lists of 1 element") {
            continuousMatches(
                SFList.of<Int>().prepend(1).fix(),
                SFList.of<Int>().prepend(1).fix()
            ) shouldBe 1
//            continuousMatches(
//                SFList.of<String>().prepend("A") as SFList,
//                SFList.of<String>().prepend("A") as SFList
//            ) shouldBe 1
        }
//        expect("0 for homogeneous but different lists of 1 element") {
//            continuousMatches(SFList.of<Int>().prepend(1) as SFList, SFList.of<Int>().prepend(2) as SFList) shouldBe 0
//            continuousMatches(
//                SFList.of<String>().prepend("A") as SFList,
//                SFList.of<String>().prepend("B") as SFList
//            ) shouldBe 0
//        }
//        expect("0 for heterogeneous lists of 1") {
//            continuousMatches(
//                SFList.of<Int>().prepend(1) as SFList,
//                SFList.of<String>().prepend("1") as SFList
//            ) shouldBe 0
//        }
//        expect("2 for equal lists of 2 elements") {
//            continuousMatches(
//                SFList.of<Int>().prepend(1).prepend(2) as SFList,
//                SFList.of<Int>().prepend(1).prepend(2) as SFList
//            ) shouldBe 2
//            continuousMatches(
//                SFList.of<String>().prepend("A").prepend("B") as SFList,
//                SFList.of<String>().prepend("A").prepend("B") as SFList
//            ) shouldBe 2
//        }
//        expect("3 for equal lists of 3 elements") {
//            continuousMatches(
//                SFList.of<Int>().prepend(1).prepend(2).prepend(3) as SFList,
//                SFList.of<Int>().prepend(1).prepend(2).prepend(3) as SFList
//            ) shouldBe 3
//            continuousMatches(
//                SFList.of<String>().prepend("A").prepend("B").prepend("C") as SFList,
//                SFList.of<String>().prepend("A").prepend("B").prepend("C") as SFList
//            ) shouldBe 3
//        }
//        expect("2 for lists of 3 elements differing in third place") {
//            continuousMatches(
//                SFList.of<Int>().prepend(1).prepend(2).prepend(3) as SFList,
//                SFList.of<Int>().prepend(3).prepend(2).prepend(3) as SFList
//            ) shouldBe 2
//            continuousMatches(
//                SFList.of<String>().prepend("A").prepend("B").prepend("C") as SFList,
//                SFList.of<String>().prepend("C").prepend("B").prepend("C") as SFList
//            ) shouldBe 2
//        }
//        expect("1 for lists of 3 elements differing in second place") {
//            continuousMatches(
//                SFList.of<Int>().prepend(1).prepend(2).prepend(3) as SFList,
//                SFList.of<Int>().prepend(1).prepend(4).prepend(3) as SFList
//            ) shouldBe 1
//            continuousMatches(
//                SFList.of<String>().prepend("A").prepend("B").prepend("C") as SFList,
//                SFList.of<String>().prepend("A").prepend("D").prepend("C") as SFList
//            ) shouldBe 1
//        }
    }
})