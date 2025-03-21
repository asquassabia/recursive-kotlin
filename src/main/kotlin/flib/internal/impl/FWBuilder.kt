package org.xrpn.flib.internal.impl

import org.xrpn.flib.ERR_BY_TAG
import org.xrpn.flib.ERR_TAG
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FLRDone
import org.xrpn.flib.adt.FLRFail
import org.xrpn.flib.adt.FLRes
import org.xrpn.flib.adt.FRDone
import org.xrpn.flib.adt.FRFail
import org.xrpn.flib.adt.FRK
import org.xrpn.flib.adt.FRes
import org.xrpn.flib.adt.FWK
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWriterMock
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.api.FLibEvaluationException
import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.shredset.SizeMe
import org.xrpn.flib.rs.SFList
import java.io.OutputStream
import kotlin.getValue

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class FWBuilder private constructor(
    private val trace: FLKApi<String>
): SizeMe, IdMe { init {
    require(trace.ne)
}

    override val size by lazy { trace.size }
    override val hash by lazy { trace.hash }
    override val show by lazy { trace.show }
    override fun toString(): String = tagTrace(trace)
    fun msg(): String = trace.head()!!

    companion object {

        sealed interface FWTT {  val tt: TeleTrace }

        fun of(s: String) = FWBuilder(SFList.of(FLCons(s, FLNil())))
        fun of(ss: FLKApi<String>) = FWBuilder(ss)

        val traceHeader = "MESSAGES:"
        val unexpectedTerminationMsg = "$ERR_TAG evaluation terminated prematurely $ERR_BY_TAG"
        private fun tagTrace(trace: FLKApi<String>) = trace.display(traceHeader, false)
        private fun tagErrTrace(trace: FLKApi<String>) = "evaluation failure ${tagTrace(trace)}"
        private fun <T> unexpectedTermination(here: T) = "$unexpectedTerminationMsg $here"
        private fun msgsIncomplete(s: String) = "FWrtMsgs(item=*, log='$s',...)"
        private fun msgsNotInitialized(s: String) = "FWrtMsgs(item=*, tt='$s')"
        private fun msgNotInitialized(s: String) = "FWrtMsg(item=*, msg='$s')"
        private fun tag(msg: String): String = "[ $ERR_TAG at function evaluation $ERR_BY_TAG $msg ]"
        private fun show(fw: FWriter<*>): String = TODO() // (fw as FWLog<*>).log.trace.show
        private fun <T : Any> hash(item: T, log: FWBuilder): Int = 31 * item.hashCode() + log.trace.hash

        internal data class TeleTrace(val acc: FLKApi<String> = SFList.of()) {
            fun prepend(tt: TeleTrace) = TeleTrace(acc.prepend(tt.acc))
            fun prepend(s: String) = TeleTrace(acc.prepend(s))
            override fun toString(): String = tagTrace(acc)
        }

        private fun w2tt(lw: FWriter<*>): TeleTrace = try {
            (lw as FWTT).tt
        } catch (e: Exception) {
            TeleTrace(of(e.toString()).trace)
        }

        private fun trex(flk: FLKApi<*>): FLKApi<String> = @Suppress("UNCHECKED_CAST") (flk as FLKApi<String>)

        fun <T> FRes.lift(): FRK<T> = when (this) {
            is FRDone<*> -> FRKPatterns.liftToDone<T>(this.r())
            is FRFail<*> -> FRKPatterns.liftToFail<T>(this.r())
        } as FRK<T>

        private fun <T : Any> lazyItemWithTrace(
            res: FLRes,
            flk: FLKApi<String>,
            dest: OutputStream
        ): Pair<Lazy<T>, FLKApi<String>> = when (res) {
            is FLRDone<*> -> Pair(@Suppress("UNCHECKED_CAST") (res.done as Lazy<T>), flk)
            is FLRFail<*> -> {
                val e = res.fail.value as Exception
                object : FLibLog {}.reportException(e, tagErrTrace(flk), dest = dest)
                throw FLibEvaluationException(e.toString(), e)
            }
        }

        sealed interface DCF
        private data class A<AFA : Any>(val a: AFA): DCF
        private data class FA<AFA, TFA : Any>(val f: (AFA) -> TFA, val a: AFA): DCF
        private data class FL<AFL>(val lw: Lazy<FWriter<AFL>>): DCF
        private data class FW<AFW, TFW>(val fw: (AFW) -> FWK<TFW>, val a: AFW): DCF

        private fun dcfEval(dcf: DCF, tt: TeleTrace): Pair<FLRes, TeleTrace> = when (dcf) {
            is A<*> -> Pair(FLRDone.of(dcf.a),tt)
            is FA<*,*> -> eval(dcf, tt)
            is FL<*> -> eval(dcf, tt)
            is FW<*, *> -> eval(dcf, tt)
            is FWInitial<*> -> Pair(FLRDone.of(dcf.item), dcf.tt)
            // is FWNext<*> -> TODO()
            is FWLNext<*> -> TODO()
        }

        // ============ eval

        private fun eval(
            w: FWriter<*>
        ): FLRes = try {
            FLRDone.of(w.item)
        } catch (e: Exception) {
            FLRFail.of(e)
        }

        private fun eval(
            fl: FL<*>,
            tt: TeleTrace
        ): Pair<FLRes, TeleTrace> = try {
            val v: FWriter<*> = fl.lw.value
            Pair(eval(v),  tt.prepend(w2tt(v)))
        } catch (e: Exception) {
            Pair( FLRFail.of(e), tt)
        }

        private fun <A, T> eval(
            fw: FW<A, T>,
            tt: TeleTrace
        ) : Pair<FLRes, TeleTrace> = try {
            val w: FWriter<T> = fw.fw(fw.a).fix()
            Pair(eval(w),  tt.prepend(w2tt(w)))
        } catch (e: Exception)  {
            Pair(FLRFail.of(e), tt)
        }

        private fun <A, T : Any> eval(
            fa: FA<A, T>,
            tt: TeleTrace
        ) : Pair<FLRes, TeleTrace> = try {
            Pair(FLRDone.of(fa.f(fa.a)), tt)
        } catch (e: Exception) {
            Pair(FLRFail.of(e), tt)
        }

        internal class FWInitial<T : Any>(val a: T, val s: String): FWTT, DCF, FWriterMock<T>, IdMe {
            override val tt = TeleTrace(of(s).trace)
            private val inner: Pair<FLRes, TeleTrace> = dcfEval(A(a),tt)
            private val log = FWBuilder(tt.acc)
            override val item: T by lazy { lItem.value }
            override val msg: String = log.msg()
            override val hash: Int by lazy { hash(item, log) }
            override val show: String by lazy { "${FWriterMock::class.simpleName}(item=$item, msg='$s')" }
            private val lItem = inner.first.r<T>()
            override fun toString(): String = if (lItem.isInitialized()) show else msgNotInitialized(s)
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this === other
        }

//        private open class FWNext<T : Any> (val a: T, val t: FWBuilder): FWrtMsgs<T>, FWTT, DCF {
//            override val tt = TeleTrace(t.trace)
//            private val inner: Pair<FLRes, TeleTrace> = dcfEval(A(a),tt)
//            val log = FWBuilder(tt.acc)
//            private val lItem = inner.first.r<T>()
//            override val item: T by lazy { lItem.value }
//            override val msg: String = log.msg()
//            override val hash: Int by lazy { hash(item, log) }
//            override val show: String by lazy { "${FWrtMsgs::class.simpleName}(item=$item, tt='$tt')" }
//            override fun toString(): String = if (lItem.isInitialized()) show else msgsNotInitialized(msg)
//            override fun hashCode(): Int = hash
//            override fun equals(other: Any?): Boolean = this === other
//            override fun fix(): FWriter<T> = this as FWrtMsgs<T>
//        }

        internal class FWLNext<T : Any> (val a: Lazy<T>, val t: FWBuilder): FWrtMsgs<T>, FWTT, DCF {
            override val tt = TeleTrace(t.trace)
            val log = t
            override val item: T by lazy { a.value }
            override val msg: String = log.msg()
            override val hash: Int by lazy { hash(item, log) }
            override val show: String by lazy { "${FWrtMsgs::class.simpleName}(item=$item, tt='$tt')" }
            override fun toString(): String = if (a.isInitialized()) show else msgsNotInitialized(msg)
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this === other
            override fun fix(): FWriter<T> = this as FWrtMsgs<T>
        }

        internal fun <AFAS:Any, TFAS : Any> ofLazy(
            init: FWInitial<AFAS>,
            f: (AFAS) -> TFAS,
            dest: OutputStream = System.err
        ) : FWK<TFAS> {
            val startValue = dcfEval(init,TeleTrace())
            val fa = FA(f,startValue.first.r<AFAS>().value)
            // evaluation of f and single message
            val res: Pair<Lazy<TFAS>, FLKApi<String>> = run {
                val (res, tele) = dcfEval(fa, startValue.second)
                lazyItemWithTrace(res, tele.acc, dest)
            }
            return FWLNext(res.first,of(res.second))
        }

        internal fun <AFLS: Any, TFLS : Any> ofLazy(
            w: FWriter<AFLS>,
            s: String,
            f: (AFLS) -> TFLS,
            dest: OutputStream = System.err
        ) : FWK<TFLS> {
            val res1: Pair<Lazy<AFLS>, FLKApi<String>> = when (w) {
                is FWLNext<AFLS> ->
                    lazyItemWithTrace<AFLS>(FLRDone.of(w.item), w.tt.acc.prepend(s), dest)
                else -> {
                    val fl = FL(lazy { w })
                    val (res, tele) = eval(fl, TeleTrace(of(s).trace))
                    lazyItemWithTrace<AFLS>(res, tele.acc, dest)
                }
            }
            val fa = FA(f,res1.first.value)
            val res2: Pair<Lazy<TFLS>, FLKApi<String>> = run {
                val (res, tele) = eval(fa, TeleTrace(res1.second))
                lazyItemWithTrace<TFLS>(res, tele.acc, dest)
            }
            return FWLNext(res2.first,of(res2.second))
        }

        internal fun <AFLS: Any, TFLS : Any> ofLazy(
            wm: FWriterMock<AFLS>,
            s: String,
            f: (AFLS) -> TFLS,
            dest: OutputStream = System.err
        ) : FWK<TFLS> {
            val res1: Pair<Lazy<AFLS>, FLKApi<String>> = Pair(lazy {wm.item}, (wm as FWTT).tt.prepend(s).acc)
            val fa = FA(f,res1.first.value)
            val res2: Pair<Lazy<TFLS>, FLKApi<String>> = run {
                val (res, tele) = eval(fa, TeleTrace(res1.second))
                lazyItemWithTrace<TFLS>(res, tele.acc, dest)
            }
            return FWLNext(res2.first,of(res2.second))
        }

        internal fun <AFWL: Any, TFWL : Any> ofLazy(
            f: (AFWL) -> FWK<TFWL>,
            w: FWriter<AFWL>,
            dest: OutputStream = System.err
        ): FWK<TFWL> {
            val res1: Pair<Lazy<AFWL>, FLKApi<String>> = when (w) {
                is FWLNext<AFWL> ->
                    lazyItemWithTrace<AFWL>(FLRDone.of(w.item), w.tt.acc, dest)
                else -> {
                    val fl = FL(lazy { w })
                    val (res, tele) = eval(fl,TeleTrace())
                    lazyItemWithTrace<AFWL>(res, tele.acc, dest)
                }
            }
            val fw = FW(f,res1.first.value)
            val res2: Pair<Lazy<TFWL>, FLKApi<String>> = run {
                val (res, tele) = eval(fw, TeleTrace(res1.second))
                lazyItemWithTrace(res, tele.acc, dest)
            }
            return FWLNext(res2.first,of(res2.second))
        }

        internal fun <AFWL: Any, TFWL : Any> ofLazy(
            f: (AFWL) -> FWK<TFWL>,
            wm: FWriterMock<AFWL>,
            dest: OutputStream = System.err
        ): FWK<TFWL> {
            val fw = FW(f,wm.item)
            val res2: Pair<Lazy<TFWL>, FLKApi<String>> = run {
                val (res, tele) = eval(fw, (wm as FWTT).tt)
                lazyItemWithTrace(res, tele.acc, dest)
            }
            return FWLNext(res2.first,of(res2.second))
        }
    }
}