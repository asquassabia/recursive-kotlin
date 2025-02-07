package org.xrpn.flib.impl

import org.xrpn.flib.adt.KWMsg
import org.xrpn.flib.internal.ops.KWfOps

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class KWf<A: Any, B: Any> private constructor (
    val f: (A) -> B,
    val msg: String
): (A) -> KWMsg<B> {
    override fun invoke(a: A): KWMsg<B> = KWfOps.of(f)(msg)(a)
    companion object{
        fun<A: Any, B: Any> of(f: (A) -> B, msg: String) = KWf(f, msg)
    }
}