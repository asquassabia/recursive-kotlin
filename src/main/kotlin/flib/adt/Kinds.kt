package org.xrpn.flib.adt

import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.shredset.SizeMe
import org.xrpn.flib.pattern.Kind

sealed interface FLK<T>: Kind<FList<*>, T> {
    @Suppress("UNCHECKED_CAST")
    override fun fix(): FList<T> = this as FList<T>
}

interface FListSafe<A: Any>: FLK<A>
interface FListApi<T: Any>: IdMe, SizeMe {
    fun append(item: T): FListSafe<T>
    fun count(isMatch: (T) -> Boolean): Int
    fun <B> fold(z: B, f: (B, T) -> B): B
    fun <B> foldLeft(z: B, f: (B, T) -> B): B
    fun <B> foldRight(z: B, f: (T, B) -> B): B
    fun head(): T?
    fun init(): FLKApi<T>
    fun last(): T?
    fun pick(): T?
    fun prepend(item: T): FLKApi<T>
    fun reverse(): FLKApi<T>
    fun tail(): FLKApi<T>
}
interface FLKApi<A: Any>: FListSafe<A>, FListApi<A>

/**
 * the out projection to the parameter type of testType: fun
 * testType(superClass: SuperClass<out SuperType>) This will basically mean
 * that this function accepts SuperClass<T> where T is a subtype of
 * SuperType. Of course, it adds certain limitations on the superClass
 * usage: as T can be absolutely any subtype of SuperType, it is not
 * type-safe to pass anything to functions that expect T as an argument,
 * and this is prohibited.
 * https://stackoverflow.com/questions/43453771/kotlin-generics-super-child-classes
 *
 * Declaration-site variance
 * 'out' means 'produced'
 * 'in' means 'consumed'
 * Array<T> is invariant in T, and so neither Array<Int> nor Array<Any> is a subtype of the other
 *
 * when a type parameter T of a class C is declared out, it may occur only
 * in the out-position in the members of C, but in return C<out Base> can
 * safely be a supertype of C<Derived> i.e.
 */
interface Grand
interface Parent: Grand
interface Child: Parent

open class CT<T>()

val cGra = CT<Grand>()
val cPar = CT<Parent>()
val cChi = CT<Child>()

// invariant
fun <D: Parent> user(a : CT<D>): Int = TODO()

// USE-site variance
// can only call methods that consume the type parameter D
fun <D: Parent> consumer(a : CT<in D>): Int = TODO()

// can only call methods that return the type parameter D
fun <D: Parent> producer(a : CT<out D>): Int = TODO()

val cg = consumer(cGra)
val cp = consumer(cPar)
val cc = consumer(cChi)

val pp = producer(cPar)
val pc = producer(cChi)

// val ug = user(cGra) // NO, works only with open class CT<in T>()
val up = user(cPar)
val uc = user(cChi)