package org.xrpn.flib.internal.tool

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.SAFE_RECURSION_SIZE
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FListApi
import org.xrpn.flib.api.size
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.rs.SFList

/**
 * Given two [FList] with content of undetermined type, count how many
 * matches there are before a position where the corresponding items are
 * different.
 */
internal tailrec fun continuousMatches(lhsTail: FList<*>, rhsTail: FList<*>, matchCount: Int = 0): Int = if (
    lhsTail is FLNil || rhsTail is FLNil) /* we're done, nothing left on at least one side */ matchCount
else {
    lhsTail as FLCons
    rhsTail as FLCons
    when {
        lhsTail.head::class != rhsTail.head::class -> matchCount // nest elements are not same type
        lhsTail.head.hashCode() != rhsTail.head.hashCode() -> matchCount // next elements are not equal, end of continuous matches
        else -> continuousMatches(lhsTail.tail, rhsTail.tail, /* match: heads are equal */matchCount + 1)
    }
}

// builds FList
tailrec fun flistBuilder(offset: Int, top: Int = SAFE_RECURSION_SIZE.get(), l: FList<Int> = FLNil<Int>()): FList<Int> =
    if (top == offset) l else flistBuilder(offset, top-1, FLCons(top-1, l))
tailrec fun flistReverseBuilder(offset: Int, top: Int = SAFE_RECURSION_SIZE.get(), l: FList<Int> = FLNil<Int>()): FList<Int> =
    if (offset == top) l else flistReverseBuilder(offset+1, top, FLCons(offset,l))

private fun <T: Any> suitable(flc: FLCons<T>, sfl: SFList<*>): Boolean {
    return sfl.ne && (sfl.head()!!::class == flc.head::class) // same type
}

fun <T: Any> FList<T>.like(flk: FLK<*>): FLKApi<T> = when (this) {
    is FLCons<T> -> when (flk) {
        is SFList<*> if suitable(this,flk) -> // don't rebuild shreds
            @Suppress("UNCHECKED_CAST")
            SFList.of(this, flk as SFList<T>)
        is SFList<*> -> SFList.of(this)
        is FLKApi<*> -> {
            object: FLibLog{}.log("unexpected target when encapsulating $this", flk)
            SFList.of(this)
            TODO(FIX_TODO)
        }
    }
    is FLNil -> SFList.of()
}