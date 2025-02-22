package org.xrpn.flib.adt

sealed interface FIO<A: Any>
interface FIOE<A: Any>: FIO<A> { val f: () -> A }
interface FIOU<A: Any>: FIO<A> { val f: (A) -> Unit }

data class KGE<A: Any>(override val f: () -> A) : FIOE<A>
data class KSE<A: Any>(override val f: (A) -> Unit): FIOU<A>
data class KFlatMap<A: Any, B: Any>(val kio: KGE<A>, val f: (A) -> KGE<B>) : FIO<B>
//data class KFailure<out A: Any>(val t: Throwable) : KIO<A>
//data class KRecovery<A: Any>(val kio: KIO<*>, val f: (Throwable) -> KIO<A>) : KIO<A>