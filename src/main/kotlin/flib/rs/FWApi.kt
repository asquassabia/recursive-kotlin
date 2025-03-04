package org.xrpn.flib.rs

import org.xrpn.flib.adt.FWLog
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.impl.FWOps.Companion.of

fun <A,B: Any> ((A) -> B).fwrit(s:String): (A) -> FWrtMsg<B> = { a -> of(this(a), s)  }

fun <A,B: Any> FWriter<A>.andThen(msg: String, f: (A) -> B) : FWriter<B> =
    of(f(this.item), msg, (this as FWLog<*>).log)

fun <A,B: Any> FWriter<A>.bind(f: (A) -> FWrtMsg<B>): FWriter<B> {
    @Suppress("UNCHECKED_CAST")
    val fw = f(this.item) as FWLog<B>
    require(fw.log.size == 1) {
        val msg = "Internal Error -- FWrtMsg must contain only one message"
        (object : FLibLog{}).log(msg,fw)
        msg
    }
    return of(fw.item, fw.msg, (this as FWLog<A>).log)
}

fun <A: Any> fwStartValue(a:A): FWrtMsg<A> = of (a, "start value")
fun <A: Any> fwStartValue(a:A, msg: String): FWrtMsg<A> = of (a, msg)
