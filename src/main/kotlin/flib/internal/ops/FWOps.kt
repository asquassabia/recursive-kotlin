package org.xrpn.flib.internal.ops

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FWLog
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.impl.KFList

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class FWOps<A: Any> private constructor (
    private val trace: KFList<String>
) { init { require(trace.ne) }
    internal val size by lazy { trace.size }

    companion object {

        internal fun <T: Any> of(a:T, s: String): FWrtMsg<T> = object: FWrtMsg<T>, FWLog<T> {
            override val log = FWOps<T>(KFList.of(FLCons(s,FLNil)))
            override val msg: String = s
            override val item: T = a
        }
        internal fun <T: Any> of(a:T, s: String, t: FWOps<*>): FWrtMsgs<T> = object: FWrtMsgs<T>, FWLog<T> {
            override val log = FWOps<T>(KFList.of(FLCons(s,t.trace.fix())))
            override val msg: String = s
            override val item: T = a
        }
    }
}
