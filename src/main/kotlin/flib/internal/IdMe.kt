package org.xrpn.flib.internal

interface IdMe {
    /** Stack safe hash code of [IdMe]. */
    val hash: Int
    /** Stack safe [String] representation of [IdMe] */
    val show: String
    fun <T: IdMe> equal(other: T): Boolean = this === other || ( other::class == this::class && hash == other.hash )
}