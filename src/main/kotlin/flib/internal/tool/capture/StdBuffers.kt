package org.xrpn.flib.internal.tool.capture

import org.xrpn.flib.internal.tool.CAPTURE_SIZE
import org.xrpn.flib.internal.tool.capture.Capture
import java.io.ByteArrayOutputStream
import java.io.Closeable

/**
 * Holds and manages the mutable buffers to which stdout and
 * stderr are redirected during capture for later verification.
 */

@ConsistentCopyVisibility
data class StdBuffers private constructor(
    internal val outBuffer: ByteArrayOutputStream,
    internal val errBuffer: ByteArrayOutputStream
): Closeable {
    internal val contentMutex = Object()
    internal lateinit var out: String
    internal lateinit var err: String

    /**
     * [shareBuffers] must be called at most once when constructing [Capture]
     */
    internal fun shareBuffers() = Capture.of(this)
    internal fun flush() = synchronized(contentMutex) {
        outBuffer.flush(); out = outBuffer.toString(); outBuffer.reset()
        errBuffer.flush(); err = errBuffer.toString(); errBuffer.reset()
    }
    override fun close() = synchronized(contentMutex) {
        flush()
        outBuffer.close()
        errBuffer.close()
    }
    companion object {
        fun build(initSize: Int = CAPTURE_SIZE): StdBuffers = StdBuffers(ByteArrayOutputStream(initSize), ByteArrayOutputStream(initSize))
    }
}
