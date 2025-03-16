package org.xrpn.flib.api

import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.internal.impl.FWBuilder.Companion.startWith
import org.xrpn.flib.internal.impl.FWBuilder.Companion.ofLazy
import java.io.OutputStream
import java.lang.System

fun <A,B: Any> ((A) -> B).makeFWriter(msg:String, errLog: OutputStream = System.err): (A) -> FWrtMsg<B> = { a ->
    ofLazy(this, a, msg, errLog)  }

fun <A,B: Any> FWriter<A>.andThen(msg: String, f: (A) -> B, errLog: OutputStream = System.err) : FWriter<B> =
    ofLazy(f, lazy { this }, msg, errLog)

fun <A,B: Any> FWriter<A>.bind(f: (A) -> FWrtMsg<B>, errLog: OutputStream = System.err): FWriter<B> =
    ofLazy(f,lazy { this } ,errLog)

//fun <A,B: Any> FWriter<A>.bind(f: (A) -> FWrtMsgs<B>): FWriter<B> =
//    ofLazy(f,this,(this as FWLog<A>).log)

val startValueMsg = "start value"
fun <A: Any> fwStartValue(a:A): FWrtMsg<A> = startWith (a, startValueMsg)
fun <A: Any> fwStartValue(a:A, msg: String): FWrtMsg<A> = startWith (a, msg)
