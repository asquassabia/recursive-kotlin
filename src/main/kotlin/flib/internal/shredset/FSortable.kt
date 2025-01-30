package org.xrpn.flib.internal.shredset

import org.xrpn.flib.internal.shred.FSorting

/**
 * Signature of a container with a natural order that may depend on its content [A]. It may hold none, one, or more
 * elements.
 */
internal interface FSortable<K, A> :
    FSequence<K,A>
    , FSorting<K, A>
        where A: Any, A: Comparable<A>
