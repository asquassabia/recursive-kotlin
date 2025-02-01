package flib.testtool

import io.kotest.core.spec.style.ExpectSpec
import org.xrpn.flib.internal.tool.capture.StdBuffers

internal typealias BufferFactory = () -> StdBuffers

internal abstract class CaptureSpec(expose: CaptureSpec.(BufferFactory) -> Unit = {}) : ExpectSpec({
    expose(this as CaptureSpec, makeBuffers)
}) {
    companion object {
        val makeBuffers: () -> StdBuffers = { StdBuffers.build() }
    }
}