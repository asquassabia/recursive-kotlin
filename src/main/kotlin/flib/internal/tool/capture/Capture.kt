package org.xrpn.flib.internal.tool.capture

import org.xrpn.flib.internal.tool.CAPTURE_SIZE
import java.io.ByteArrayOutputStream
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
data class Capture private constructor (
    private val outBuffer: ByteArrayOutputStream,
    private val errBuffer: ByteArrayOutputStream,
    private val contentMutex: Object,
    private val wrapper: BufWrapper
): Closeable {
    private var isRedirected = false
    fun isCaptured(): Boolean = synchronized(this) { isRedirected }
    fun redirect(): Unit = synchronized(this) { synchronized(contentMutex) {
        System.setOut(PrintStream(outBuffer,true))
        System.setErr(PrintStream(errBuffer,true))
        isRedirected = true
    }}
    fun revert(): Unit = synchronized(this) { synchronized(contentMutex) {
        System.setOut(System.out)
        System.setErr(System.err)
        isRedirected = false
    }}
    override fun close(): Unit = synchronized(this) { synchronized(contentMutex) { wrapper.use {
        revert()
    }}}
    fun examine(): Pair<OutStr, ErrStr> = synchronized(this) { synchronized(contentMutex) { wrapper.use {
        revert()
        it.flush()
    }}}
    companion object {

        private interface BufWrapper: Closeable {
            fun flush(): Pair<OutStr, ErrStr>
        }

        /**
         * Holds and manages the mutable buffers to which stdout and
         * stderr are redirected during capture for later verification.
         */
        @ConsistentCopyVisibility
        data class StdBuffers private constructor(
            private val outBuffer: ByteArrayOutputStream,
            private val errBuffer: ByteArrayOutputStream
        ): BufWrapper {
            private var flushed: Boolean = false
            internal val contentMutex = Object()
            private lateinit var out: String
            private lateinit var err: String

            /**
             * [capture] must be called at most once when constructing [Capture]
             */
            fun capture(): Capture? = if(!flushed) of(outBuffer,errBuffer,contentMutex,this) else null
            override fun flush(): Pair<OutStr, ErrStr> = synchronized(contentMutex) { if(!flushed) {
                outBuffer.flush(); out = outBuffer.toString(); outBuffer.reset()
                errBuffer.flush(); err = errBuffer.toString(); errBuffer.reset()
            } else flushed = true
                Pair(out,err)
            }

            override fun close() = synchronized(contentMutex) {
                flush()
                outBuffer.close()
                errBuffer.close()
            }
            companion object {
                fun build(initSize: Int = CAPTURE_SIZE): StdBuffers =
                    StdBuffers(ByteArrayOutputStream(initSize), ByteArrayOutputStream(initSize))
            }
        }

        private fun of(outBuffer: ByteArrayOutputStream,
               errBuffer: ByteArrayOutputStream,
               mx: Object,
               c: BufWrapper
        ) = synchronized(mx) { Capture(outBuffer,errBuffer,mx,c) }
    }
}