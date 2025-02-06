package org.xrpn.flib.internal.tool.capture

import arrow.atomic.AtomicBoolean
import org.xrpn.flib.FIX_TODO
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
    private val outBuffer: ByteArrayOutputStream, // val, but is mutable and needs locks
    private val errBuffer: ByteArrayOutputStream, // val, but is mutable and needs locks
    private val contentMutex: Object,
    private val wrapper: BufWrapper
): Closeable {
    private val isRedirected = AtomicBoolean(false)
    private lateinit var originalOut: PrintStream
    private lateinit var originalErr: PrintStream
    private val capPair: Pair<PrintStream, PrintStream> by lazy { synchronized(contentMutex) {
        Pair(PrintStream(outBuffer, true), PrintStream(errBuffer, true))
    }}
    fun isCaptured(): Boolean = isRedirected.get()
    fun redirect(): Boolean = synchronized(this) {
        synchronized(contentMutex) {
            isRedirected.set(true) // assign before action it protects
            /* I have no means to establish if System.out or System.err had already
             * been redirected. These are whatever they are at this time
             */
            originalOut = System.out
            originalErr = System.err
            System.setOut(capPair.first)
            System.setErr(capPair.second)
            (System.out != originalOut && System.err != originalErr)
        }
    }
    fun revert(): Boolean = isRedirected.get() && synchronized(this) {
        synchronized(contentMutex) { wrapper.use {
            val owned = (System.out === capPair.first) && (System.err === capPair.second)
            if (!owned) TODO("$FIX_TODO HANDLING attempt to revert on capture by others")
            run({
                System.setOut(originalOut)
                System.setErr(originalErr)
                isRedirected.set(false) // assign after action it protects
                (System.out === originalOut && System.err === originalErr)
            })
        }}
    }

    fun examine(): Pair<OutStr, ErrStr> = synchronized(this) { synchronized(contentMutex) { wrapper.use {
        it.flush()
    }}}

    override fun close() { revert() }

    companion object {

        // private val globalMutex = Object()

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
            val alreadyCaptured = AtomicBoolean(false)
            internal val contentMutex = Object() // protects all private var
            private var flushed: Boolean = false
            private lateinit var out: String
            private lateinit var err: String

            /**
             * [capture] must be called at most once when constructing [Capture]
             */
            fun capture(): Capture? =
//                synchronized(globalMutex) {
                    synchronized (contentMutex) { when {
                alreadyCaptured.get() -> TODO("$FIX_TODO cannot capture more than once")
                flushed -> TODO("$FIX_TODO it should be impossible to get here")
                else -> {
                    alreadyCaptured.set(true)
                    of(outBuffer,errBuffer,contentMutex,this)
                }
            }}
        //}
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