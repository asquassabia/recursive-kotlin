package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FSelecting<F, A> {

    /** Count the elements in [fa] that match the predicate */
    fun fcount(fa: Kind<F, A>, isMatch: (A) -> Boolean): Int
}