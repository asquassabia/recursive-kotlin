package org.xrpn.flib.effect.kio

import org.xrpn.flib.adt.KFlatMap
import org.xrpn.flib.adt.KGE
import org.xrpn.flib.adt.KSE
import org.xrpn.flib.internal.effect.FIOKind
import org.xrpn.flib.internal.effect.IORunner
import org.xrpn.flib.internal.effect.KFlatMapKind
import org.xrpn.flib.internal.effect.KGEKind
import org.xrpn.flib.internal.effect.KSEKind

fun <A: Any> KGE<A>.run() { IORunner<A>(KGEKind(this)).eval() }
fun <A: Any> KSE<A>.run() { IORunner<A>(KSEKind(this)).eval() }
fun <A: Any, B: Any> KFlatMap<A,B>.run() { IORunner<B>(KFlatMapKind(this) as FIOKind<B>).eval() }
