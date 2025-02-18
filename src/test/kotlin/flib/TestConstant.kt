package flib

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FList
import org.xrpn.flib.impl.KFList
import org.xrpn.flib.impl.prepend

const val LARGE_DEPTH = 5000
const val XLARGE_DEPTH = 10000

// builds FKList
tailrec fun listKindReverseBuilder(l: KFList<Int>, c: Int, s: Int = LARGE_DEPTH): KFList<Int> =
    if (c == s) l else listKindReverseBuilder(l.prepend(c), c+1, s)
tailrec fun listKindBuilder(l: KFList<Int>, c: Int, s: Int = LARGE_DEPTH): KFList<Int> =
    if (s == c) l else listKindBuilder(l.prepend(s-1), c, s-1)

// builds FList
tailrec fun flistBuilder(l: FList<Int>, c: Int, s: Int = LARGE_DEPTH): FList<Int> =
    if (s == c) l else flistBuilder(FLCons(s-1, l), c, s-1)
tailrec fun flistReverseBuilder(l: FList<Int>, c: Int, s: Int = LARGE_DEPTH): FList<Int> =
    if (c == s) l else flistReverseBuilder(FLCons(c,l), c+1, s)