package org.xrpn.flib.adt

import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.impl.FListKind
import org.xrpn.flib.impl.prepend
import org.xrpn.flib.impl.head

sealed interface KWriter<A>: Kind<KWriter<A>,A> { val item: A
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
            KWTrace(a,FListKind.empty<String>().prepend(msg))
        internal fun <A: Any> of(a:A, msg1:String, msg2:String): KWTrace<A> =
            KWTrace(a,FListKind.empty<String>().prepend(msg1).prepend(msg2))
        internal fun <A: Any, B: Any> push(b:B, newMsg: String, mts:KWTrace<A>): KWTrace<B> =
            KWTrace(b,mts.trace.prepend(newMsg))
    }
}

