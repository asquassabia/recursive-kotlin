package org.xrpn.flib.effects

import java.io.PrintStream

/**
 * [FLibLogCtx] is shorthand sugar, a construct of practical convenience
 * for writing to a log with contextual information
 */
interface FLibLogCtx {
    // destination where to emit log messages to
    val logStream: PrintStream

    // type identity of emitter
    val emitterClass: String

    // message signature of emitter, if any
    val emitterString: String?

    /** emit to destination what we know of the emitter identity */
    fun emitUnconditionally(): FLibLogCtx {
        logStream.println("$emitterClass${emitterString?.let { "'$it'" }}")
        return this
    }

    /** emit to destination the stack trace of a [Throwable] [t] */
    fun emitUnconditionally(t: Throwable): FLibLogCtx {
        t.printStackTrace(logStream)
        return this
    }
}