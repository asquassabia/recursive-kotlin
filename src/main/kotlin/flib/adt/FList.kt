package org.xrpn.flib.adt

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
sealed interface FList<T>: FLK<T>

/**
 * A [List] element with no content.
 */
class FLNil<T>: FList<T>

/**
 * List element with content. A non-empty list has at least one [FLCons].
 * Example of non-empty list: val l: FList<Int> = FLCons(1, FLNil). 'out'
 * in the declaration [FLCons]<[T]> is implied, since it inherits from
 * [FList]. Obviously, [head] is read-only and [FLCons] is immutable only
 * to the extent that [head] is also immutable.
 */
data class FLCons<T: Any>(val head: T, val tail: FList<T>) : FList<T> //, FListNonEmpty<T>