package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FCounting<F, A> {

    /** Return the number of elements in [fa] */
    fun fsize(fa: Kind<F, A>): Int

    /** Return true if [fa] has no elements ([fsize] is 0) */
    fun fempty(fa: Kind<F, A>): Boolean
}
