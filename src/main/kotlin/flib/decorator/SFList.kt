package org.xrpn.flib.decorator

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLKDecorator
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FNel
import org.xrpn.flib.adt.FListNonEmpty
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.impl.FLKShreds
import org.xrpn.flib.internal.shredset.SizeMe

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
    /** delegate that holds implementation code used for the API */
    internal val ops: FLKShreds<A> = FLKShreds.build<A>()
) : FLKDecorator<A>, IdMe, SizeMe {
    /** empty by default */
    private val list: FList<A> by lazy { llist }
    private lateinit var llist: FList<A>
    override val size: Int by lazy { ops.fsize(this) }
    override val empty: Boolean by lazy { list is FLNil }
    override val show by lazy { (foldLeft("${FList::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }
    override val hash by lazy { (foldLeft(1549L) { acc: Long, h: A -> 31L * acc + h.hashCode() }).let{ (it xor (it ushr 32)).toInt() } }
    override fun equals(other: Any?): Boolean = other?.let { (other is SFList<*>)
        && (    (equal(other) && let {
                    assert(continuousMatches(this, other).let { size == it })
                    {(object : FLibLog {}).log(
                        msg = "same hash, but not equal\nthis =$show,\nother=${other.show}\nthis =$hash,\nother=${other.hash}",
                        emitter = this@SFList
                    )}
                    true
                })
             || let { assert(continuousMatches(this, other).let { size != it })
                {(object : FLibLog {}).log(
                    msg = "equal, but different hash\nthis =$show,\nother=${other.show}\nthis =$hash,\nother=${other.hash}",
                    emitter = this@SFList
                )}
                false
            }
        )} == true
    override fun hashCode(): Int = hash
    override fun toString(): String = show
    override fun fix(): FList<A> = list
    @Suppress("UNCHECKED_CAST")
    fun fnel(): FListNonEmpty<A> =
        if (list is FLCons<A>) FNel.of(list as FLCons<A>,this)
        else throw IllegalStateException("Non empty list expected when list is empty")

    companion object {
        /** Builder of empty [SFList]<[T1]> */
        fun <T1 : Any> of(): SFList<T1> = SFList<T1>().also { it.llist = FLNil<T1>() }

        /** Builder of [SFList]<[T2]> with content [flist]. */
        fun <T2 : Any> of(flist: FList<T2>): SFList<T2> = when (flist) {
            is FLNil ->  of()
            is FLCons -> SFList<T2>().also { it.llist = flist }
        }

        /**
         * Given two [SFList] with content of undetermined type, count how many
         * matches there are before a position where the corresponding items are
         * different.
         */
        internal tailrec fun continuousMatches(lhsTail: SFList<*>, rhsTail: SFList<*>, matchCount: Int = 0): Int = when {
            lhsTail.empty || rhsTail.empty -> matchCount // we're done, nothing left on either side
            lhsTail.head()!!::class != rhsTail.head()!!::class -> matchCount // nest elements are not same type
            lhsTail.head().hashCode() != rhsTail.head().hashCode() -> matchCount // next elements are not equal, end of continuous matches
            else -> continuousMatches(lhsTail.tail(), rhsTail.tail(), /* match: heads are equal */matchCount + 1)
        }
    }
}