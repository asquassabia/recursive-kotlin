package org.xrpn.flib.internal.effect

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
    fun emitterId(indent: Boolean = false, newLine: Boolean = true): FLibLogCtx = synchronized(logStream) {
        if (indent) logStream.print("\t")
        logStream.print("$emitterClass${emitterString?.let { "($it)" } ?: ("")}")
        if (newLine) logStream.println()
        logStream.flush()
        this
    }

    /** emit to destination the stack trace of a [Throwable] [t] */
    fun stackTrace(t: Throwable): FLibLogCtx = synchronized(logStream) {
        t.printStackTrace(logStream)
        logStream.flush()
        this
    }
}