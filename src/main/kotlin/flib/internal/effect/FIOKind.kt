package org.xrpn.flib.internal.effect

import org.xrpn.flib.pattern.Kind
import org.xrpn.flib.adt.KFlatMap
import org.xrpn.flib.adt.KGE
import org.xrpn.flib.adt.FIOE
import org.xrpn.flib.adt.KSE

sealed interface FIOKind<A: Any>
interface FIOEffect<A: Any>: Kind<FIOE<A>, A>, FIOKind<A>
interface FIOProcess<A: Any, B:Any>: Kind<KGE<A>, B>, FIOKind<B>
data class KGEKind<A: Any>(val effect: KGE<A>) : FIOEffect<A>
data class KSEKind<A: Any>(val effect: KSE<A>) : FIOEffect<A>
data class KFlatMapKind<A: Any, B: Any>(val kfm: KFlatMap<A,B>) : FIOProcess<A,B>
//data class KFailureKind<A: Any>(val t: Throwable) : KIOSomethingElse<A>
//data class KRecoveryKind<A: Any>(val kio: KIO<*>, val f: (Throwable) -> KIO<A>) : KIOSomethingElse<A>

internal data class IORunner<A: Any>(val kiok: FIOKind<A>) {
    fun eval(): Any = evalkind(kiok)
    companion object {
        fun <A : Any, B : Any> evalfm(io: KFlatMap<A, B>): B {
            val res: A = evalkge(io.kio)
            val kio: KGE<B> = io.f(res)
            return evalkge(kio)
        }
        fun <A : Any> evalkge(io: KGE<A>): A = /* wrap in case it throws */ io.f()
        fun <A : Any> evalkse(io: KSE<A>): (A) -> Unit = /* wrap in case it throws */ { a: A -> io.f(a) }
        fun <A : Any> evalkind(kio: FIOKind<A>): Any = when (kio) {
            is KGEKind<A> -> evalkge(kio.effect)
            is KSEKind<A> -> evalkse(kio.effect)
            is FIOProcess<*,A> -> evalfm((@Suppress("UNCHECKED_CAST") (kio as KFlatMapKind<A, *>)).kfm)
            is FIOEffect<*> ->  TODO("never gets here")
        }
    }
}