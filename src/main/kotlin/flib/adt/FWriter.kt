package org.xrpn.flib.adt

import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.impl.FWBuilder

sealed interface FWrit {  val msg: String }
sealed interface FWriter<A>: FWrit, /* FWKind<A>, */ IdMe { val item: A }
/**
 * Guaranteed to have exactly one message in the trace
 */
interface FWrtMsg<A: Any>: FWriter<A>
/**
 * One or more messages in the trace
 */
interface FWrtMsgs<A: Any>: FWriter<A>
internal interface FWLog<T: Any>: FWriter<T> { val log: FWBuilder<T> }