package flib.api

import flib.XLARGE_DEPTH
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.api.append
import org.xrpn.flib.api.appendSafe
import org.xrpn.flib.api.count
import org.xrpn.flib.api.empty
import org.xrpn.flib.api.foldLeft
import org.xrpn.flib.api.fold
import org.xrpn.flib.api.foldRight
import org.xrpn.flib.api.foldRightSafe
import org.xrpn.flib.api.head
import org.xrpn.flib.api.init
import org.xrpn.flib.api.initSafe
import org.xrpn.flib.api.last
import org.xrpn.flib.api.ne
import org.xrpn.flib.api.pick
import org.xrpn.flib.api.prepend
import org.xrpn.flib.api.reverse
import org.xrpn.flib.api.size
import org.xrpn.flib.api.tail
import org.xrpn.flib.internal.tool.flistBuilder
import org.xrpn.flib.internal.tool.flistReverseBuilder

class FListApiTest : ExpectSpec({

    val listOf1 = FLCons(1, FLNil<Int>())
    val listOf1s = FLCons("1", FLNil<String>())
    val listOf2 = FLCons(2, FLNil<Int>())
    val listOf3 = FLCons(3, FLNil<Int>())
    val listOf12 = FLCons(1, FLCons(2, FLNil<Int>()))
    val listOf12s = FLCons("1", FLCons("2", FLNil<String>()))
    val listOf21 = FLCons(2, FLCons(1, FLNil<Int>()))
    val listOf21s = FLCons("2", FLCons("1", FLNil<String>()))
    val listOf23 = FLCons(2, FLCons(3, FLNil<Int>()))
    val listOf31 = FLCons(3, FLCons(1, FLNil<Int>()))
    val listOf123 = FLCons(1, FLCons(2, FLCons(3, FLNil<Int>())))
    val listOf123s = FLCons("1", FLCons("2", FLCons("3", FLNil<String>())))
    val listOf321 = FLCons(3, FLCons(2, FLCons(1, FLNil<Int>())))
    val listOf321s = FLCons("3", FLCons("2", FLCons("1", FLNil<String>())))
    val listOf312 = FLCons(3, FLCons(1, FLCons(2, FLNil<Int>())))
    val listThousand = flistBuilder(0,1000)
    val listThousandR = flistReverseBuilder(0,1000)
    val listUnsafe = flistBuilder(0, XLARGE_DEPTH)
    val seq1to100 = flistBuilder(1,101)
    val sum: (Int, Int) -> Int = { a, b -> a + b }
    val diff: (Int, Int) -> Int = { a, b -> b - a }
    val isEven: (Int) -> Boolean = { i -> i%2 == 0 }
    val isOdd: (Int) -> Boolean = { i -> i%2 != 0 }

    context("size()") {
        expect("FLNil") {
            FLNil<Int>().size() shouldBe 0
        }
        expect("list of 1, of 2, of 3") {
            listOf1.size() shouldBe 1
            listOf12.size() shouldBe 2
            listOf123.size() shouldBe 3
        }
        expect("list of 1000") {
            listThousand.size() shouldBe 1000
        }
        expect("list huge") {
            listUnsafe.size() shouldBe XLARGE_DEPTH
        }
    }

    context("empty(), ne()") {
        expect("FLNil") {
            FLNil<Int>().empty() shouldBe true
            FLNil<Int>().ne() shouldBe false
        }
        expect("list of 1, of 2, of 3") {
            listOf1.empty() shouldBe false
            listOf1.ne() shouldBe true
            listOf23.empty() shouldBe false
            listOf23.ne() shouldBe true
            listOf312.empty() shouldBe false
            listOf312.ne() shouldBe true
        }
        expect("list huge") {
            listUnsafe.empty() shouldBe false
            listUnsafe.ne() shouldBe true
        }
    }

    context("head(), tail()") {
        expect("FLNil") {
            FLNil<Int>().head() shouldBe null
            FLNil<Int>().tail() shouldBe FLNil<Int>()
        }
        expect("list of 1") {
            listOf1.head() shouldBe 1
            listOf1.tail() shouldBe FLNil<Int>()
            listOf2.head() shouldBe 2
            listOf2.tail() shouldBe FLNil<Int>()
        }
        expect("list of 2") {
            listOf12.head() shouldBe 1
            listOf12.tail() shouldBe listOf2
            listOf23.head() shouldBe 2
            listOf23.tail() shouldBe listOf3
        }
        expect("list of 3") {
            listOf123.head() shouldBe 1
            listOf123.tail() shouldBe listOf23
            listOf312.head() shouldBe 3
            listOf312.tail() shouldBe listOf12
        }
        expect("list of 1000") {
            listThousand.head() shouldBe 0
            val aut = listThousand.tail()
            aut.size() shouldBe 999
            aut.head() shouldBe 1
        }
        expect("list huge") {
            listUnsafe.head() shouldBe 0
            val aut = listUnsafe.tail()
            aut.size() shouldBe XLARGE_DEPTH-1
            aut.head() shouldBe 1
        }
    }

    context("init(), last()") {
        expect("FLNil") {
            FLNil<Int>().init() shouldBe FLNil<Int>()
            FLNil<Int>().last() shouldBe null
        }
        expect("list of 1") {
            listOf1.init() shouldBe listOf1
            listOf1.last() shouldBe null
            listOf2.init() shouldBe listOf2
            listOf2.last() shouldBe null
        }
        expect("list of 2") {
            listOf12.init() shouldBe listOf1
            listOf12.last() shouldBe 2
            listOf23.init() shouldBe listOf2
            listOf23.last() shouldBe 3
        }
        expect("list of 3") {
            listOf123.init() shouldBe listOf12
            listOf123.last() shouldBe 3
            listOf312.init() shouldBe listOf31
            listOf312.last() shouldBe 2
        }
        expect("list of 1000") {
            listThousand.last() shouldBe 999
            val aut = listThousand.init()
            aut.size() shouldBe 999
            aut.head() shouldBe 0
            aut.last() shouldBe 998
        }
        expect("list huge last") {
            listUnsafe.last() shouldBe XLARGE_DEPTH - 1
        }
        expect("list huge init") {
            shouldThrow<StackOverflowError> {
                listUnsafe.init()
            }
        }
    }

    context("initSafe()") {
        expect("FLNil") {
            FLNil<Int>().initSafe() shouldBe FLNil<Int>()
        }
        expect("list of 1") {
            listOf1.initSafe() shouldBe listOf1
            listOf2.initSafe() shouldBe listOf2
        }
        expect("list of 2") {
            listOf12.initSafe() shouldBe listOf1
            listOf23.initSafe() shouldBe listOf2
        }
        expect("list of 3") {
            listOf123.initSafe() shouldBe listOf12
            listOf312.initSafe() shouldBe listOf31
        }
        expect("list of 1000") {
            val aut = listThousand.initSafe()
            aut.size() shouldBe 999
            aut.head() shouldBe 0
            aut.last() shouldBe 998
        }
        expect("list huge") {
            val aut = listUnsafe.initSafe()
            aut.size() shouldBe XLARGE_DEPTH - 1
            aut.head() shouldBe 0
            aut.last() shouldBe XLARGE_DEPTH - 2
        }
    }

    context("append()") {
        expect("FLNil") {
            FLNil<Int>().append(1) shouldBe FLCons(1, FLNil<Int>())
        }
        expect("list of 1, of 2, of 3") {
            val app2 = listOf1.append(2)
            app2 shouldBe listOf12
            app2.toString() shouldBe "FLCons(head=1, tail=FLCons(head=2, tail=FLNil()))"
            val app3 = app2.append(3)
            app3 shouldBe listOf123
            app3.toString() shouldBe "FLCons(head=1, tail=FLCons(head=2, tail=FLCons(head=3, tail=FLNil())))"
        }
        expect("list of 1000") {
            val aut = listThousand.append(2000)
            aut.head() shouldBe 0
            aut.size() shouldBe 1001
            aut.init() shouldBe listThousand
            aut.last() shouldBe 2000
        }
        expect("list huge") {
            shouldThrow<StackOverflowError> {
                listUnsafe.append(XLARGE_DEPTH * 2)
            }
        }
    }

    context("appendSafe()") {
        expect("FLNil") {
            FLNil<Int>().appendSafe(1) shouldBe FLCons(1, FLNil<Int>())
        }
        expect("list of 1, of 2, of 3") {
            val app2 = listOf1.appendSafe(2)
            app2 shouldBe listOf12
            app2.toString() shouldBe "FLCons(head=1, tail=FLCons(head=2, tail=FLNil()))"
            val app3 = app2.appendSafe(3)
            app3 shouldBe listOf123
            app3.toString() shouldBe "FLCons(head=1, tail=FLCons(head=2, tail=FLCons(head=3, tail=FLNil())))"
        }
        expect("list of 1000") {
            val aut = listThousand.appendSafe(2000)
            aut.head() shouldBe 0
            aut.size() shouldBe 1001
            aut.init() shouldBe listThousand
            aut.last() shouldBe 2000
        }
        expect("list huge") {
            val aut = listUnsafe.appendSafe(XLARGE_DEPTH*2)
            aut.head() shouldBe 0
            aut.size() shouldBe XLARGE_DEPTH+1
            shouldThrow<StackOverflowError> {
                // this is a limitation for recursive equals()
                aut.initSafe().equals(listUnsafe) shouldBe true
                //             ^^^^^^
            }
            shouldThrow<StackOverflowError> {
                // this is such limitation fort the test harness
                aut.initSafe() shouldBe listUnsafe
                //             ^^^^^^^^
            }
            aut.last() shouldBe XLARGE_DEPTH*2
        }
    }

    context("fold(), foldLeft()") {
        expect("FLNil") {
            FLNil<Int>().foldLeft(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe FLNil()
            FLNil<Int>().fold(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe FLNil()
        }
        expect("list of 1") {
            listOf1.foldLeft(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe listOf1s
            listOf1.fold(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe listOf1s
        }
        expect("list of 2") {
            listOf12.foldLeft(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe listOf21s
            listOf12.fold(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe listOf21s
        }
        expect("list of 3") {
            listOf123.foldLeft(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe listOf321s
            listOf123.fold(FLNil<String>() as FList<String>) { l, s -> FLCons(s.toString(), l) } shouldBe listOf321s
        }
        expect("arithmetic sequence sum") {
            seq1to100.foldLeft(0, sum) shouldBe 5050
            seq1to100.fold(0, sum) shouldBe 5050
        }
        expect("non-commutative op") {
            seq1to100.foldLeft(0, diff) shouldBe 50
            seq1to100.fold(0, diff) shouldBe 50
        }
        expect("list huge") {
            listUnsafe.foldLeft(0, sum) shouldBe XLARGE_DEPTH * (XLARGE_DEPTH-1) / 2
            listUnsafe.fold(0, sum) shouldBe XLARGE_DEPTH * (XLARGE_DEPTH-1) / 2
        }
    }

    context("foldRight()") {
        expect("FLNil") {
            FLNil<Int>().foldRight(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe FLNil()
        }
        expect("list of 1") {
            listOf1.foldRight(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe listOf1s
        }
        expect("list of 2") {
            listOf12.foldRight(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe listOf12s
        }
        expect("list of 3") {
            listOf123.foldRight(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe listOf123s
        }
        expect("arithmetic sequence sum") {
            seq1to100.foldRight(0, sum) shouldBe 5050
        }
        expect("non-commutative op") {
            seq1to100.foldRight(0, diff) shouldBe -5050
        }
        expect("list huge") {
            shouldThrow<StackOverflowError> {
                listUnsafe.foldRight(0, sum)
            }
        }
    }

    context("foldRightSafe()") {
        expect("FLNil") {
            FLNil<Int>().foldRightSafe(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe FLNil()
        }
        expect("list of 1") {
            listOf1.foldRightSafe(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe listOf1s
        }
        expect("list of 2") {
            listOf12.foldRightSafe(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe listOf12s
        }
        expect("list of 3") {
            listOf123.foldRightSafe(FLNil<String>() as FList<String>) { s, l -> FLCons(s.toString(), l) } shouldBe listOf123s
        }
        expect("arithmetic sequence sum") {
            seq1to100.foldRightSafe(0, sum) shouldBe 5050
        }
        expect("non-commutative op") {
            seq1to100.foldRightSafe(0, diff) shouldBe -5050
        }
        expect("list huge") {
            listUnsafe.foldRightSafe(0, sum) shouldBe XLARGE_DEPTH * (XLARGE_DEPTH-1) / 2
        }
    }

    context("pick()") {
        expect("FLNil") {
            FLNil<Int>().pick() shouldBe null
        }
        expect("list of 1, of 2, of 3") {
            listOf1.pick() shouldBe 1
            listOf12.pick() shouldBe 1
            listOf23.pick() shouldBe 2
            listOf123.pick() shouldBe 1
            listOf312.pick() shouldBe 3
        }
        expect("list of 1000") {
            listThousand.pick() shouldBe 0
        }
        expect("list huge") {
            listUnsafe.pick() shouldBe 0
        }
    }

    context("prepend()") {
        expect("FLNil") {
            FLNil<Int>().prepend(1) shouldBe FLCons(1, FLNil<Int>())
        }
        expect("list of 1, of 2, of 3") {
            val app2 = listOf1.prepend(2)
            app2 shouldBe listOf21
            app2.toString() shouldBe "FLCons(head=2, tail=FLCons(head=1, tail=FLNil()))"
            val app3 = app2.prepend(3)
            app3 shouldBe listOf321
            app3.toString() shouldBe "FLCons(head=3, tail=FLCons(head=2, tail=FLCons(head=1, tail=FLNil())))"
        }
        expect("list of 1000") {
            val aut = listThousand.prepend(2000)
            aut.head() shouldBe 2000
            aut.size() shouldBe 1001
            aut.tail() shouldBe listThousand
            aut.last() shouldBe 999
        }
        expect("list huge") {
            val aut = listUnsafe.prepend(XLARGE_DEPTH*2)
            aut.head() shouldBe XLARGE_DEPTH*2
            aut.size() shouldBe XLARGE_DEPTH+1
            aut.tail().head() shouldBe 0
            aut.last() shouldBe XLARGE_DEPTH-1
        }
    }

    context("reverse()") {
        expect("FLNil") {
            FLNil<Int>().reverse() shouldBe FLNil<Int>()
        }
        expect("list of 1, of 2, of 3") {
            listOf1.reverse() shouldBe listOf1
            listOf12.reverse() shouldBe listOf21
            listOf123.reverse() shouldBe listOf321
        }
        expect("list of 1000") {
            listThousand.reverse() shouldBe listThousandR
        }
        expect("list huge") {
            val aut = listUnsafe.reverse()
            aut.head() shouldBe XLARGE_DEPTH-1
            aut.size() shouldBe XLARGE_DEPTH
            aut.last() shouldBe 0
        }
    }

    context("count()") {
        expect("FLNil") {
            FLNil<Int>().count{ true } shouldBe 0
        }
        expect("list of 1") {
            listOf1.count{ true } shouldBe 1
            listOf1.count(isOdd) shouldBe 1
            listOf1.count(isEven) shouldBe 0
        }
        expect("list of 2") {
            listOf12.count{ true } shouldBe 2
            listOf12.count(isOdd) shouldBe 1
            listOf12.count(isEven) shouldBe 1
        }
        expect("list of 3") {
            listOf123.count{ true } shouldBe 3
            listOf123.count(isOdd) shouldBe 2
            listOf123.count(isEven) shouldBe 1
        }
        expect("list of 1000") {
            listThousand.count { true } shouldBe 1000
            listThousand.count(isOdd) shouldBe 500
            listThousand.count(isEven) shouldBe 500
        }
        expect("list huge") {
            isEven(XLARGE_DEPTH) shouldBe true
            listUnsafe.count { true } shouldBe XLARGE_DEPTH
            listUnsafe.count(isOdd) shouldBe XLARGE_DEPTH/2
            listUnsafe.count(isEven) shouldBe XLARGE_DEPTH/2
        }
    }
})
