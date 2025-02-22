package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FFoldingSafe<F, A> {

    /**
     * Fold element(s) using the binary operator f and the initial seed z.
     * Traversal is from last to first iff [fa] is a [Sequence], else no
     * guarantee. Stack safe and potentially slower than [ffoldRight]
     */
    fun <B> ffoldRightSafe(fa: Kind<F, A>, z: B, f: (A, B) -> B): B
}
