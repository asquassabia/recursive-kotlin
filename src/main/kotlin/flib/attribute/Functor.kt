package org.xrpn.flib.attribute

/** [Functor] is the trade name for whatever has a [map] function */
interface Functor<F, A> {
    fun <G, B : Any> map(fa: Kind<F, A>, f: (A) -> B): Kind<G, B>
}