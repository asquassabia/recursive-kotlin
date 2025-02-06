package flib.effect

import flib.testtool.CaptureSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.tool.CAPTURE_SIZE
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class FLibErrLogTest : ExpectSpec({
    assertSoftly = true
    threads = 1
    context("errLog") {
        expect("to stdErr") {
            val baos = ByteArrayOutputStream(CAPTURE_SIZE)
            val capture = PrintStream(baos,true)
            capture.use { cap -> baos.use { buf ->
            val aut = object : FLibLog {}
                aut.log(TestMe.errorMessage, TestMe.emitter, true, cap)
                buf.flush()
                val log = buf.toString()
                log shouldBe TestMe.errLogOracle
            }}
        }
        expect("null emitter") {
            val baos = ByteArrayOutputStream(CAPTURE_SIZE)
            val capture = PrintStream(baos,true)
            capture.use { cap -> baos.use { buf ->
            val aut = object : FLibLog {}
                aut.log(TestMe.errorMessage, dest = cap)
                buf.flush()
                val log = buf.toString()
                log shouldBe TestMe.errLogNullOracle
            }}
        }
        expect("no new line") {
            val baos = ByteArrayOutputStream(CAPTURE_SIZE)
            val capture = PrintStream(baos,true)
            capture.use { cap -> baos.use { buf ->
            val aut = object : FLibLog {}
                aut.log(TestMe.errorMessage, TestMe.emitter, newLine = false, cap)
                buf.flush()
                val log = buf.toString()
                log shouldBe TestMe.errLogNNLOracle
            }}
        }
        expect("reportException") {
            val baos = ByteArrayOutputStream(CAPTURE_SIZE)
            val capture = PrintStream(baos,true)
            capture.use { cap -> baos.use { buf ->
                val aut = object : FLibLog {}
                aut.reportException(TestMe.t,TestMe.circumstances,TestMe.emitter, dest = cap)
                buf.flush()
                val log = buf.toString()
                log.take(TestMe.exceptionReport.length) shouldBe TestMe.exceptionReport
           }}
        }
    }
})