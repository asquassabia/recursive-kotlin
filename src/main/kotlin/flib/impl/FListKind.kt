package org.xrpn.flib.impl

import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.ops.FListOps
import org.xrpn.flib.internal.ops.FSequenceKind
import org.xrpn.flib.internal.ops.IdMe

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class FListKind<T : Any> private constructor(
    /** empty by default */
    internal val list: FList<T> = FLNil,
    /** delegate that holds implemetation code used for the API */
    internal val flops: FSequenceKind<T> = FListOps.build<T>().get()
) : Kind<FListKind<T>, T>, IdMe {
    override val size: Int by lazy { flops.fsize(this) }
    override val show by lazy { (foldLeft("${FList::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }
    override val hash by lazy {  (foldLeft(1549L) { acc: Long, h: T -> 31L * acc + h.hashCode() }).let{ (it xor (it ushr 32)).toInt() } }
    override fun equals(other: Any?): Boolean = this === other || (other is FListKind<*>) && run({
        val equality = (this.hash == other.hash)
        // hash is the same IFF continuousMatches equals size
        assert( continuousMatches(this,other).let { (equality && (size == it)) || !equality } ) {(object : FLibLog {}).log(
            msg = "lh=$hash,\nrh=${other.hash},\nthis =$show,\nother=${other.show}",
            emitter = this@FListKind
        )}
        return equality
    })
    override fun hashCode(): Int = hash
    override fun toString(): String = show
    companion object {
        /** Builder of empty [FListKind]<[T]> */
        fun <T : Any> empty() = FListKind<T>()

        /** Builder of [FListKind]<[T]> with content [list]. */
        fun <T : Any> of(list: FList<T>) = FListKind<T>(list)

        /**
         * Given two [FListKind] with content of undetermined type, count how many
         * matches there are before a position where the corresponding items are
         * different.
         */
        internal tailrec fun continuousMatches(lhsTail: FListKind<*>, rhsTail: FListKind<*>, matchCount: Int = 0): Int = when {
            lhsTail.empty || rhsTail.empty -> matchCount // we're done, nothing left on either side
            lhsTail.head()!!::class != rhsTail.head()!!::class -> matchCount // nest elements are not same type
            lhsTail.head().hashCode() != rhsTail.head().hashCode() -> matchCount // next elements are not equal, end of continuous matches
            else -> continuousMatches(lhsTail.tail(), rhsTail.tail(), /* match: heads are equal */matchCount + 1)
        }
    }
}