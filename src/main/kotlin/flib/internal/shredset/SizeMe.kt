package org.xrpn.flib.internal.shredset

interface SizeMe {
    /** Number of elements in [SizeMe] */
    val size: Int
    /** True if [SizeMe] has no elements*/
    val empty: Boolean
        get() = 0 == size
    val ne: Boolean
        get() = !empty
}