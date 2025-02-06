package org.xrpn.flib.internal.ops

internal interface IdMe {
    /** Number of elements in [IdMe] */
    val size: Int
    /** True if [IdMe] has no elements*/
    val empty: Boolean
        get() = 0 == size
    val ne: Boolean
        get() = 0 < size
    /** Stack safe hash code of [IdMe]. */
    val hash: Int
    /** Stack safe [String] representation of [IdMe] */
    val show: String
}