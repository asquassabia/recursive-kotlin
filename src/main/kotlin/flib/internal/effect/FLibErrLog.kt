package org.xrpn.flib.internal.effect

import java.io.OutputStream
import java.io.PrintStream

/**
 * [FLibErrLog] is useful for emitting error messages to [System.err] with
 * contextual information from [FLibLogCtx]
 */
internal interface FLibErrLog {

    /** Write a log entry to [dest] on behalf of [emitter]?:this */
    fun errLog(msg: String, emitter: Any? = null, newLine: Boolean = true, dest: OutputStream = System.err) = object :
        FLibLogCtx {
        override val logStream = PrintStream(dest,true)
        override val emitterClass = emitter?.let { "${it::class}" } ?: "${this::class.supertypes}"
        override val emitterString: String = "${emitter ?: ""}"
        fun emitMsg(): FLibLogCtx = synchronized(logStream) {
            this.logStream.print("$ERR_TAG $msg $ERR_BY_TAG ")
            if (newLine) this.logStream.println()
            this
        }
    }.emitMsg().emitterId(indent = newLine, newLine = newLine)

    /**
     * Report [ex] writing a log entry to [dest] about [item]'s failure message
     * on behalf of [emitter]?:this
     */
    fun reportException(ex: Exception, circumstances: String, emitter: Any? = null, newLine: Boolean = true, dest: OutputStream = System.err) =
        errLog(circumstances, emitter, newLine, dest).stackTrace(ex)
}
