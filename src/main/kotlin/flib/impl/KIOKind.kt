package org.xrpn.flib.impl

import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.adt.KFlatMap
import org.xrpn.flib.adt.KGE
import org.xrpn.flib.adt.KIOE
import org.xrpn.flib.adt.KSE

sealed interface KIOKind<A: Any>
interface KIOEffect<A: Any>: Kind<KIOE<A>, A>, KIOKind<A>
interface KIOProcess<A: Any, B:Any>: Kind<KGE<A>, B>, KIOKind<B>
data class KGEKind<A: Any>(val effect: KGE<A>) : KIOEffect<A>
data class KSEKind<A: Any>(val effect: KSE<A>) : KIOEffect<A>
data class KFlatMapKind<A: Any, B: Any>(val kfm: KFlatMap<A,B>) : KIOProcess<A,B>
//data class KFailureKind<A: Any>(val t: Throwable) : KIOSomethingElse<A>
//data class KRecoveryKind<A: Any>(val kio: KIO<*>, val f: (Throwable) -> KIO<A>) : KIOSomethingElse<A>

internal data class IORunner<A: Any>(val kiok: KIOKind<A>) {
    fun eval(): Any = evalkind(kiok)
    companion object {
        fun <A : Any, B : Any> evalfm(io: KFlatMap<A, B>): B {
            val res: A = evalkge(io.kio)
            val kio: KGE<B> = io.f(res)
            return evalkge(kio)
        }
        fun <A : Any> evalkge(io: KGE<A>): A = /* wrap in case it throws */ io.f()
        fun <A : Any> evalkse(io: KSE<A>): (A) -> Unit = /* wrap in case it throws */ { a: A -> io.f(a) }
        fun <A : Any> evalkind(kio: KIOKind<A>): Any = when (kio) {
            is KGEKind<A> -> evalkge(kio.effect)
            is KSEKind<A> -> evalkse(kio.effect)
            is KIOProcess<*,A> -> evalfm((@Suppress("UNCHECKED_CAST") (kio as KFlatMapKind<A, *>)).kfm)
            is KIOEffect<*> ->  TODO("never gets here")
        }
    }
}