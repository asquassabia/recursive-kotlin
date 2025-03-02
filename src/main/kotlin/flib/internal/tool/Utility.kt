package org.xrpn.flib.internal.tool

import org.xrpn.flib.SAFE_RECURSION_SIZE
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList

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
