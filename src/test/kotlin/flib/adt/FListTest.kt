package flib.adt

import flib.LARGE_DEPTH
import flib.XLARGE_DEPTH
import flib.flistBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList

class FListTest : ExpectSpec({

    context("FLNil") {
        expect("toString()") {
            FLNil<Int>().toString() shouldBe "FLNil()"
        }
        expect("hashCode()") {
            FLNil<Int>().hashCode() shouldBe /* unfortunately, type erasure */ FLNil<Char>().hashCode()
        }
        expect("equals()") {
            FLNil<Int>().equals(FLNil<Char>()) shouldBe /* unfortunately, type erasure */ true
        }
    }
    context("FLCons") {
        context("toString()") {
            expect("string") {
                FLCons(1, FLNil()).toString() shouldBe "FLCons(head=1, tail=FLNil())"
                FLCons(1, FLCons(2, FLNil())).toString() shouldBe "FLCons(head=1, tail=FLCons(head=2, tail=FLNil()))"
            }
            expect ("stack blowup") {
                shouldThrow<StackOverflowError> {
                    flistBuilder(0, LARGE_DEPTH).toString()
                }
            }
        }
        context("hashcode") {
            expect("hash of small") {
                FLCons(1, FLNil()).hashCode() shouldBe FLCons(1, FLNil()).hashCode()
            }
            expect ("hash of large (hashCode() overflow)") {
                flistBuilder(0, LARGE_DEPTH).hashCode() shouldBe flistBuilder(0, LARGE_DEPTH).hashCode()
            }
            expect ("hash of xlarge (hashCode() overflow)") {
                flistBuilder(0, XLARGE_DEPTH).hashCode() shouldBe flistBuilder(0, XLARGE_DEPTH).hashCode()
            }
            expect ("hashCode() overflow") {
                flistBuilder(0, LARGE_DEPTH).hashCode() shouldNotBe flistBuilder(0, XLARGE_DEPTH).hashCode()
            }
        }
        expect("equals()") {
            FLCons(1, FLNil()).equals(FLCons(1, FLNil())) shouldBe true
            FLCons(1, FLCons(2, FLNil())).equals(FLCons(1, FLCons(2, FLNil()))) shouldBe true
        }
    }
})
