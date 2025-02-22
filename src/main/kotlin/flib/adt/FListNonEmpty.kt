package org.xrpn.flib.adt

import org.xrpn.flib.FLK

sealed interface FListNonEmpty<in T: Any>: FLK<T>

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class FNel<T: Any> private constructor (
    private val nel: FLCons<T>,
    internal val kind: FLK<T>?
) : FList<T>, FListNonEmpty<T> {
    override fun fix(): FLCons<T> = nel
    companion object {
        internal fun <TT: Any> of(fl: FLCons<TT>, k: FLK<TT>?): FNel<TT> = FNel(fl,k)
    }
}

