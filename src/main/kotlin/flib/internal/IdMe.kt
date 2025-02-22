package org.xrpn.flib.internal

interface IdMe {
    /** Stack safe hash code of [IdMe]. */
    val hash: Int
    /** Stack safe [String] representation of [IdMe] */
    val show: String
    fun equal(other: IdMe): Boolean =  this === other || hash == other.hash

}