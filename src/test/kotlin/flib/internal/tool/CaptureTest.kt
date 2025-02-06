package flib.internal.tool

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.xrpn.flib.internal.tool.Capture
import org.xrpn.flib.internal.tool.ErrStr
import org.xrpn.flib.internal.tool.OutStr
import java.time.Duration
import java.time.temporal.ChronoUnit.NANOS

class CaptureTest : ExpectSpec({

    context("capture ops") {
        expect("revert before redirect") {
            val cap = Capture.build()
            cap.use {
                it.isCaptured() shouldBe false
                it.isSpent() shouldBe false
                it.active() shouldBe null
                it.revert() shouldBe false
                it.isCaptured() shouldBe false
                it.isSpent() shouldBe false
                it.active() shouldBe null
            }
        }
        expect("redirect and close without revert") {
            val cap = Capture.build()
            cap.use {
                it.isCaptured() shouldBe false
                it.isSpent() shouldBe false
                it.redirect() shouldBe true
                it.active() shouldNotBe null
                it.isCaptured() shouldBe true
                it.isSpent() shouldBe false
            }
            cap.isCaptured() shouldBe false
            cap.isSpent() shouldBe true
        }
        expect("redirect twice") {
            val cap = Capture.build()
            cap.use {
                it.redirect() shouldBe true
                it.redirect() shouldBe false
                it.isCaptured() shouldBe true
                it.isSpent() shouldBe false
            }
        }
        expect("revert twice") {
            val cap = Capture.build()
            cap.use {
                it.redirect() shouldBe true
                it.revert() shouldBe true
                it.revert() shouldBe false
            }
        }
        expect("examine before redirect") {
            val cap = Capture.build()
            cap.use {
                it.isCaptured() shouldBe false
                it.isSpent() shouldBe false
                cap.examine() shouldBe null
                it.isCaptured() shouldBe false
                it.isSpent() shouldBe false
            }
        }
        expect("examine stdout") {
            val cap = Capture.build()
            cap.use {
                it.redirect()
                System.out.print("A")
                System.out.println()
                System.out.flush()
                val bufs: Pair<OutStr, ErrStr> = cap.examine()!!
                bufs.first shouldBe "A\n"
                bufs.second shouldBe ""
            }
        }
        expect("examine stderr") {
            val cap = Capture.build()
            cap.use {
                it.redirect()
                System.err.print("A")
                System.err.println()
                System.err.flush() // should not be necessary
                val bufs: Pair<OutStr, ErrStr> = cap.examine()!!
                bufs.first shouldBe ""
                bufs.second shouldBe "A\n"
            }
        }
        expect("examine stderr and stdout") {
            val cap = Capture.build()
            cap.use {
                it.active() shouldBe null
                it.redirect()
                System.out.print("B")
                System.out.println()
                System.out.flush()
                System.err.print("A")
                System.err.println()
                System.err.flush() // should not be necessary
                val bufs: Pair<OutStr, ErrStr> = cap.examine()!!
                bufs.first shouldBe "B\n"
                bufs.second shouldBe "A\n"
            }
            val a: Duration? = cap.active()
            a shouldNotBe null
            a!!.get(NANOS) shouldBeGreaterThan 0
        }
        expect("examine after close") {
            val cap = Capture.build()
            cap.use {
                it.redirect()
                System.err.println("A")
                System.err.println()
                System.err.flush() // should not be necessary
            }
            cap.examine() shouldBe null
        }

    }
})