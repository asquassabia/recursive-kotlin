package flib.effect

import flib.testtool.CaptureSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.internal.effect.FLibErrLog

internal class FLibErrLogTest : CaptureSpec({ makeBuffers ->

    context("errLog") {
        expect("to stdErr") {
            val aut = object : FLibErrLog {}
            val cap = makeBuffers().shareBuffers()
            cap.use {
                it.redirect()
                it.isCaptured() shouldBe true
                aut.errLog(TestMe.errorMessage, TestMe.emitter, true, System.err)
                val (outBuf,errBuf) = it.examine()
                outBuf shouldBe ""
                errBuf shouldBe TestMe.errLogOracle
            }
            cap.isCaptured() shouldBe false
        }
        expect("null emitter") {
            val aut = object : FLibErrLog {}
            val cap = makeBuffers().shareBuffers()
            cap.use {
                it.redirect()
                it.isCaptured() shouldBe true
                aut.errLog(TestMe.errorMessage,null)
                val (outBuf,errBuf) = it.examine()
                errBuf shouldBe TestMe.errLogNullOracle
                outBuf shouldBe ""
            }
            cap.isCaptured() shouldBe false
        }
        expect("no new line") {
            val aut = object : FLibErrLog {}
            val cap = makeBuffers().shareBuffers()
            cap.use {
                it.redirect()
                it.isCaptured() shouldBe true
                aut.errLog(TestMe.errorMessage, TestMe.emitter, newLine = false)
                val (outBuf,errBuf) = it.examine()
                errBuf shouldBe TestMe.errLogNNLOracle
                outBuf shouldBe ""
            }
            cap.isCaptured() shouldBe false
        }
        expect("reportException") {
            val aut = object : FLibErrLog {}
            val cap = makeBuffers().shareBuffers()
            cap.use {
                it.redirect()
                it.isCaptured() shouldBe true
                aut.reportException(TestMe.t,TestMe.circumstances,TestMe.emitter)
                val (outBuf,errBuf) = it.examine()
                errBuf.take(TestMe.exceptionReport.length) shouldBe TestMe.exceptionReport
                outBuf shouldBe ""
            }
            cap.isCaptured() shouldBe false
        }
    }
})