package org.xrpn.flib.pattern

/**
 * [Kind] is the conventional signature of a nested parametrized type.
 *
 * We'll use it to keep track of nested generics, e.g. [F]<[A]>, or
 * possibly [F]<[A]<[B]>>, etc. Kotlin does not support nested parametrized
 * types, and a notation like [F]<[A]> where [F] and [A] are both generics
 * will not compile.
 *
 * [Kind] is a workaround that allows ous, by convention, to enlist the
 * type system of the compiler to keep (in part) track of something which,
 * in turn, is-a [F]<[A]>. This is a convention only, and the compiler does
 * not enforce it, so let coders beware. In some cases, the parametrized
 * type [F] could be a container of sorts (the 'outer' type), and [A] the
 * type [A] of what goes in the container. In such special case, if we
 * write [Kind]<Container<[A]>,[A]> as [Kind]<[F],[A]> we mean a construct
 * that holds elements of inner type [A] in a parametrized wrapper named
 * Container[A] that, here, is labeled as [F]. There is no convention
 * requirement that [F] be a container, it can be anything. If [F]
 * happens to be a container, think of [F] as a shorthand representation
 * of the parametrized type Container<[A]>. 'Container' is a known type
 * instance, which means we know what its type is, and it is 'Container'
 * (say, we could assume that this container is a [List] -- there is
 * no uncertainty, since [List] is a type instance). However, consider
 * when [B] and [A] are not type instances, but type variables. Each is
 * a generic, a type not known beforehand. We don't know what either
 * could be. Hypothetically, we could write (this will not compile in
 * Kotlin) [List]<[B]<[A]>> to mean a [List] holding instances of [C] (a
 * type variable) in turn parametrized by [A], another type variable.
 * Note this is not the same as writing [List]<[Set]<[A]>> because
 * [Set] is a well-known type, i.e. a type instance, which is different
 * from [B], which is a type variable. IN summary, [Kind] is a type
 * identifier (you could call it a type constructor) parametrized with two
 * parametrized types: [F], and [A], which is a workaround for [F]<[A]>.
 *
 * Why [Kind], you may ask. Supporting nested parametrized types natively,
 * for compiler writers, becomes complicated very fast, because the
 * compiler must unequivocally assure the legality of anything that happens
 * when doing things with nested type variables. The designers of Kotlin
 * have chosen simplicity, instead (relatively speaking) On the other hand,
 * there are languages (Scala, for instance, or Haskell) support nested
 * parametrized types. It is my opinion that understanding nested generics
 * and learning what can be done is a challenge for most developers who
 * approach functional-style code. Kotlin will compel a developer to wrap
 * and unwrap these generics, which favors the need of understanding
 * exactly what's happening. For the purpose of learning, Kotlin
 * offers a clearer path. So to speak, it's not only easier, but also
 * necessary, to look under the hood and help the compiler do its magic.
 *
 * Still, [Kind]<[F],[A]> is a workaround, a convention adopted as a
 * replacement for nested type parametrization. Yes, it can make things
 * possible (and a little easier to understand, perhaps). Yes, of course,
 * there are limitations. Like all workarounds, at some point it runs into
 * trouble, which is to be expected.
 *
 * CREDIT: see the excellent book "Functional Programming in Kotlin" for
 * the ins and outs of this technique, which here has been liberally
 * adapted. The book does a fine job. This adaptation tries to be easier
 * to understand, I hope, and is likely to run into trouble faster, I'm
 * afraid.
 */
interface Kind<out F, in A> { //}: PKind<Kind<*,*>> {
    /**
     * Cast [Kind]<[F],[A]> to [F] so we can 'unnest' [F] and resolve the
     * abstraction. It's called [fix] because, after all, this is a fix.
     * When [Kind] is used in the meaning of [F]<[A]> the cast is safe.
     * Otherwise, [fix] must be overridden.
     */
    @Suppress("UNCHECKED_CAST")
    fun fix(): @UnsafeVariance F = (this as F)
}