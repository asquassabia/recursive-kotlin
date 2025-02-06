package flib.effect

import flib.testtool.CaptureSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.internal.effect.FLibLogCtx
import org.xrpn.flib.internal.tool.Capture
import org.xrpn.flib.internal.tool.ErrStr
import org.xrpn.flib.internal.tool.OutStr
import java.io.PrintStream

internal class FLibLogCtxTest : CaptureSpec({ makeCapture: () -> Capture ->
    assertSoftly = true
    threads = 1
    context("emitterId") {
        expect("to stdout") {
            val aut = object : FLibLogCtx {
                override val logStream: PrintStream
                    get() = System.out
                override val emitterClass: String
                    get() = TestMe.emitter::class.toString()
                override val emitterString: String
                    get() = TestMe.emitter.toString()
            }
            val cap = makeCapture()
            cap.use {
                it.redirect() shouldBe true
                it.isCaptured() shouldBe true
                aut.emitterId(indent = false, newLine = true)
                val (outBuf: OutStr,errBuf: ErrStr) = it.examine()!!
                errBuf shouldBe ""
                outBuf shouldBe TestMe.msgOracle
                it.revert() shouldBe true
            }
            cap.isCaptured() shouldBe false
        }
        expect("to stderr, no newLine") {
            val aut = object : FLibLogCtx {
                override val logStream: PrintStream
                    get() = System.err
                override val emitterClass: String
                    get() = TestMe.emitter::class.toString()
                override val emitterString: String
                    get() = TestMe.emitter.toString()
            }
            val cap = makeCapture()
            cap.use {
                it.redirect() shouldBe true
                it.isCaptured() shouldBe true
                aut.emitterId(indent = false, newLine = false)
                val (outBuf,errBuf) = it.examine()!!
                errBuf shouldBe TestMe.msgOracle.take(30)
                outBuf shouldBe ""
                it.revert() shouldBe true
            }
            cap.isCaptured() shouldBe false
        }
        expect("to stdout, null msg") {
            val aut = object : FLibLogCtx {
                override val logStream: PrintStream
                    get() = System.out
                override val emitterClass: String
                    get() = TestMe.emitter::class.toString()
                override val emitterString: String? = null
            }
            val cap = makeCapture()
            cap.use {
                it.redirect() shouldBe true
                it.isCaptured() shouldBe true
                aut.emitterId(indent = false, newLine = true)
                val (outBuf,errBuf) = it.examine()!!
                errBuf shouldBe ""
                outBuf shouldBe TestMe.msgOracle.take(24)+"\n"
                it.revert() shouldBe true
            }
            cap.isCaptured() shouldBe false
        }
        expect("to stdout, null emitter") {
            val aut = object : FLibLogCtx {
                override val logStream: PrintStream
                    get() = System.out
                override val emitterClass: String
                    get() = TestMe.emitter::class.toString()
                override val emitterString: String? = null
            }
            val cap = makeCapture()
            cap.use {
                it.redirect() shouldBe true
                it.isCaptured() shouldBe true
                aut.emitterId(indent = false, newLine = true)
                val (outBuf,errBuf) = it.examine()!!
                errBuf shouldBe ""
                outBuf shouldBe TestMe.msgOracle.take(24)+"\n"
                it.revert() shouldBe true
            }
            cap.isCaptured() shouldBe false
        }
    }
    context("stackTrace") {
        expect("to stdout") {
            val aut = object : FLibLogCtx {
                override val logStream: PrintStream
                    get() = System.out
                override val emitterClass: String
                    get() = TestMe.emitter::class.toString()
                override val emitterString: String
                    get() = TestMe.emitter.toString()
            }
            val cap = makeCapture()
            cap.use {
                it.redirect()
                it.isCaptured() shouldBe true
                aut.stackTrace(TestMe.t)
                val (outBuf,errBuf) = it.examine()!!
                errBuf shouldBe ""
                outBuf.take(TestMe.tmsgStartOracle.length) shouldBe TestMe.tmsgStartOracle
                it.revert() shouldBe true
            }
            cap.isCaptured() shouldBe false
        }
        expect("to stderr") {
            val aut = object : FLibLogCtx {
                override val logStream: PrintStream
                    get() = System.err
                override val emitterClass: String
                    get() = TestMe.emitter::class.toString()
                override val emitterString: String
                    get() = TestMe.emitter.toString()
            }
            val cap = makeCapture()
            cap.use {
                it.redirect() shouldBe true
                it.isCaptured() shouldBe true
                aut.stackTrace(TestMe.t)
                val (outBuf,errBuf) = it.examine()!!
                errBuf.take(TestMe.tmsgStartOracle.length) shouldBe TestMe.tmsgStartOracle
                outBuf shouldBe ""
                it.revert() shouldBe true
            }
            cap.isCaptured() shouldBe false
        }
    }
})