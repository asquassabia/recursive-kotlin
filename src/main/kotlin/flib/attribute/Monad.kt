package org.xrpn.flib.attribute

/**
 * [Monad] is the trade name for whatever has a [flatMap] function (name
 * borrowed from Scala) and a [lift] function.
 */
interface Monad<F, A> {

    /** More about [flatMap] later. */
    fun <G, B : Any> flatMap(fa: Kind<F, A>, f: (A) -> Kind<G, B>): Kind<G, B>

    /**
     * Every [Monad] can easily become a [Functor] because it is possible
     * to implement [Functor.map] using [flatMap] and [lift]. For instance,
     * here, [mmap] is an example of how to do it. The name [mmap] will not
     * conflict with [Functor.map] if an entity both is-a [Monad] and is-a
     * [Functor]. In such case [Functor.map] may simply defer to [mmap] (such
     * as in [Functor.map] = [mmap]), where appropriate, or provide a different
     * implementation. Disclaimer: [mmap] is a figment of Kotlin's [Kind] used
     * as the Type Constructor workaround, and is provided primarily as an
     * example. Use carefully!
     */
    fun <G, B: Any> mmap(
        fa: Kind<F, A>,
        f: (A) -> B
    ): Kind<G, B> {
        val lifter = object : Elevator<Kind<G,*>> {
            @Suppress("UNCHECKED_CAST")
            override fun <T: Any> lift(t: T): Kind<G,T> = t as Kind<G,T>
        }
        fun fx(a: A) = lifter.lift<B>(f(a))
        return flatMap(fa) { a -> fx(a) }
    }
}