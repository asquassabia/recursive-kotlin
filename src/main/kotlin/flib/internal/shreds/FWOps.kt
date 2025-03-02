package org.xrpn.flib.internal.ops

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FWLog
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.internal.IdMe

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class FWOps<A: Any> private constructor (
    private val trace: FLKApi<String>
) { init { require(trace.ne) }
    internal val size by lazy { trace.size }
    override fun toString(): String = trace.toString()

    companion object {

        fun show(fw: FWriter<*>): String = (fw as FWLog<*>).log.trace.show

        internal fun <T: Any> of(a:T, s: String): FWrtMsg<T> = object: FWrtMsg<T>, FWLog<T>, IdMe {
            override val log = FWOps<T>(SFList.of(FLCons(s,FLNil())))
            override val msg: String = s
            override val item: T = a
            override fun toString(): String = show
            override fun hashCode(): Int =  hash
            override fun equals(other: Any?): Boolean = other?.let { it is FWrtMsg<*> && (it.item::class == item::class) && equal(it) } ?: false
            override val hash: Int by lazy { 31 * item.hashCode() + log.trace.hash }
            override val show: String by lazy { "FWrtMsg(item=$item, msg='$s')" }
        }

        internal fun <T: Any> of(a:T, s: String, t: FWOps<*>): FWrtMsgs<T> = object: FWrtMsgs<T>, FWLog<T> {
            override val log = FWOps<T>(SFList.of(FLCons(s,t.trace.fix())))
            override val msg: String = s
            override val item: T = a
            override fun toString(): String = show
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = other?.let { it is FWrtMsgs<*> && (it.item::class == item::class) && equal(it)} ?: false
            override val hash: Int by lazy { 31 * item.hashCode() + log.trace.hash }
            override val show: String by lazy { "FWrtMsgs(item=$item, log=${show(this)})" }
        }
    }
}
