package flib.testtool

import io.kotest.core.spec.style.ExpectSpec
import org.xrpn.flib.internal.tool.capture.Capture.Companion.StdBuffers

internal typealias BufferFactory = () -> StdBuffers

internal abstract class CaptureSpec(expose: CaptureSpec.(BufferFactory) -> Unit = {}) : ExpectSpec({
    expose(this as CaptureSpec, makeCapture)
}) {
    companion object {
        val makeCapture: () -> StdBuffers = { StdBuffers.build() }
    }
}