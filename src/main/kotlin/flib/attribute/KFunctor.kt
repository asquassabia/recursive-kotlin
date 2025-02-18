package org.xrpn.flib.attribute

/*
 * CREDIT: Functional Programming in Kotlin
 */

/** [KFunctor] is the trade name for whatever has a [map] function */

interface KFunctor<F> {
    fun <A: Any, B: Any> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B>
}