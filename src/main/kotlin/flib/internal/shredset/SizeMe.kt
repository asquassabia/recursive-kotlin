package org.xrpn.flib.internal.shredset

import org.xrpn.flib.internal.IdMe

interface SizeMe {
    /** Number of elements in [IdMe] */
    val size: Int
    /** True if [IdMe] has no elements*/
    val empty: Boolean
        get() = 0 == size
    val ne: Boolean
        get() = !empty
}