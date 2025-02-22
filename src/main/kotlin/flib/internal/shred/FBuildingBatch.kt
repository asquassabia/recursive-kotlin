package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FBuildingBatch<F, A> {

    /**
     * Add all element in [fax] to [fa], maintaining their sequential order if
     * [fax] is a [Sequence], before the head of [fa] iff [fa] is a [Sequence].
     * If [fa] is not a sequence, add to it all element in [fax]
     */
    fun fprepend(fa: Kind<F, A>, fax: Kind<F, A>): Kind<F, A>

    /**
     * Add all element in [fax] to [fa], maintaining their sequential order if
     * [fax] is a [Sequence], after the end of [fa] iff [fa] is a [Sequence].
     * If [fa] is not a sequence, add to it all element in [fax]
     */
    fun fappend(fa: Kind<F, A>, fax: Kind<F, A>): Kind<F, A>
}