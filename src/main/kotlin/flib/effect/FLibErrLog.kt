package org.xrpn.flib.effects

import java.io.OutputStream
import java.io.PrintStream

/**
 * [FLibErrLog] is useful for emitting error messages to [System.err] with
 * contextual information from [FLibLogCtx]
 */
internal interface FLibErrLog {
    /** Write a log entry to [dest] on behalf of [emitter]?:this */
    fun errLog(msg: String, emitter: Any? = null, dest: OutputStream = System.err) = object : FLibLogCtx {
        override val logStream: PrintStream by lazy { PrintStream(dest) }
        override val emitterClass: String by lazy { emitter?.let { "${it::class}" } ?: "${this::class}" }
        override val emitterString: String? by lazy { emitter?.let { "$msg\n$it" } ?: msg }
    }.emitUnconditionally()

    /**
     * Report [ex] writing a log entry to [dest] about [item]'s failure message
     * on behalf of [emitter]?:this
     */
    fun reportException(ex: Exception, circumstances: String, emitter: Any? = null, dest: OutputStream = System.err) {
        val aux: FLibLogCtx = errLog(circumstances, emitter, dest)
        aux.emitUnconditionally().emitUnconditionally(ex)
    }
}
