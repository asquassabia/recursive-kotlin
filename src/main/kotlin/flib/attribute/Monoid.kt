package org.xrpn.flib.attribute

/**
 * more about [Monoid] later
 */
interface Monoid<F,A> {
    fun fcombine(a1: A, a2: A): A
    val nil: A
    fun <B> ffoldMap(fa: Kind<F, A>, m: Monoid<F,B>, f: (A) -> B): B
}
