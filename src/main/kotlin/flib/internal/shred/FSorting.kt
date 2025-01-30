package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.internal.shredset.FSortable

internal interface FSorting<F, A> where A : Any, A : Comparable<A> {

    /** Return [fa] sorted in ascending order according to [A]'s order */
    fun fsortAscending(fa: Kind<FSortable<F, A>, A>): Kind<F, A>

    /** Return [fa] sorted in descending order according to [A]'s order */
    fun fsortDescending(fa: Kind<FSortable<F, A>, A>): Kind<F, A>

}