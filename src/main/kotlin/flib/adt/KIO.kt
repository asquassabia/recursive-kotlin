package org.xrpn.flib.adt

sealed interface KIO<A: Any>
interface KIOE<A: Any>: KIO<A>
interface KIOF<A: Any>: KIO<A>

data class KGE<A: Any>(val f: () -> A) : KIOE<A>
data class KSE<A: Any>(val f: (A) -> Unit): KIOE<A>
data class KFlatMap<A: Any, B: Any>(val kio: KGE<A>, val f: (A) -> KGE<B>) : KIOF<B>
//data class KFailure<out A: Any>(val t: Throwable) : KIO<A>
//data class KRecovery<A: Any>(val kio: KIO<*>, val f: (Throwable) -> KIO<A>) : KIO<A>