package org.xrpn.flib.pattern

/*
 * CREDIT: Functional Programming in Kotlin
 */

/**
 * [KMonad] (read, Monad) is the trade signature name for whatever has a
 * [flatMap] (a name that Kotlin maybe borrowed from Scala) function and a
 * [lift] function. In Scala, [lift] is called 'unit'. I use [lift] here
 * because in my opinion it's more descriptive.
 */

interface KMonad<F> : KFunctor<F>, KLift<F> {


    /** More about [flatMap] later. */
    fun <TA : Any, TB : Any> flatMap(fa: Kind<F, TA>, f: (TA) -> Kind<F, TB>): Kind<F, TB>

    /**
     * Every [KMonad] becomes a [KFunctor] because it is possible to implement
     * [KFunctor.map] using [flatMap] and [lift]. For instance, here is an
     * example of how to do it (see credits for attribution). However, in this
     * implementation the default map function as coded below is more than
     * likely not efficient, and should be overridden.
     */
    override fun <MA : Any, MB : Any> map(
        fa: Kind<F, MA>,
        f: (MA) -> MB
    ): Kind<F, MB> = // TODO()
        flatMap(fa) { a -> lift(fa, f(a)) as Kind<F, MB> }

    fun <M2A : Any, M2B : Any, M2C : Any> map2(
        fa: Kind<F, M2A>,
        fb: Kind<F, M2B>,
        f: (M2A, M2B) -> M2C
    ): Kind<F, M2C> = // TODO()
        flatMap(fa) { a -> map(fb) { b -> f(a, b) } }
}