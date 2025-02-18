package org.xrpn.flib.adt

import org.xrpn.flib.attribute.Kind

typealias FLK<T> = Kind<FList<@UnsafeVariance T>, @UnsafeVariance T>

/**
 * FList is a recursive list defined in terms of this ADT (Algebraic Data
 * Type).
 *
 * @param T type of the list contents. Here, <out T> is a bound on the
 *    variance of T. 'Variance' is a term that relates to inheritance.
 *    Kotlin supports inheritance, and the parametrized type T may have
 *    super- and subtypes. For instance, if I have class Sup() and class
 *    Sub():Sup(), then Sup is a supertype of Sub. Writing <out T> means
 *    that when declaring FList<out A>, FList is allowed to hold items of
 *    type A, and items that are a subtype of A (i.e. they inherit from A).
 *    Saying that FList<out T> is 'covariant' expresses the same concept
 *    with precise language. 'Covariant' means just that. The compiler will
 *    prevent adding to FList any instances that are supertypes of A.
 *    Without the 'out' bound, neither super- nor subtypes of A, but only A
 *    types (strictly A), would be allowed in FList
 */
sealed interface FList<out T>

sealed interface FListNonEmpty<out T: Any> {
    val kind: FLK<T>?
}

/**
 * List element with no content. Marks the end of the list. [Nothing] is
 * a subtype of all types. Since [FList] is 'covariant' and [FLNil] is
 * parametrized with a type which is a subtype of all types, we can always
 * add [FLNil] to [FList].
 */
data object FLNil : FList<Nothing>

/**
 * List element with content. A non-empty list has at least one [FLCons].
 * Example of non-empty list: val l: FList<Int> = FLCons(1, FLNil). 'out'
 * in the declaration [FLCons]<[T]> is implied, since it inherits from
 * [FList]. Obviously, [head] is read-only and [FLCons] is immutable only
 * to the extent that [head] is also immutable.
 */
data class FLCons<T: Any>(val head: T, val tail: FList<T>) : FList<T>


@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
data class FLNel<T: Any> private constructor (
    val fnel: FLCons<T>,
    override val kind: FLK<T>
) : FList<T>, FListNonEmpty<T> {
    companion object {
        internal fun <TT: Any> of (fl: FLCons<TT>, k: FLK<TT>) = FLNel(fl,k)
    }
}
