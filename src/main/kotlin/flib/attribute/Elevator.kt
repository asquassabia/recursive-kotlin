package org.xrpn.flib.attribute

interface Elevator<G:Kind<*,*>> {
    /**
     * [lift] transforms, i.e. 'lifts', something of type [T] and turns it
     * into a [T] wrapped in a container [G]. At this time, we don't know what the
     * container will be.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T: Any> lift(t: T): G = (t as Kind<*,T>) as G
}

