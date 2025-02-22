package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FBuilding<F, A> {

    /** Add [item] to [fa], at the head of [fa] iff [fa] is a [Sequence] */
    fun fprepend(fa: Kind<F, A>, item: A): Kind<F, A>

    /** Add [item] to [fa], at the end of [fa] iff [fa] is a [Sequence] */
    fun fappend(fa: Kind<F, A>, item: A): Kind<F, A>
}