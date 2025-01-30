package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FPruning<F, A> {

    /** Remove at most one structurally identical (==) item from [fa] */
    fun fdropItem(fa: Kind<F, A>, item: A): Kind<F, A>

    /** Remove, if found, this instance (===) of item from [fa] */
    fun fdropItemStrictly(fa: Kind<F, A>, item: A): Kind<F, A>

    /** Remove one element from [fa] (the first, if [fa] is a [Sequence]) */
    fun fpop(fa: Kind<F, A>): A?

    /**
     * Remove one element from [fa] (the first, if [fa] is a [Sequence]).
     * Return this element and the remainder of [fa] with this element removed.
     */
    fun fpopAndRemainder(fa: Kind<F, A>): Pair<A?, Kind<F, A>>
}