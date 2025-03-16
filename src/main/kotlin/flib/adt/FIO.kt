package org.xrpn.flib.adt

/**
 * The idea of [FIO] is to describe computations with side effect (e.g. computations
 * that interact with the outside world like getting input or printing output) and
 * may fail or misbehave
 */

sealed interface FIO<A>

/** producer IO */
interface FIOin<A>: FIO<A> {
    val f: () -> FWriter<A>
}
data class FIOAccept<A>(override val f: () -> FWriter<A>): FIOin<A>

/** consumer IO */
interface FIOex<A: Any>: FIO<A> {
    val f: (FWriter<A>) -> Unit
}
data class FIODump<A: Any>(override val f: (FWriter<A>) -> Unit): FIOex<A>

/** two-way IO */
interface FIOput<A, B>: FIO<A> {
    val f: (A) -> FWriter<B>
}
data class FIOPush<A, B>(override val f: (A) -> FWriter<B>): FIOput<A,B>

interface FIORes<A>: () -> A?
interface FIOFail<A,T:Any>: FIORes<A> { val reason:T }
interface FIODone<A>: FIORes<A>
