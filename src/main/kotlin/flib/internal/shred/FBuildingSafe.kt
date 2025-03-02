package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

interface FBuildingSafe<F, A> {

    /** Add [item] to [fa], at the end of [fa] iff [fa] is a [Sequence] */
    fun fappendSafe(fa: Kind<F, A>, item: A): Kind<F, A>
}