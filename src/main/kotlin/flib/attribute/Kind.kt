package org.xrpn.flib.attribute

/**
 * [Kind] is the conventional signature of a type constructor in Kotlin. We
 * use to keep track of nested generics, e.g. F<G> or possibly F<G<H>>>,
 * etc. because Kotlin does not support nested parametrized types a.k.a.
 * generics.
 *
 * [Kind] is a workaround that allows ous, by convention, to enlist the
 * type system of the compiler to keep track of the (usually) outer
 * type [F] and the (usually) inner type [A] without requiring from the
 * Kotlin compiler the complexity of, say, the Scala compiler. This is a
 * convention only, and the compiler does not enforce it, so let coders
 * beware. If you use [F] for other than the outer type things may become
 * messy.
 *
 * About type constructors...
 *
 * [Kind]<Container<[A]>,[A]> is a type constructor. This means, putting it
 * simply, [Kind]<[F],[A]> is a construct that holds elements of inner type
 * [A] in a parametrized wrapper named Container[A] that, here, is labeled
 * as [F]
 *
 * Think of [F] as a shorthand representation of the parametrized type
 * Container<[A]>, whatever 'Container' may be. If Kotlin (it does not)
 * had support for nested parametrized types such as <C<D>>. If it did, we
 * could have written more concisely a type-level equality (let's call this
 * operation ==== ) as, for instance,
 *
 *      [Kind]<Container<[A]>,[A]> ==== Container<[B]<[A]>>
 *
 * Here, 'Container' is a type instance. We know what its type is, and it
 * is 'Container' (say, we could assume that this container is a [List] --
 * there is no uncertainty, since [List] is a type instance). However, [B]
 * and [A] are not type instances, but type variables. Each is a generic, a
 * kind of type not known beforehand. We don't know what either could be,
 * except for possibly a few constraints. Hypothetically, we could write
 * (this will not compile in Kotlin) [List]<[C]<[A]>> to mean a [List]
 * holding instances of [C] (a type variable) parametrized by [A], another
 * type variable. Note this is not the same as writing [List]<[Set]<[A]>>
 * because [Set] is a well-known type, i.e. a type instance, which is
 * different from [C], which is a type variable. We have established that
 * [C] means some type, but because it's a type variable, we have very
 * limited knowledge of it. So, [Kind] is an identifier parametrized with
 * two parametrized types: [F] stands for the wrapper, and [A] stands for
 * what's inside the wrapper. Supporting nested types natively for compiler
 * writers, becomes complicated very fast, because if so the compiler must
 * unequivocally assure the legality of anything that happens when doing
 * things with type variables. Kotlin has chosen simplicity. On the other
 * hand, Scala, for instance, does support nested parametrized types.
 * It is my opinion that understanding type constructor is a challenge
 * for most developers, and Kotlin will compel a developer to understand
 * what's happening. For the purpose of learning the intricacies of what
 * type constructors mean, again in my arbitrary opinion, Kotlin offers a
 * clearer path. Advanced Scala code can become difficult to understand.
 * Advanced Kotlin code is also challenging, but--so to speak--it's easier
 * to look under the hood.
 *
 * The workaround [Kind]<[F],[A]> as a replacement for nested type
 * parametrization can make things both possible and a little easier to
 * understand. Of course, there are limitations. This is a workaround,
 * after all, and at some point it runs into trouble, which is to be
 * expected of most workarounds.
 *
 * CREDIT: see the excellent book "Functional Programming in Kotlin" for
 * the ins and outs of this technique, which here has been liberally
 * adapted. The book does a fine job. This adaptation tries to be easier
 * to understand, I hope, and is likely to run into trouble faster, I'm
 * afraid. However, I hope in succeeding, perhaps, to make it easier to
 * make sense of type constructors.
 */
interface Kind<F, A> {
    /**
     * Cast [Kind]<[F],[A]> to [F] (e.g. Container[A]) so we can resolve the
     * abstraction. It's called [fix] because, after all, this is a workaround.
     * When [Kind] is used correctly, the cast is safe.
     */
    @Suppress("UNCHECKED_CAST")
    fun fix(): @UnsafeVariance F = (this as F)
}