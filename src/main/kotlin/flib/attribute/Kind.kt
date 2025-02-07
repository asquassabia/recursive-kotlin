package org.xrpn.flib.attribute

/**
 * [Kind] is the conventional signature of a nested parametrized type.
 *
 * We'll use it to keep track of nested generics, e.g. [F]<[A]>, or
 * possibly [F]<[A]<[B]>>, etc. Kotlin does not support nested parametrized
 * types, and a notation like [F]<[A]> where [F] and [A] are both generics
 * will not compile.
 *
 * [Kind] is a workaround that allows ous, by convention, to enlist the
 * type system of the compiler to keep track of the (usually) outer type
 * [F], likely a container of sorts, and of the (usually) inner type [A] of
 * the same container. This is a convention only, and the compiler does not
 * enforce it, so let coders beware.
 *
 * If we write [Kind]<Container<[A]>,[A]> as [Kind]<[F],[A]> we
 * mean a construct that holds elements of inner type [A] in a
 * parametrized wrapper named Container[A] that, here, is labeled as [F]
 *
 * Think of [F] as a shorthand representation of the parametrized type
 * Container<[A]>, whatever 'Container' may be. Here, 'Container' is a
 * type instance. We know what its type is, and it is 'Container' (say, we
 * could assume that this container is a [List] -- there is no uncertainty,
 * since [List] is a type instance). However, consider when [B] and [A] are
 * not type instances, but type variables. Each is a generic, a kind of
 * type not known beforehand. We don't know what either could be, except
 * for possibly a few constraints. Hypothetically, we could write (this
 * will not compile in Kotlin) [List]<[B]<[A]>> to mean a [List] holding
 * instances of [C] (a type variable) in turn parametrized by [A], another
 * type variable. Note this is not the same as writing [List]<[Set]<[A]>>
 * because [Set] is a well-known type, i.e. a type instance, which is
 * different from [B], which is a type variable. So, [Kind] is an type
 * identifier parametrized with two parametrized types: [F], and [A], which
 * is a workaround for [F]<[A]>.
 *
 * Supporting nested types natively, for compiler writers, becomes
 * complicated very fast, because the compiler must unequivocally assure
 * the legality of anything that happens when doing things with nested
 * type variables. Kotlin has chosen simplicity. On the other hand, Scala,
 * for instance, does support nested parametrized types. It is my opinion
 * that understanding nested generics is a challenge for most developers,
 * and Kotlin will compel a developer to understand what's happening. For
 * the purpose of learning, Kotlin offers a clearer path. Advanced Scala
 * code can become difficult to understand. Advanced Kotlin code is also
 * challenging, but--so to speak--it's easier to look under the hood.
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
 * afraid.
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