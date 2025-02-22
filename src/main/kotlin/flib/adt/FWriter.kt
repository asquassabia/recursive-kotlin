package org.xrpn.flib.adt

import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.pattern.Kind
import org.xrpn.flib.internal.ops.FWOps

typealias FWKind<A> = Kind<FWriter<*>,A>

sealed interface FWrit {  val msg: String }
sealed interface FWriter<A>: FWrit, FWKind<A>, IdMe { val item: A }
/**
 * Guaranteed to have exactly one message in the trace
 */
interface FWrtMsg<A: Any>: FWriter<A>
/**
 * One or more messages in the trace
 */
interface FWrtMsgs<A: Any>: FWriter<A>
internal interface FWLog<T: Any>: FWriter<T> { val log: FWOps<T> }