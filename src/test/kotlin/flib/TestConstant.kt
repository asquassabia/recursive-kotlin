package flib

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.decorator.prepend

const val LARGE_DEPTH = 5000
const val XLARGE_DEPTH = 100000

// builds FKList
tailrec fun listKindReverseBuilder(offset: Int, top: Int = LARGE_DEPTH, l: SFList<Int> = SFList.of<Int>()): SFList<Int> =
    if (offset == top) l else listKindReverseBuilder(offset+1, top, l.prepend(offset))
tailrec fun listKindBuilder(offset: Int, top: Int = LARGE_DEPTH, l: SFList<Int> = SFList.of<Int>()): SFList<Int> =
    if (top == offset) l else listKindBuilder(offset, top-1, l.prepend(top-1))

// builds FList
tailrec fun flistBuilder(offset: Int, top: Int = LARGE_DEPTH, l: FList<Int> = FLNil<Int>()): FList<Int> =
    if (top == offset) l else flistBuilder(offset, top-1, FLCons(top-1, l))
tailrec fun flistReverseBuilder(offset: Int, top: Int = LARGE_DEPTH, l: FList<Int> = FLNil<Int>()): FList<Int> =
    if (offset == top) l else flistReverseBuilder(offset+1, top, FLCons(offset,l))