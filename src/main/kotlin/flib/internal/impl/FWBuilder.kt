package org.xrpn.flib.internal.impl

import org.xrpn.flib.ERR_BY_TAG
import org.xrpn.flib.ERR_TAG
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FWLog
import org.xrpn.flib.adt.FWriter
import org.xrpn.flib.adt.FWrtMsg
import org.xrpn.flib.adt.FWrtMsgs
import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.shredset.SizeMe
import org.xrpn.flib.rs.SFList
import java.io.OutputStream
import kotlin.getValue

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class FWBuilder<A : Any> private constructor(
    private val trace: FLKApi<String>
): SizeMe, IdMe { init {
    require(trace.ne)
}
    override val size by lazy { trace.size }
    override val hash by lazy { trace.hash }
    override val show by lazy { trace.show }
    override fun toString(): String = trace.display("MESSAGES:",false)

    companion object {

        val unexpectedTerminationMsg = "$ERR_TAG evaluation terminated prematurely $ERR_BY_TAG"
        private fun <T> unexpectedTermination(here: T) = "$unexpectedTerminationMsg $here"
        private val msgsUnavailable = "FWrtMsgs(item=*, log=*)"
        private fun msgsNotInitialized(s:String) = "FWrtMsgs(item=*, log='$s',...)"
        private fun itemNotInitialized(s: String) = "FWrtMsg(item=*, msg='$s')"
        private fun tag(msg: String): String = "[ $ERR_TAG at function evaluation $ERR_BY_TAG $msg ]"
        private fun tagLog(lw: FWLog<*>) = try {
            lw.log.toString()
        } catch (e: Exception) {
            e.toString()
        }
        private fun show(fw: FWriter<*>): String = (fw as FWLog<*>).log.trace.show
        private fun <T : Any> hash(item: T, log: FWBuilder<T>): Int = 31 * item.hashCode() + log.trace.hash
        private fun <T : Any> IdMe.eq(other: Any?, item: T): Boolean =
            other?.let { it is FWrtMsg<*> && (it.item::class == item::class) && equal(it) } == true

        private fun <T> eval(lw: Lazy<FWriter<T>>, log: FWLog<*>, dest: OutputStream): T? = try {
            log.log // need to dynamically add to log here XXXQQQXXX
            TODO()
            lw.value.item
        } catch (e: Exception) {
            object : FLibLog {}.reportException(e, tagLog(log), lw, dest = dest)
            null
        }
        private fun <A,T:Any> eval(f: (A) -> T, a: A, s: String,log: FWLog<*>, dest: OutputStream = System.err): T = try {
            f(a)
        } catch (e: Exception) {
            object : FLibLog {}.reportException(e, tag(s), f, dest = dest)
            throw e
        }
        private fun <A,T:Any> eval(f: (A) -> FWrtMsg<T>, lw: Lazy<FWriter<A>>, log: FWLog<*>, dest: OutputStream): FWriter<T> = eval(lw, log, dest)?.let {
            try {
                f(it)
            } catch (e: Exception) {
                object : FLibLog {}.reportException(e, tagLog(log), f, dest = dest)
                throw e
            }
        } ?: throw IllegalStateException(unexpectedTermination(lw))
        private fun <A,T:Any> eval(f: (A) -> T, lw: Lazy<FWriter<A>>, log: FWLog<*>, dest: OutputStream = System.err): T = eval(lw, log, dest)?.let {
            try {
                f(it)
            } catch (e: Exception) {
                object : FLibLog {}.reportException(e, tagLog(log), f, dest = dest)
                throw e
            }
        } ?: throw IllegalStateException(unexpectedTermination(lw))

        internal fun <T : Any> startWith(a: T, s: String): FWrtMsg<T> = object : FWrtMsg<T>, FWLog<T>, IdMe {
            override val log = FWBuilder<T>(SFList.of(FLCons(s, FLNil())))
            override val item: T = a
            override val msg: String = s
            override fun toString(): String = show
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this.eq(other, item)
            override val hash: Int by lazy { hash(item, log) }
            override val show: String by lazy { "FWrtMsg(item=$item, msg='$s')" }
        }

        internal fun <T : Any> startWith(a: T, s: String, t: FWBuilder<*>): FWrtMsgs<T> = object : FWrtMsgs<T>, FWLog<T> {
            override val log = FWBuilder<T>(SFList.of(FLCons(s, t.trace.fix())))
            override val msg: String = s
            override val item: T = a
            override fun toString(): String = show
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this.eq(other, item)
            override val hash: Int by lazy { hash(item, log) }
            override val show: String by lazy { "FWrtMsgs(item=$item, log=${show(this)})"}
        }

        internal fun <A, T : Any> ofLazy(f: (A) -> T, a: A, s: String, dest: OutputStream = System.err): FWrtMsg<T> = object : FWrtMsg<T>, FWLog<T>, IdMe {
            override val log = FWBuilder<T>(SFList.of(FLCons(s, FLNil())))
            override val item: T by lazy { res.value }
            override val msg: String = s
            override fun toString(): String = if (res.isInitialized()) show else itemNotInitialized(s)
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this.eq(other, item)
            override val hash: Int by lazy { hash(item, log) }
            override val show: String by lazy { "FWrtMsg(item=$item, msg='$s')" }
            val res = lazy { eval(f,a,s,this,dest) }
        }

        internal fun <A, T : Any> ofLazy(f: (A) -> T, lazyWriter: Lazy<FWriter<A>>, s: String, dest: OutputStream = System.err): FWrtMsgs<T> = object : FWrtMsgs<T>, FWLog<T> {
            override val log by lazy { FWBuilder<T>(SFList.of(FLCons(s, (lazyWriter.value as FWLog<A>).log.trace.fix()))) }
            override val msg: String = s
            override val item: T by lazy { res.value }
            override fun toString(): String = if (res.isInitialized()) show else msgsNotInitialized(s)
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this.eq(other, item)
            override val hash: Int by lazy { hash(item, log) }
            val res = lazy { eval(f,lazyWriter,this,dest) }
            override val show: String by lazy {"FWrtMsgs(item=$item, log=${show(this)})"}
        }

        internal fun <A, T: Any> ofLazy(f: (A) -> FWrtMsg<T>, lazyWriter: Lazy<FWriter<A>>, dest: OutputStream = System.err): FWriter<T> = object : FWrtMsgs<T>, FWLog<T> {
            override val log by lazy { FWBuilder<T>(SFList.of(FLCons(msg, (lazyWriter.value as FWLog<A>).log.trace.fix()))) }
            override val msg: String by lazy { res.value.msg }
            override val item: T by lazy { res.value.item }
            override fun toString(): String = if (res.isInitialized()) show else msgsUnavailable
            override fun hashCode(): Int = hash
            override fun equals(other: Any?): Boolean = this.eq(other, item)
            override val hash: Int by lazy { hash(item, log) }
            val res = lazy { eval(f,lazyWriter,this, dest) }
            override val show: String by lazy { "FWrtMsgs(item=$item, log=${show(this)})" }
        }
    }

}