package org.xrpn.flib.adt

import org.xrpn.flib.impl.FListKind
import org.xrpn.flib.impl.append
import org.xrpn.flib.impl.head
import org.xrpn.flib.internal.ops.KWfOps

sealed interface KWriter<A> { val item: A
    companion object {
        internal fun <A: Any> of(a:A, msg:String): KWriter<A> =
            KWTrace.of(a, msg)
        internal fun <A: Any> of(a:A, msg1:String, msg2:String,): KWriter<A> =
            KWTrace.of(a, msg1, msg2)
        internal fun <A: Any, B: Any> push(b:B, msg: String, kw: KWriter<A>): KWriter<B> =
            KWTrace.push(b, msg, kw as KWTrace<A>)
    }
}

interface KWMsg<A: Any>: KWriter<A> { val msg:String }

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class KWTrace<A: Any> private constructor (override val item: A, internal val trace: FListKind<String>): KWMsg<A> {
    init { require(trace.ne) }
    override val msg = trace.head()!!
    companion object {
        internal fun <A: Any> of(a:A, msg:String): KWTrace<A> =
            KWTrace(a,FListKind.empty<String>().append(msg))
        internal fun <A: Any> of(a:A, msg1:String, msg2:String): KWTrace<A> =
            KWTrace(a,FListKind.empty<String>().append(msg1).append(msg2))
        internal fun <A: Any, B: Any> push(b:B, newMsg: String, mts:KWTrace<A>): KWTrace<B> =
            KWTrace(b,mts.trace.append(newMsg))
    }
}

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class KWf<A: Any, B: Any> private constructor (val f: (A) -> B, val msg: String): (A) -> KWMsg<B> {
    override fun invoke(a: A): KWMsg<B> = KWfOps.of(f)(msg)(a)
    companion object{
        fun<A: Any, B: Any> of(f: (A) -> B, msg: String) = KWf(f, msg)
    }
}