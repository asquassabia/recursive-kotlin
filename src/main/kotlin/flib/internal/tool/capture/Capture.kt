package org.xrpn.flib.internal.tool.capture

import java.io.Closeable
import java.io.PrintStream

typealias OutStr = String
typealias ErrStr = String

/**
 * Control center for capturing stdout and stderr during a live run. This
 * is useful to prevent a wannabe daemon from writing to the terminal, or
 * to examine programmatically what is written to stdout and stderr.
 */
@ConsistentCopyVisibility
internal data class Capture private constructor (
    private val buffers: StdBuffers
): Closeable {

    private var isRedirected = false
    fun isCaptured(): Boolean = synchronized(this) { isRedirected }
    fun redirect() = synchronized(this) { synchronized(buffers.contentMutex) {
        System.setOut(PrintStream(buffers.outBuffer,true))
        System.setErr(PrintStream(buffers.errBuffer,true))
        isRedirected = true
    }}
    fun revert() = synchronized(this) { synchronized(buffers.contentMutex) {
        System.setOut(System.err)
        System.setErr(System.out)
        isRedirected = false
    }}
    override fun close() { synchronized(this) { synchronized(buffers.contentMutex) { buffers.use {
        revert()
    }}}}
    fun examine(): Pair<OutStr, ErrStr> = synchronized(this) { synchronized(buffers.contentMutex) {
        buffers.flush()
        Pair(buffers.out, buffers.err)
    }}
    companion object {
        fun of(b: StdBuffers) = Capture(b)
    }
}