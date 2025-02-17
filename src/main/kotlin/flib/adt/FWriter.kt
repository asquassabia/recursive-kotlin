package org.xrpn.flib.adt

import org.xrpn.flib.attribute.KMonad
import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.impl.KFList
import org.xrpn.flib.impl.head
import org.xrpn.flib.impl.prepend

typealias FWK<T> = Kind<FWriter<@UnsafeVariance T>, @UnsafeVariance T>

sealed interface FWriter<A>: FWK<A> {
    val item: A
}

interface FWMsg<A: Any>: FWriter<A> {
    val msg: String
}

interface FWMsgs<A: Any>: FWriter<A> {
    val msgs: String
}


@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class FWTrace<A: Any> private constructor (
    override val item: A,
    internal val trace: KFList<String>
): FWMsg<A>, KMonad<FWriter<*>> { init { require(trace.ne) }
    override val msg by lazy { trace.head()!! }

    override fun <TA : Any, TB : Any> flatMap(
        fa: Kind<FWriter<*>, TA>,
        f: (TA) -> Kind<FWriter<*>, TB>
    ): Kind<FWriter<*>, TB> {
        @Suppress("UNCHECKED_CAST")
        val ta = fa.fix().item as TA
        @Suppress("UNCHECKED_CAST")
        val fwtb = f(ta).fix() as FWTrace<TB>
        assert (1 == fwtb.trace.size)
        @Suppress("UNCHECKED_CAST")
        return of(fwtb.item, fwtb.msg, fa as FWTrace<*>) as Kind<FWriter<*>, TB>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <LA : Any> lift(a: LA): Kind<FWriter<*>, LA> = of(a,msg,null) as Kind<FWriter<*>, LA>

    companion object {
        internal fun <T: Any> of(a:T, s: String, t: FWTrace<*>? = null): FWTrace<T> = t?.let {
          FWTrace(a,KFList.of(t.trace.prepend(s).fix())) } ?:
          FWTrace(a,KFList.of(FLCons(s,FLNil)))
    }
}

fun <A,B: Any> FWriter<A>.andThen(msg: String, f: (A) -> B) : FWriter<B> =
    FWTrace.of(f(this.item), msg, this as FWTrace)

fun <A,B: Any> FWriter<A>.bind(f: (A) -> FWMsg<B>) : (String) -> FWriter<B> = { msg: String ->
    val fw = f(this.item)
    FWTrace.of(fw.item, fw.msg, this as FWTrace)
}

fun <A,B: Any> prepare(msg: String, f: (A) -> B) : (A) -> FWriter<B> = { a: A ->
    val b = f(a)
    FWTrace.of(b, msg, null)
}
