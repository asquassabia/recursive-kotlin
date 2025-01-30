package org.xrpn.flib.attribute

/**
 * [Monad] is the trade name for whatever has a [flatMap] function (name
 * borrowed from Scala) and a [lift] function.
 */
interface Monad<F, A> {

    /**
     * [lift] transforms, i.e. 'lifts', something of type [T] and turns it into
     * a [T] wrapped in an [F]. In a perfect world, [T] is the same as [A],
     * and we may run into trouble about it later since we're attempting a
     * workaround for nested parametrization.
     */
    fun <T> lift(a: T): Kind<F, T>

    /** More about [flatMap] later. */
    fun <B> flatMap(fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B>

    /**
     * Every [Monad] can easily become a [Functor] because it is possoble to
     * implement [Functor.map] using [flatMap] and [lift]. For instance, [mapx]
     * is an example of how to do it. The name [mapx] will not conflict with
     * [Functor.map] if somewhere there will be an entity that is-a both a
     * [Monad] and a [Functor] where [Functor.map] is written in a different
     * manner. Otherwise, it would be simple to code (using a simplistic
     * shorthand notation) [map] as [Functor.map] = [mapx]
     */
    fun <B> mapx(
        fa: Kind<F, A>,
        f: (A) -> B
    ): Kind<F, B> = flatMap(fa) { a -> lift(f(a)) }
}