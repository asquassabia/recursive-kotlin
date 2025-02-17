package org.xrpn.flib.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FLNel
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FListNonEmpty
import org.xrpn.flib.adt.IM
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.ops.KFListOps
import org.xrpn.flib.internal.ops.IdMe
import org.xrpn.flib.internal.ops.KFLOps

/**
 * A utility wrapper that provides boilerplate functionality for [FList] without
 * polluting the adt with crud. The content is parametrized, and the actual type
 * instance must be an immutable type. All behavior assumes that this is the case.
 * Terrible things may happen if this assumption is violated. Enforcing the assumption
 * is at best baroque, if possible at all, and at worst imbued with a false sense of
 * security.
 */

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class KFList<A: Any> private constructor(
    /** delegate that holds implementation code used for the API */
    internal val ops: KFLOps<FList<A>, A> = KFListOps.build<A>().get()
) : FLK<A>, IdMe {
    /** empty by default */
    private val list: FList<A> by lazy { llist }
    private lateinit var llist: FList<A>
    override val size: Int by lazy { ops.fsize(this) }
    override val show by lazy { (foldLeft("${FList::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }
    override val hash by lazy { (foldLeft(1549L) { acc: Long, h: A -> 31L * acc + h.hashCode() }).let{ (it xor (it ushr 32)).toInt() } }
    override fun equals(other: Any?): Boolean = this === other || (other is KFList<*>) && run({
        val equality = (this.hash == other.hash)
        // hash is the same IFF continuousMatches equals size
        assert( continuousMatches(this,other).let { (equality && (size == it)) || !equality } ) {(object : FLibLog {}).log(
            msg = "lh=$hash,\nrh=${other.hash},\nthis =$show,\nother=${other.show}",
            emitter = this@KFList
        )}
        return equality
    })
    override fun hashCode(): Int = hash
    override fun toString(): String = show
    override fun fix(): FList<A> = when (val fl = list) {
        is FLNil -> fl
        is FLNel -> fl.fnel
        is FLCons -> TODO("$FIX_TODO impossible code path")
    }
    @Suppress("UNCHECKED_CAST")
    fun fnel(): FListNonEmpty<A> = list as? FListNonEmpty<A> ?: throw IllegalStateException("Non empty list request when list is empty")

    companion object {
        /** Builder of empty [KFList]<[TT]> */
        fun <TT : Any> of(): KFList<TT> = KFList<TT>().also { it.llist = FLNil }

        /** Builder of [KFList]<[TT]> with content [flist]. */
        fun <TT : Any> of(flist: FList<TT>): KFList<TT> = when (flist) {
            is FLNil ->  of()
            is FLCons -> KFList<TT>().also { it.llist = FLNel.of(flist, it) }
            is FLNel -> KFList<TT>().also {
                /*
                 * This allows the same FList to be shared between two different
                 * instances of KFList. As long as FList is immutable, meaning,
                 * TT is also immutable, the behavior is safe.
                 */
                it.llist = FLNel.of(flist.fnel,it)
            }
        }

        /**
         * Given two [KFList] with content of undetermined type, count how many
         * matches there are before a position where the corresponding items are
         * different.
         */
        internal tailrec fun continuousMatches(lhsTail: KFList<*>, rhsTail: KFList<*>, matchCount: Int = 0): Int = when {
            lhsTail.empty || rhsTail.empty -> matchCount // we're done, nothing left on either side
            lhsTail.head()!!::class != rhsTail.head()!!::class -> matchCount // nest elements are not same type
            lhsTail.head().hashCode() != rhsTail.head().hashCode() -> matchCount // next elements are not equal, end of continuous matches
            else -> continuousMatches(lhsTail.tail(), rhsTail.tail(), /* match: heads are equal */matchCount + 1)
        }
    }
}