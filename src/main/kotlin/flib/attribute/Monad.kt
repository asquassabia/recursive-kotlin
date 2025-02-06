package org.xrpn.flib.attribute

/*
 * CREDIT: Functional Programming in Kotlin
 */

/**
 * [Monad] is the trade name for whatever has a [flatMap] function (name
 * borrowed from Scala) and a [lift] function.
 */

interface Monad<F>: Functor<F> {

    /**
     * [lift] transforms, i.e. 'lifts', something of type [A] and turns it into an [A] wrapped in a 'type constructor' for an
     * undetermined container [F]. At this time, we don't know much about the container.
     */

    fun <A> lift(a: A): Kind<F, A>

    /** More about [flatMap] later. */
    fun <A, B> flatMap(fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B>

    /**
     * Every [Monad] can easily become a [Functor] because it is possible
     * to implement [Functor.map] using [flatMap] and [lift]. For instance,
     * here, [map] is an example of how to do it.
     */
    override fun <A, B> map(
        fa: Kind<F, A>,
        f: (A) -> B
    ): Kind<F, B> =
        flatMap(fa) { a -> lift(f(a)) }

    fun <A, B, C> map2(
        fa: Kind<F, A>,
        fb: Kind<F, B>,
        f: (A, B) -> C
    ): Kind<F, C> =
        flatMap(fa) { a -> map(fb) { b -> f(a, b) } }
}