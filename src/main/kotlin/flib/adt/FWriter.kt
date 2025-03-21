package org.xrpn.flib.adt

import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.impl.FWBuilder
import org.xrpn.flib.internal.impl.FWBuilder.Companion.TeleTrace

interface FWriterMock<A> {
    val msg: String
    val item: A
}
sealed interface FWrit {  val msg: String }
sealed interface FWriter<A>: FWrit, IdMe { val item: A }
/**
 * Guaranteed to have exactly one message in the trace
 */
interface FWrtMsg<A: Any>: FWriter<A>, FWInit<A>
/**
 * One or more messages in the trace
 */
interface FWrtMsgs<A: Any>: FWriter<A>, FWInit<A>

interface FWInit<A:Any>: FWK<A>