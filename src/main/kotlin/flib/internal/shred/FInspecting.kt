package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FInspecting<F, A> {

    /** Peek at one random, easy-to-get (i.e. cheap to get) element */
    fun fpick(fa: Kind<F, A>): A?
}