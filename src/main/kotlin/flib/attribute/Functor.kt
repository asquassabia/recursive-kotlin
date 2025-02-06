package org.xrpn.flib.attribute

/*
 * CREDIT: Functional Programming in Kotlin
 */

/** [Functor] is the trade name for whatever has a [map] function */

interface Functor<F> {
    fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B>
}