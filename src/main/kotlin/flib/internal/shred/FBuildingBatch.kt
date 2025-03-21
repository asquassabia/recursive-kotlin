package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FBuildingBatch<F, A> {

    /**
     * Add all element in [faNew] to [fa], maintaining their sequential order if
     * [faNew] is a [Sequence], before the head of [fa] iff [fa] is a [Sequence].
     * If [fa] is not a sequence, add to it all element in [faNew] in whatever
     * order may suit.
     */
    fun fprepend(faNew: Kind<F, A>, fa: Kind<F, A>): Kind<F, A>

    /**
     * Add all element in [faNew] to [fa], maintaining their sequential order if
     * [faNew] is a [Sequence], after the end of [fa] iff [fa] is a [Sequence].
     * If [fa] is not a sequence, add to it all element in [faNew]
     */
    fun fappend(fa: Kind<F, A>, faNew: Kind<F, A>): Kind<F, A>
}