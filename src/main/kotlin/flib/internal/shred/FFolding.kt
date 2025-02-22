package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

/**
 * The order of traversal is deterministic only if ordering is an intrinsic
 * property. Else, the order of traversal is undetermined. In the latter
 * case, f MUST be commutative or the outcome will also be undetermined and
 * not necessarily repeatable.
 */
internal interface FFolding<F, A> {

    /**
     * Fold element(s) using the binary operator f and the initial seed z.
     * Traversal is from first to last iff [fa] is a [Sequence], else no
     * guarantee.
     */
    fun <B> ffoldLeft(fa: Kind<F, A>, z: B, f: (B, A) -> B): B

    /**
     * Fold element(s) using the binary operator f and the initial seed z.
     * Traversal is from last to first iff [fa] is a [Sequence], else no
     * guarantee. Not stack safe
     */
    fun <B> ffoldRight(fa: Kind<F, A>, z: B, f: (A, B) -> B): B
}