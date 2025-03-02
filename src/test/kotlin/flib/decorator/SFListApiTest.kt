package flib.decorator

import flib.LARGE_DEPTH
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.internal.IdMe

class SFListApiTest : ExpectSpec({

    val sfl10 = SFList.ofIntSeq(0,10)
    val sflLarge = SFList.ofIntSeq(0,LARGE_DEPTH)
    val sflLargeRev = SFList.ofIntSeqRev(0,LARGE_DEPTH)


    context("append") {
        expect("TODO") {
            TODO("implement this test")
        }
    }

    context("count") {
        val aut0 = SFList.of<Int>()
        val aut1 = aut0.prepend(1)
        val aut2 = aut1.prepend(2)
        val aut3 = aut2.prepend(3)
        expect("0 if empty") {
            aut0.count { true } shouldBe 0
        }
        expect("0 if no match") {
            aut0.count { it == 0 } shouldBe 0
        }
        expect("1 if ome match") {
            aut3.count { it == 1 } shouldBe 1
            aut3.count { it == 2 } shouldBe 1
            aut3.count { it == 3 } shouldBe 1
        }
        expect("2 if two match") {
            aut3.count { 1 < it } shouldBe 2
            aut3.count { it != 2 } shouldBe 2
            aut3.count { it < 3 } shouldBe 2
        }

        expect("same as size() if all match") {
            aut1.count { true } shouldBe aut1.size
            aut2.count { true } shouldBe aut2.size
            aut3.count { true } shouldBe aut3.size
        }
    }

    context("equal") {
        val autAI0 = SFList.of<Int>()
        val autBI0 = SFList.of<Int>()
        val autCI0 = SFList.of<Int>()
        val autAS0 = SFList.of<String>()
        val autBS0 = SFList.of<String>()
        val autCS0 = SFList.of<String>()

        expect("is true for self") {
            autAI0.equal(autAI0) shouldBe true
            /* GLITCH due to delegate interference with type equality */
            autAI0.equal(object : IdMe {
                override val hash = autAI0.hash
                override val show = ""
            }) shouldBe /* unfortunately */ true
        }
        expect("is true for empty") {
            autAI0.equal(autBI0) shouldBe true
            autAS0.equal(autBS0) shouldBe true
        }
        val autAI1 = autAI0.prepend(1)
        val autBI1 = autBI0.prepend(2)
        val autCI1 = autCI0.prepend(1)
        val autAS1 = autAS0.prepend("A")
        val autBS1 = autBS0.prepend("B")
        val autCS1 = autCS0.prepend("A")
        expect("is true for single element when equal") {
            autAI1.equal(autAI1) shouldBe true
            autAI1.equal(autCI1) shouldBe true
            autAS1.equal(autAS1) shouldBe true
            autAS1.equal(autCS1) shouldBe true
        }
        expect("is false for single element when not equal") {
            autAI1.equal(autBI1) shouldBe false
            autAS1.equal(autBS1) shouldBe false
        }
        val autAI2 = autAI1.prepend(2) // 2,1
        val autBI2 = autBI1.prepend(1) // 1,2
        val autCI2 = autCI1.prepend(2) // 2,1
        val autAS2 = autAS1.prepend("B") // B,A
        val autBS2 = autBS1.prepend("A") // A,B
        val autCS2 = autCS1.prepend("B") // B,A
        expect("is true for two elements when equal") {
            autAI2.equal(autAI2) shouldBe true
            autAI2.equal(autCI2) shouldBe true
            autAS2.equal(autAS2) shouldBe true
            autAS2.equal(autCS2) shouldBe true
        }
        expect("is false for two elements when not equal") {
            autAI2.equal(autBI2) shouldBe false
            autAS2.equal(autBS2) shouldBe false
        }
        val autAI3 = autAI2.prepend(2) // 2,2,1
        val autBI3 = autBI0.prepend(1).prepend(2).prepend(3) // 3,2,1
        val autCI3 = autCI2.prepend(2) // 2,2,1
        val autAS3 = autAS2.prepend("B") // B,B,A
        val autBS3 = autBS0.prepend("A").prepend("B").prepend("C") // C,B,A
        val autCS3 = autCS2.prepend("B") // B,B,A
        expect("is true for three elements when equal") {
            autAI3.equal(autAI3) shouldBe true
            autAI3.equal(autCI3) shouldBe true
            autAS3.equal(autAS3) shouldBe true
            autAS3.equal(autCS3) shouldBe true
        }
        expect("is false for three elements when not equal") {
            autAI3.equal(autBI3) shouldBe false
            autAS3.equal(autBS3) shouldBe false
        }
    }

    context("fold") {
        expect ("in terms of foldLeft") {
            val fres = sfl10.fold(0) { a, b -> a - b }
            val flres = sfl10.foldLeft(0) { a, b -> a - b }
            val frres = sfl10.foldRight(0) { a, b -> a - b }
            fres shouldBe flres
            fres shouldNotBe frres
        }
    }

    context("foldLeft") {
        expect("copy empty") {
            val autI0 = SFList.of<Int>()
            val aut = SFList.of(autI0.foldLeft(SFList.of<Int>().fix()) { l, item -> FLCons(item,l) })
            aut.size shouldBe 0
        }
        expect("copy one") {
            val autI1 = SFList.of<Int>().prepend(1)
            val aut = SFList.of(autI1.foldLeft(SFList.of<Int>().fix()) { l, item -> FLCons(item,l) })
            aut.equal(autI1) shouldBe true
        }
        expect("copy two") {
            val autI2 = SFList.of<Int>().prepend(1).prepend(2)
            val aut = SFList.of(autI2.foldLeft(SFList.of<Int>().fix()) { l, item -> FLCons(item,l) })
            aut.reverse().equal(autI2) shouldBe true
        }
        expect("copy three") {
            val autI3 = SFList.of<Int>().prepend(1).prepend(2).prepend(3)
            val aut = SFList.of(autI3.foldLeft(SFList.of<Int>().fix()) { l, item -> FLCons(item,l) })
            aut.reverse().equal(autI3) shouldBe true
        }
    }

    context("foldRight") {
        expect("copy empty") {
            val autI0 = SFList.of<Int>()
            val aut = SFList.of(autI0.foldRight(SFList.of<Int>().fix()) { item, l -> FLCons(item,l) })
            aut.size shouldBe 0
        }
        expect("copy one") {
            val autI1 = SFList.of<Int>().prepend(1)
            val aut = SFList.of(autI1.foldRight(SFList.of<Int>().fix()) { item, l -> FLCons(item,l) })
            aut.equal(autI1) shouldBe true
        }
        expect("copy two") {
            val autI2 = SFList.of<Int>().prepend(1).prepend(2)
            val aut = SFList.of(autI2.foldRight(SFList.of<Int>().fix()) { item, l -> FLCons(item,l) })
            aut.equal(autI2) shouldBe true
        }
        expect("copy three") {
            val autI3 = SFList.of<Int>().prepend(1).prepend(2).prepend(3)
            val aut = SFList.of(autI3.foldRight(SFList.of<Int>().fix()) { item, l -> FLCons(item,l) })
            aut.equal(autI3) shouldBe true
        }
    }

    context("head") {
        val aut0 = SFList.of<Int>()
        expect("is null for empty list") {
            aut0.head() shouldBe null
        }
        val aut1 = aut0.prepend(1)
        expect("is the only element") {
            aut1.head() shouldBe 1
        }
        val aut2 = aut1.prepend(2)
        expect("is the first element") {
            aut2.head() shouldBe 2
        }
        val aut3 = aut2.prepend(3)
        expect("is the first element of three") {
            aut3.head() shouldBe 3
        }
    }

    context("init") {
        expect("TODO") {
            TODO("implement this test")
        }
    }

    context("last") {
        expect("TODO") {
            TODO("implement this test")
        }
    }

    context("pick") {
        val aut0 = SFList.of<Int>()
        expect("is null for empty list") {
            aut0.pick() shouldBe null
        }
        val aut1 = aut0.prepend(1)
        expect("is the only element") {
            aut1.pick() shouldBe 1
        }
        val aut2 = aut1.prepend(2)
        expect("is the first element") {
            aut2.pick() shouldBe 2
        }
    }

    context("prepend") {
        val aut0 = SFList.of<Int>()
        val aut1 = aut0.prepend(1)
        expect("one") {
            aut1.size shouldBe 1
            aut0.size shouldBe 0
        }
        val aut2 = aut1.prepend(1)
        expect("one more") {
            aut2.size shouldBe 2
            aut1.size shouldBe 1
            aut0.size shouldBe 0
        }
        expect("arbitrary type and subtype") {
            open class SupSup()
            open class Sup(): SupSup()
            class Sub() : Sup()
            val autF0 = SFList.of<Sup>()
            val autF1 = autF0.prepend(Sup())
            autF1.size shouldBe 1
            val autF2 = autF1.prepend(Sub())
            autF2.size shouldBe 2
            val autF3 = autF2.prepend(Sup())
            autF3.size shouldBe 3
            // should not compile
            // val autF4 = autF1.prepend(SupSup())
        }
    }

    context("reverse") {
        expect("a distinct empty list when empty") {
            val autI0 = SFList.of<Int>()
            val autA = autI0.reverse()
            autA.empty shouldBe true
            autI0.equal(autA) shouldBe true
        }
        expect("a distinct, equal list when one element") {
            val autI1 = SFList.of<Int>().prepend(1)
            val autA = autI1.reverse()
            autA.size shouldBe 1
            autI1.equal(autA) shouldBe true
        }
        expect("a reversed list when two elements") {
            val autI2 = SFList.of<Int>().prepend(1).prepend(2) // 2,1
            val autA = autI2.reverse() // 1,2
            autA.size shouldBe 2
            autA.equal(SFList.of<Int>().prepend(2).prepend(1)) shouldBe true
        }
        expect("a reversed list when three elements") {
            val autI3 = SFList.of<Int>().prepend(1).prepend(2).prepend(3)
            val autA = autI3.reverse()
            autA.size shouldBe 3
            autA.equal(SFList.of<Int>().prepend(3).prepend(2).prepend(1)) shouldBe true
        }
        expect("matches reversed list") {
            sflLarge.reverse().equal(sflLargeRev) shouldBe true
        }
    }

    context("tail") {
        expect("TODO") {
            TODO("implement this test")
        }
    }

})
