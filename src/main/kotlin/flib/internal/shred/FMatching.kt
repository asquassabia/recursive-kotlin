package org.xrpn.flib.internal.shred

import org.xrpn.flib.pattern.Kind

internal interface FMatching<F, A> : FFiltering<F, A> {

    /** True if all elements in [fa] match predicate */
    fun fall(fa: Kind<F, A>, isMatch: (A) -> Boolean): Boolean {
        val negated: (A) -> Boolean = { a: A -> !isMatch(a) }
        return ffindAny(fa, negated) == null
    }

    /** True if at least one element in [fa] matched predicate */
    fun fany(fa: Kind<F, A>, isMatch: (A) -> Boolean): Boolean = ffindAny(fa, isMatch) != null

    /** True if not a single element in [fa] matched predicate */
    fun fnone(fa: Kind<F, A>, isMatch: (A) -> Boolean): Boolean = ffindAny(fa, isMatch) == null

    /**
     * True if [target] exists in [fa] as a structurally equal (==) element (may or may
     * not be same instance)
     */
    fun fcontains(fa: Kind<F, A>, target: A): Boolean = ffindAny(fa) { item: A -> item == target } != null

    /**
     * True if [target] exists in [fa] as the same (===) instance
     */
    fun fcontainsStrictly(fa: Kind<F, A>, target: A): Boolean = ffindAny(fa) { item: A -> item === target } != null

}