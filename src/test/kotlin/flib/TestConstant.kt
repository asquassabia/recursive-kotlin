package flib

import org.xrpn.flib.impl.FListKind
import org.xrpn.flib.impl.prepend

const val LARGE_DEPTH = 5000
const val XLARGE_DEPTH = 10000
// builds list (
tailrec fun listKindReverseBuilder(l: FListKind<Int>, c: Int, s: Int = LARGE_DEPTH): FListKind<Int> =
    if (c == s) l else listKindReverseBuilder(l.prepend(c), c+1, s)
tailrec fun listKindBuilder(l: FListKind<Int>, c: Int, s: Int = LARGE_DEPTH): FListKind<Int> =
    if (s == c) l else listKindBuilder(l.prepend(s-1), c, s-1)

