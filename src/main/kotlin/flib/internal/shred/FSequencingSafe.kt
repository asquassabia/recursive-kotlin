package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FSequencingSafe<F, A> {

    /**
     * Return everything except the last element of [fa]. Stack safe and
     * potentially slower.
     */
    fun finitSafe(fa: Kind<F, A>): Kind<F, A>
}