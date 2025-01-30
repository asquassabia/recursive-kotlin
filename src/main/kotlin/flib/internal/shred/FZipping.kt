package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FZipping<F, A> {

    /**
     * Compose [fa] and [fb] by pairing in sequence each element [A] of [fa]
     * with the corresponding element [B] of [fb]
     */
    fun <B> fzip(fa: Kind<F, B>, fb: Kind<F, B>): Kind<F, Pair<A, B>>

    /**
     * Compose [fa] and [f] by pairing in sequence each element [A] of [fa]
     * with an element [B] obtained applying [f] to it.
     */
    fun <B> fzipMap(fa: Kind<F, B>, f: (A) -> B): Kind<F, Pair<A, B>>
}