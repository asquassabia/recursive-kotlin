package org.xrpn.flib.internal.tool

import arrow.atomic.AtomicBoolean
import org.xrpn.flib.CAPTURE_SIZE
import org.xrpn.flib.FIX_TODO
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.PrintStream
import java.time.Duration
import java.time.Instant

typealias OutStr = String
typealias ErrStr = String

/**
 * Control center for capturing stdout and stderr during a live run. This
 * is useful to prevent a wannabe daemon from writing to the terminal, or
 * to examine programmatically what is written to stdout and stderr.
 * Create with [Capture.build]
 * '''
 * val cap = [Capture.build]()
 * '''
 * Always wrap with [use]. In order, this is a typical session:
 * '''
 * typealias OutStr = String
 * typealias ErrStr = String
 * cap.use {
 *     assert(!it.isCaptured())
 *     assert(!it.isSpent())
 *     assert(it.redirect())
 *     assert(it.isCaptured())
 *     // ... do something that puts info to stdout or stderr
 *     val bufs: Pair<OutStr, ErrStr> = cap.examine()
 *     assert (bufs.first == "stdout output")
 *     assert (bufs.second == "stderr output")
 *     assert(it.revert())
 *     assert(!it.isCaptured())
 *     assert(it.isSpent())
 * }
 * '''
 */
@ConsistentCopyVisibility
data class Capture private constructor (
    private val outBuffer: ByteArrayOutputStream, // val, but is mutable and needs locks
    private val errBuffer: ByteArrayOutputStream, // val, but is mutable and needs locks
    private val contentMutex: Object,
    private val wrapper: BufWrapper
): Closeable {
    private val isRedirected = AtomicBoolean(false)
    private val isSpent = AtomicBoolean(false)
    private var captureStart: Instant? = null
    private var captureStop: Instant? = null
    private var captureLapse: Duration? = null
    private lateinit var originalOut: PrintStream
    private lateinit var originalErr: PrintStream
    private val capPair: Pair<PrintStream, PrintStream> by lazy { synchronized(contentMutex) {
        Pair(PrintStream(outBuffer, true), PrintStream(errBuffer, true))
    }}
    fun isCaptured(): Boolean = isRedirected.get()
    fun isSpent(): Boolean = isSpent.get()
    fun active(): Duration? = synchronized(contentMutex) { captureStart?.let { start ->
        captureStop?.let { captureLapse } ?: Duration.between(Instant.now(), start)
    }}
    fun redirect(): Boolean = !isSpent.get() && synchronized(globalMutex) {
        synchronized(contentMutex) {
            isRedirected.set(true) // assign before action it protects
            /* I have no means to establish if System.out or System.err had already
             * been redirected. These are whatever they are at this time
             */
            originalOut = System.out
            originalErr = System.err
            System.setOut(capPair.first)
            System.setErr(capPair.second)
            captureStart = Instant.now()
            (System.out != originalOut && System.err != originalErr)
        }
    }
    fun revert(): Boolean = isRedirected.get() && synchronized(globalMutex) {
        synchronized(contentMutex) { wrapper.use {
            val owned = (System.out === capPair.first) && (System.err === capPair.second)
            if (!owned) TODO("$FIX_TODO HANDLING attempt to revert on capture by others")
            run({
                System.setOut(originalOut)
                System.setErr(originalErr)
                captureStop = Instant.now()
                captureLapse = Duration.between(captureStop!!, captureStart!!)
                isRedirected.set(false) // assign after action it protects
                isSpent.set(true)
                (System.out === originalOut && System.err === originalErr)
            })
        }}
    }

    fun examine(): Pair<OutStr, ErrStr>? = if (isRedirected.get() && !isSpent.get())
        synchronized(this) { synchronized(contentMutex) { wrapper.use {
        it.flush()
    }}} else null

    override fun close() { revert() }

    companion object {

        fun build(): Capture = StdBuffers.build().capture()!!

        /* Used in Capture during [redirect()] and [revert()] */
        private val globalMutex = Object()

        private interface BufWrapper: Closeable {
            fun flush(): Pair<OutStr, ErrStr>
        }

        private fun of(outBuffer: ByteArrayOutputStream,
                       errBuffer: ByteArrayOutputStream,
                       mx: Object,
                       c: BufWrapper
        ) = synchronized(mx) { Capture(outBuffer,errBuffer,mx,c) }

        /**
         * Holds and manages the mutable buffers to which stdout and
         * stderr are redirected during capture for later verification.
         */
        @ConsistentCopyVisibility
        private data class StdBuffers private constructor(
            private val outBuffer: ByteArrayOutputStream,
            private val errBuffer: ByteArrayOutputStream
        ): BufWrapper {
            private val alreadyCaptured = AtomicBoolean(false)
            private val contentMutex = Object() // protects all private var
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
                fun build(initSize: Int = CAPTURE_SIZE.get()): StdBuffers =
                    StdBuffers(ByteArrayOutputStream(initSize), ByteArrayOutputStream(initSize))
            }
        }

    }
}