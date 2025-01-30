package org.xrpn.flib.attribute

/**
 * [Kind] is the conventional signature of a type constructor in Kotlin.
 *
 * [Kind] allows the type system of the compiler to keep track of the
 * (usually) outer type [K] and the (usually) inner type [A] without
 * requiring the complexity of, say, the Scala compiler. This is a
 * convention, and the compiler does not enforce it, so let coders beware.
 *
 * About type constructors...
 *
 * [Kind]<Container<[A]>,[A]> is a type constructor. This means, putting it
 * simply, [Kind]<[F],[A]> is a construct that holds elements of inner type
 * [A] in a parametrized wrapper named Container[A].
 *
 * Think of [F] as representing the parametrized type Container[A].
 * This approach is necessary because Kotlin does not support nested
 * parametrized types such as <C<D>>. If it did, we could have written more
 * concisely the following type-level equality (let's call this operation
 * ====)
 *
 *      [Kind]<Container<[A]>,[A]> ==== Identifier<C<D>>
 *
 * to mean that [Kind] is an identifier parametrized with a parametrized
 * type. This, for compiler writers, becomes complicated very fast because
 * at this point the compiler must unequivocally assure the legality
 * of anything that happens when doing things with Identifier<C<D>>.
 *
 * Kotlin, again, does nothing in this regard. However, the workaround
 * [Kind]<[F],[A]> as a replacement for Identifier<C,D>> goes a long way
 * in making things possible (and a little easier to understand) that
 * otherwise would not be. Of course, there are limitations. This is a
 * workaround, after all, and at some point it runs into trouble, which is
 * to be expected of most workarounds.
 *
 * CREDIT: see the excellent book "Functional Programming in Kotlin" for
 * a lot more ins and outs about this technique, which here is liberally
 * and rather sloppily adapted. The book does a finer job. This adaptation
 * tries to be easier to understand, I hope, and runs into trouble faster,
 * I'm afraid. Perhaps it's easier to make sense of at least a little, when
 * approaching type constructors.
 */
interface Kind<in F, A> {
    /**
     * Cast [Kind]<[F],[A]> to [F] (e.g. Container[A]) so we can resolve the
     * abstraction. It's called [fix] because, after all, this is a workaround.
     */
    fun fix(): @UnsafeVariance F = @Suppress("UNCHECKED_CAST") (this as F)
}