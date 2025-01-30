package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FSequencing<F, A> {

    /** Return the first element of [fa] */
    fun fhead(fa: Kind<F, A>): A?

    /** Return what comes after the next element of [fa] */
    fun ftail(fa: Kind<F, A>): Kind<F, A>

    /** Return [fa] in reverse order */
    fun freverse(fa: Kind<F, A>): Kind<F, A>

    /** Return everything except the last element of [fa]. Not stack safe */
    fun finit(fa: Kind<F, A>): Kind<F, A>

    /** Return the last element of [fa] */
    fun flast(fa: Kind<F, A>): A?
}