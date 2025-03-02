package org.xrpn.flib.decorator

import org.xrpn.flib.EQUALS_DEBUG
import org.xrpn.flib.SAFE_RECURSION_SIZE
import org.xrpn.flib.adt.FListApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FListSafe
import org.xrpn.flib.api.size
import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.impl.FLKShreds
import org.xrpn.flib.internal.shredset.SizeMe
import org.xrpn.flib.internal.tool.continuousMatches
import org.xrpn.flib.internal.tool.flistBuilder
import org.xrpn.flib.internal.tool.flistReverseBuilder

/**
 * A utility wrapper that provides boilerplate functionality for [FList] without
 * polluting the adt with crud. The content is parametrized, and the actual type
 * instance must be an immutable type. All behavior assumes that this is the case.
 * Terrible things may happen if this assumption is violated. Enforcing the assumption
 * is at best baroque, if possible at all, and at worst imbued with a false sense of
 * security.
 */

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class SFList<A: Any> private constructor(
    private val list: FList<A>,
    private val shallowEquals: Boolean = true,
    private var ops: FListApi<A>? = null
): FListSafe<A>, IdMe, SizeMe {
    override val size: Int = list.size()
    override val hash: Int by lazy { (31L * SFList::class.simpleName!!.hashCode().toLong() + ops!!.hash.toLong()).let{ (it xor (it ushr 32)).toInt() } }
    override val show: String by lazy { ops!!.show }
    override val empty: Boolean by lazy { ops!!.empty }
    override val ne: Boolean by lazy { ops!!.ne  }
    override fun hashCode(): Int = hash
    override fun toString(): String = show
    // override fun fix(): FList<A> = list
    override fun equals(other: Any?): Boolean = other?.let { (it as? SFList<*>)?.let {
        if (EQUALS_DEBUG.get()) {
            if(equal(it)) {
                val otherSize = continuousMatches(list, it.list)
                assert (size == otherSize) {(object : FLibLog {}).log(
                    msg = "same hash, but not equal\nthis =$show,\nother=${it.show}\nthis =$hash,\nother=${it.hash}",
                    emitter = this@SFList
                )}
                true
            } else false
        } else equal(it) && (shallowEquals || size == continuousMatches(list, it.list))
    }} == true
    override fun fix(): FList<A> = list

    companion object {
        /** Builder of empty [SFList]<[T1]> */
        fun <T1 : Any> of(): FLKApi<T1> {
            val sfl = SFList<T1>(FLNil<T1>())
            sfl.ops = if (sfl.size < SAFE_RECURSION_SIZE.get()) FLKShreds.buildApi(sfl) else FLKShreds.buildSafeApi(sfl)
            return SFListAlias(sfl) as FLKApi<T1>
        }

        /** Builder of [SFList]<[T2]> with content [flist]. */
        fun <T2 : Any> of(flist: FList<T2>, deepEquals: Boolean = false): FLKApi<T2> {
            val sfl = SFList<T2>(flist,!deepEquals)
            sfl.ops = if (sfl.size < SAFE_RECURSION_SIZE.get()) FLKShreds.buildApi(sfl) else FLKShreds.buildSafeApi(sfl)
            return SFListAlias(sfl) as FLKApi<T2>
        }

        fun ofIntSeq(offset:Int, top:Int, deepEquals: Boolean = false):FLKApi<Int> {
            require(offset < top) { "offset must be smaller than top." }
            val sfl = SFList(flistBuilder(offset, top), !deepEquals)
            sfl.ops = if (sfl.size < SAFE_RECURSION_SIZE.get()) FLKShreds.buildApi(sfl) else FLKShreds.buildSafeApi(sfl)
            return SFListAlias(sfl) as FLKApi<Int>
        }
        fun ofIntSeqRev(offset:Int, top:Int, deepEquals: Boolean = false): FLKApi<Int> {
            require(offset < top) { "offset must be smaller than top." }
            val sfl = SFList(flistReverseBuilder(offset, top), !deepEquals)
            sfl.ops = if (sfl.size < SAFE_RECURSION_SIZE.get()) FLKShreds.buildApi(sfl) else FLKShreds.buildSafeApi(sfl)
            return SFListAlias(sfl) as FLKApi<Int>
        }

        private data class SFListAlias<A: Any> constructor(
            private val sfl: SFList<A>,
        ): FListApi<A> by sfl.ops!!, FListSafe<A> by sfl, FLKApi<A> {
            override fun hashCode(): Int = sfl.hashCode()
            override fun equals(other: Any?): Boolean = this === other || other?.let { when (it) {
                is SFListAlias<*> -> hashCode() == it.hashCode()
                is SFList<*> -> hashCode() == it.hashCode()
                else -> false
            }} == true
            override fun toString(): String = sfl.toString()
        }

    }
}