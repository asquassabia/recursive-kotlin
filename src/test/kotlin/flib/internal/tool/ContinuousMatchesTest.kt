package flib.internal.tool

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.rs.SFList
import org.xrpn.flib.internal.tool.continuousMatches

class ContinuousMatchesTest : ExpectSpec({

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
            continuousMatches(
                SFList.of<String>().prepend("A").fix(),
                SFList.of<String>().prepend("A").fix()
            ) shouldBe 1
        }
        expect("0 for homogeneous but different lists of 1 element") {
            continuousMatches(
                SFList.of<Int>().prepend(1).fix(),
                SFList.of<Int>().prepend(2).fix()) shouldBe 0
            continuousMatches(
                SFList.of<String>().prepend("A").fix(),
                SFList.of<String>().prepend("B").fix()
            ) shouldBe 0
        }
        expect("0 for heterogeneous lists of 1") {
            continuousMatches(
                SFList.of<Int>().prepend(1).fix(),
                SFList.of<String>().prepend("1").fix()
            ) shouldBe 0
        }
        expect("2 for equal lists of 2 elements") {
            continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2).fix(),
                SFList.of<Int>().prepend(1).prepend(2).fix()
            ) shouldBe 2
            continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").fix(),
                SFList.of<String>().prepend("A").prepend("B").fix()
            ) shouldBe 2
        }
        expect("3 for equal lists of 3 elements") {
            val a = SFList.of<Int>().prepend(1)
            val b = a.prepend(2)
            val c = b.prepend(3)
            val d = c.fix()
            continuousMatches(
                d,
                SFList.of<Int>().prepend(1).prepend(2).prepend(3).fix()
            ) shouldBe 3
            continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").prepend("C").fix(),
                SFList.of<String>().prepend("A").prepend("B").prepend("C").fix()
            ) shouldBe 3
        }
        expect("2 for lists of 3 elements differing in third place") {
            continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2).prepend(3).fix(),
                SFList.of<Int>().prepend(3).prepend(2).prepend(3).fix()
            ) shouldBe 2
            continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").prepend("C").fix(),
                SFList.of<String>().prepend("C").prepend("B").prepend("C").fix()
            ) shouldBe 2
        }
        expect("1 for lists of 3 elements differing in second place") {
            continuousMatches(
                SFList.of<Int>().prepend(1).prepend(2).prepend(3).fix(),
                SFList.of<Int>().prepend(1).prepend(4).prepend(3).fix()
            ) shouldBe 1
            continuousMatches(
                SFList.of<String>().prepend("A").prepend("B").prepend("C").fix(),
                SFList.of<String>().prepend("A").prepend("D").prepend("C").fix()
            ) shouldBe 1
        }
    }})