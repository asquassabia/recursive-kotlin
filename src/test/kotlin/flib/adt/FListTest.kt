package flib.adt

import flib.LARGE_DEPTH
import flib.XLARGE_DEPTH
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList

class FListTest : ExpectSpec({

    tailrec fun listBuilder(l: FList<Int>, c: Int, s: Int = LARGE_DEPTH): FList<Int> =
        if (c == LARGE_DEPTH) l else listBuilder(FLCons(c, l), c + 1,s)

    xcontext("FLNil()") {
//        expect("toString()") {
//            FLNil().toString() shouldBe "FLNil()"
//        }
//        expect("hashCode()") {
//            FLNil().hashCode() shouldBe -941482677
//        }
//        expect("equals()") {
//            FLNil().equals(FLNil()) shouldBe true
//        }
    }
    context("FLCons") {
        context("toString()") {
            expect("string") {
                FLCons(1, FLNil()).toString() shouldBe "FLCons(head=1, tail=FLNil())"
                FLCons(1, FLCons(2, FLNil())).toString() shouldBe "FLCons(head=1, tail=FLCons(head=2, tail=FLNil()))"
            }
            expect ("stack blowup") {
                shouldThrow<StackOverflowError> {
                    listBuilder(FLNil(), 0, LARGE_DEPTH).toString()
                }
            }
        }
        context("hashcode") {
            expect("hash of small") {
                FLCons(1, FLNil()).hashCode() shouldBe -941482646
            }
            expect ("hash of large (trouble)") {
                listBuilder(FLNil(), 0, LARGE_DEPTH).hashCode() shouldBe -554060177
            }
            expect ("hash of xlarge (trouble)") {
                listBuilder(FLNil(), 0, XLARGE_DEPTH).hashCode() shouldBe -554060177
            }
            expect ("trouble") {
                listBuilder(FLNil(), 0, LARGE_DEPTH).hashCode() shouldBe listBuilder(FLNil(), 0, XLARGE_DEPTH).hashCode()
            }
        }
        expect("equals()") {
            FLCons(1, FLNil()).equals(FLCons(1, FLNil())) shouldBe true
            FLCons(1, FLCons(2, FLNil())).equals(FLCons(1, FLCons(2, FLNil()))) shouldBe true
        }
    }
})
