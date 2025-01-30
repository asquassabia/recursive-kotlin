package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FComparing<F, A> {

    /**
     * True if lhs and rhs are equal. If [lhs] is a [Sequence] and [lhs] is a
     * [Sequence], all matching elements must appear in the same sequence.
     */
    fun fequal(lhs: Kind<F, A>, rhs: Kind<F, A>): Boolean
}