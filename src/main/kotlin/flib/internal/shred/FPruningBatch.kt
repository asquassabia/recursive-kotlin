package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FPruningBatch<F, A> {

    /** Remove from [fa] all items in [fa] which are also in [fax] */
    fun fdropAll(fa: Kind<F, A>, fax: Kind<F, A>): Kind<F, A>

    /** Remove from [fa] all matching items */
    fun fdropWhen(fa: Kind<F, A>, isMatch: (A) -> Boolean): Kind<F, A>
}