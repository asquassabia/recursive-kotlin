package org.xrpn.flib.impl
import org.xrpn.flib.adt.KWriter
import org.xrpn.flib.internal.ops.KWfOps

fun <A:Any, B:Any> KWf<A,B>.bind(kw: KWriter<A>): KWriter<B> = KWfOps.bind(this, kw)
fun <A:Any, B:Any, C:Any> KWf<A,B>.chain(kwf: KWf<B,C>): (A) -> KWriter<C> = KWfOps.chain(this, kwf)
fun <A:Any, B:Any, C:Any> KWf<B,C>.compose(kwf: KWf<A,B>): (A) -> KWriter<C> = KWfOps.compose(this, kwf)
