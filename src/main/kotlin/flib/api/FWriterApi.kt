package org.xrpn.flib.api

import org.xrpn.flib.adt.FWInit
import org.xrpn.flib.adt.FWK
import org.xrpn.flib.adt.FWriterMock
import org.xrpn.flib.internal.impl.FWBuilder.Companion.FWInitial
import org.xrpn.flib.internal.impl.FWBuilder.Companion.ofLazy
import java.io.OutputStream
import java.lang.System

fun <A:Any,B: Any> ((A) -> B).toFWriter(msg:String, errLog: OutputStream = System.err): (A) -> FWK<B> = { a ->
    ofLazy(FWInitial(a, msg), this, errLog)  }

fun <A: Any,B: Any> FWK<A>.andThen(msg: String, f: (A) -> B, errLog: OutputStream = System.err) : FWK<B> =
    ofLazy(this.fix(), msg, f, errLog)

fun <A: Any,B: Any> FWriterMock<A>.andThen(msg: String, f: (A) -> B, errLog: OutputStream = System.err) : FWK<B> =
    ofLazy(this, msg, f, errLog)

fun <A: Any,B: Any> FWK<A>.bind(f: (A) -> FWK<B>, errLog: OutputStream = System.err): FWK<B> =
    ofLazy(f, this.fix(), errLog)

fun <A: Any,B: Any> FWriterMock<A>.bind(f: (A) -> FWK<B>, errLog: OutputStream = System.err) =
    ofLazy(f, this, errLog)

const val startValueMsg = "start value"
fun <A: Any> fwStartValue(a:A): FWriterMock<A> = FWInitial (a, startValueMsg)
fun <A: Any> fwStartValue(a:A, msg: String): FWriterMock<A> = FWInitial (a, msg)
