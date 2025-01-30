package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind

internal interface FFiltering<F, A> {

    /** Return all elements that match the predicate [isMatch] */
    fun ffilter(fa: Kind<F, A>, isMatch: (A) -> Boolean): Kind<F, A>

    /** Return all elements that do not match the predicate [isMatch] */
    fun ffilterNot(fa: Kind<F, A>, isMatch: (A) -> Boolean): Kind<F, A>

    /** Return an element, if any, that matches the predicate [isMatch] */
    fun ffindAny(fa: Kind<F, A>, isMatch: (A) -> Boolean): A?
}