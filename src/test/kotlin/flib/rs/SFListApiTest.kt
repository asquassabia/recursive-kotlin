package flib.rs

import flib.LARGE_DEPTH
import flib.UNSAFE_DEPTH
import flib.XLARGE_DEPTH
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.xrpn.flib.SAFE_RECURSION_SIZE
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.rs.SFList
import org.xrpn.flib.internal.IdMe

class SFListApiTest : ExpectSpec({

    assert(SAFE_RECURSION_SIZE.get() < UNSAFE_DEPTH)
    val sfl10 = SFList.ofIntSeq(0,10)
    val sflLarge1 = SFList.ofIntSeq(0, LARGE_DEPTH)
    val sflXLarge1 = SFList.ofIntSeq(0, XLARGE_DEPTH)
    val sflUnsafe1 = SFList.ofIntSeq(0,UNSAFE_DEPTH)
    val sflUnsafe2 = SFList.ofIntSeq(0,UNSAFE_DEPTH)
    val sflUnsafeRev = SFList.ofIntSeqRev(0,UNSAFE_DEPTH)

    context("append") {
        val aut0 = SFList.of<Int>()
        val aut1 = aut0.append(1)
        val aut12 = aut1.append(2)
        val aut123 = aut12.append(3)
        expect("to empty") {
            aut0.append(1) shouldBe aut0.prepend(1)
        }
        expect("two items") {
            val aut = aut1.append(2)
            aut0.append(1).append(2) shouldBe aut
            aut.reverse() shouldBe aut0.prepend(1).prepend(2)
        }
        expect("three items") {
            val aut = aut12.append(3)
            aut0.append(1).append(2).append(3).equal(aut) shouldBe true
            aut.reverse() shouldBe aut0.prepend(1).prepend(2).prepend(3)
        }
        expect("safe for large") {
            val aut = sflUnsafe1.append(1)
            aut.size shouldBe UNSAFE_DEPTH+1
            aut.head() shouldBe 0
            aut.last() shouldBe 1
            aut.init().equal(sflUnsafe1) shouldBe true
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
            sflUnsafe1.count { true } shouldBe UNSAFE_DEPTH
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
        expect ("safe for large") {
            sflUnsafe1.equal(sflUnsafe2) shouldBe true
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
        expect("safe for large") {
            val aut = SFList.of(sflUnsafe1.foldLeft(SFList.of<Int>().fix()) { l, item -> FLCons(item,l) })
            aut.equal(sflUnsafeRev) shouldBe true
            (aut === sflUnsafe1) shouldBe false
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
        expect("safe for large") {
            val aut = SFList.of(sflUnsafe1.foldRight(SFList.of<Int>().fix()) { item, l -> FLCons(item,l) })
            aut.equal(sflUnsafe1) shouldBe true
            (aut === sflUnsafe1) shouldBe false
        }
    }

    context("head, tail") {
        val aut0 = SFList.of<Int>()
        expect("is null for empty list") {
            aut0.head() shouldBe null
            aut0.tail() shouldBe SFList.of<Int>()
        }
        val aut1 = aut0.prepend(1)
        expect("is the only element") {
            aut1.head() shouldBe 1
            aut1.tail() shouldBe SFList.of<Int>()
        }
        val aut2 = aut1.prepend(2)
        expect("is the first element") {
            aut2.head() shouldBe 2
            aut2.tail() shouldBe aut1
        }
        val aut3 = aut2.prepend(3)
        expect("is the first element of three") {
            aut3.head() shouldBe 3
            aut3.tail() shouldBe aut2
        }
        expect("safe for large") {
            sflUnsafe1.head() shouldBe 0
            sflUnsafe2.last() shouldBe UNSAFE_DEPTH-1
            val aut = sflUnsafe1.tail()
            aut.size shouldBe UNSAFE_DEPTH-1
            aut.head() shouldBe 1
            aut.last() shouldBe UNSAFE_DEPTH-1
        }
    }

    context("init, last") {
        val aut0 = SFList.of<Int>()
        expect("is null for empty list") {
            aut0.init() shouldBe aut0
            aut0.last() shouldBe null
        }
        val aut1 = aut0.append(1)
        expect("is the only element") {
            aut1.init() shouldBe aut1
            aut1.last() shouldBe null
        }
        val aut2 = aut1.append(2)
        expect("is the first element of two") {
            aut2.init() shouldBe aut1
            aut2.last() shouldBe 2
        }
        val aut3 = aut2.append(3)
        expect("first two elements of three") {
            aut3.init() shouldBe aut2
            aut3.last() shouldBe 3
        }
        expect("safe for large") {
            sflLarge1.last() shouldBe LARGE_DEPTH-1
            val aut = sflLarge1.init()
            aut.count { true } shouldBe LARGE_DEPTH-1
            aut.head() shouldBe 0
            aut.last() shouldBe LARGE_DEPTH-2
        }
        expect("safe for xlarge") {
            sflXLarge1.size shouldBe XLARGE_DEPTH
            sflXLarge1.last() shouldBe XLARGE_DEPTH-1
            val aut = sflXLarge1.init()
            aut.count { true } shouldBe XLARGE_DEPTH-1
            aut.head() shouldBe 0
            aut.last() shouldBe XLARGE_DEPTH-2
        }
        expect("safe for larger") {
            sflUnsafe1.size shouldBe UNSAFE_DEPTH
            sflUnsafe1.last() shouldBe UNSAFE_DEPTH-1
            val aut = sflUnsafe1.init()
            aut.count { true } shouldBe UNSAFE_DEPTH-1
            aut.head() shouldBe 0
            aut.last() shouldBe UNSAFE_DEPTH-2
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
        expect("safe for large") {
            val aut = sflUnsafe1.prepend(100)
            aut.size shouldBe UNSAFE_DEPTH+1
            aut.head() shouldBe 100
            aut.last() shouldBe UNSAFE_DEPTH-1
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
            sflUnsafe1.reverse().equal(sflUnsafeRev) shouldBe true
        }
    }

    context("size") {
        val aut0 = SFList.of<Int>()
        val aut1 = aut0.prepend(1)
        val aut2 = aut1.prepend(2)
        val aut3 = aut2.prepend(3)
        expect("0 if empty") {
            aut0.size shouldBe 0
        }
        expect("correct size") {
            aut1.size shouldBe 1
            aut2.size shouldBe 2
            aut3.size shouldBe 3
            sflUnsafe1.size shouldBe UNSAFE_DEPTH
        }
    }

    context("map") {
        val aut0 = SFList.of<Char>()
        val ora0 = SFList.of<String>()
        val aut1 = aut0.prepend('a')
        val ora1 = ora0.prepend("a")
        val aut2 = aut1.prepend('z')
        val ora2 = ora1.prepend("z")
        val aut3 = aut1.prepend('m')
        val ora3 = ora1.prepend("m")
        expect("map empty") {
            aut0.map(Char::toString) shouldBe SFList.of<String>()
        }
        expect("map one item") {
            aut1.map(Char::toString) shouldBe ora1
        }
        expect("map two items") {
            aut2.map(Char::toString) shouldBe ora2
        }
        expect("map three items") {
            aut3.map(Char::toString) shouldBe ora3
        }
        expect("map large") {
            val aut = sflUnsafe1.map { i -> -i }
            aut.size shouldBe UNSAFE_DEPTH
            aut.head() shouldBe 0
            aut.tail().head() shouldBe -1
            aut.last() shouldBe -UNSAFE_DEPTH+1
        }
    }

    context("lift") {
        val aut0 = SFList.of<String>()
        expect("String") {
            val aut = aut0.lift("ABC")
            aut shouldBe SFList.of(FLCons("ABC", FLNil<String>()))
            (aut as SFList).isDeep() shouldBe false
        }
        expect("FLKApi<String>") {
            val aux = FLCons("ABC", FLNil<String>())
            val auxaux: FLCons<FLKApi<String>> = FLCons(SFList.of(aux), FLNil<FLKApi<String>>())
            val aut: FLKApi<FLKApi<String>> = aut0.lift(SFList.of(aux))
            aut shouldBe SFList.of(auxaux)
            (aut as SFList).isDeep() shouldBe false
        }
    }

    context("flatMap") {
        expect("FLKApi<String>") {
            val aux1: FLKApi<String> = SFList.of(FLCons("ABC", FLNil<String>()))
            val aux2: FLKApi<String> = SFList.of(FLCons("DEF", FLNil<String>()))
            val aux3: FLKApi<String> = SFList.of(FLCons("GHI", FLCons( "JKL",FLNil<String>())))
            val aux: FLCons<FLKApi<String>> = FLCons(aux1, FLCons(aux2, FLCons(aux3,FLNil())))
            val data: FLKApi<FLKApi<String>> = SFList.of(aux)
            data.toString() shouldBe "SFList@{3}:(SFList@{1}:(ABC, #*), #(SFList@{1}:(DEF, #*), #(SFList@{2}:(GHI, #(JKL, #*)*), #*)*)*)"
            (data as SFList).isDeep() shouldBe false
            fun f(a: FLKApi<String>): FLKApi<String> = a.map{ s: String -> s.lowercase() }
            val autf: FLKApi<String> = data.flatMap(::f)
            autf.toString() shouldBe "SFList@{4}:(abc, #(def, #(ghi, #(jkl, #*)*)*)*)"
            fun g(a: FLKApi<String>): FLKApi<Int> = a.map{ s: String -> s.hashCode() }
            val autg: FLKApi<Int> = data.flatMap(::g)
            autg.toString() shouldBe "SFList@{4}:(64578, #(67557, #(70536, #(73515, #*)*)*)*)"
            "ABC".hashCode() shouldBe 64578
            "JKL".hashCode() shouldBe 73515
        }
    }
})
