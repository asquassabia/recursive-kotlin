package org.xrpn.flib.api

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList

fun <T: Any> FList<T>.append(item: T): FList<T> = foldRight(FLCons(item, FLNil<T>())) { t, fl -> FLCons(t, fl) }

fun <T: Any> FList<T>.appendSafe(item: T): FList<T> = foldRightSafe(FLCons(item, FLNil<T>())) { t, fl -> FLCons(t, fl) }

fun <T: Any> FList<T>.count(isMatch: (T) -> Boolean): Int {
    val f: (acc: Int, T) -> Int = { acc, item -> if (isMatch(item)) acc + 1 else acc }
    return foldLeft(0, f)
}

fun <T: Any> FList<T>.empty(): Boolean = this is FLNil

fun <T: Any, B> FList<T>.fold(z: B, f: (B, T) -> B): B = this.foldLeft(z,f)

fun <T: Any, B> FList<T>.foldLeft(z: B, f: (B, T) -> B): B {
    tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B = when (xs) {
        is FLNil -> z
        is FLCons -> go(xs.tail, f(z, xs.head), f)
    }
    return go(this, z, f)
}

fun <T: Any, B> FList<T>.foldRight(z: B, f: (T, B) -> B): B {
    fun go(xs: FList<T>, z: B, f: (T, B) -> B): B = when (xs) {
        is FLNil -> z
        is FLCons -> f(xs.head, go(xs.tail, z, f))
    }
    return go(this, z, f)
}

fun <T: Any, B> FList<T>.foldRightSafe(z: B, f: (T, B) -> B): B {
    data class args(val xs: FList<T>, val z: B, val f: (T, B) -> B)
    fun goDeep(a: args): B = DeepRecursiveFunction<args, B> { a ->
        when (a.xs) {
            is FLNil -> z
            is FLCons -> a.f(a.xs.head, callRecursive(args(a.xs.tail, z, f)))
        }
    }(a)
    return goDeep(args(this, z, f))
}

fun <T: Any> FList<T>.head(): T? = when (this) {
    is FLNil -> null
    is FLCons -> head
}

fun <T: Any> FList<T>.init(): FList<T> {
    fun go(xs: FList<T>, z: FList<T>, f: (T, FList<T>) -> FList<T>): FList<T> = when (xs) {
        is FLCons if (xs.tail is FLNil && z !is FLNil) -> z
        is FLCons if xs.tail is FLNil -> FLNil()
        is FLCons -> f(xs.head, go(xs.tail, z, f))
        is FLNil -> z
    }
    return if (this is FLCons && this.tail() is FLNil) this
        else go(this, FLNil<T>(), { item, l -> FLCons(item, l) })
}

fun <T: Any> FList<T>.initSafe(): FList<T> {
    data class args(val xs: FList<T>, val z: FList<T>, val f: (T, FList<T>) -> FList<T>)
    fun goDeep(a: args) = DeepRecursiveFunction<args, FList<T>> { (xs, z, f) ->
        when (xs) {
            is FLCons if (xs.tail is FLNil && z !is FLNil) -> z
            is FLCons if xs.tail is FLNil -> FLNil()
            is FLCons -> f(xs.head, callRecursive(args(xs.tail, z, f)))
            is FLNil -> z
        }
    }(a)
    return if (this is FLCons && this.tail() is FLNil) this
        else goDeep(args(this, FLNil<T>(), { item, l -> FLCons(item, l) }))
}

fun <T: Any> FList<T>.last(): T? {
    tailrec fun go(xs: FList<T>, count: Int): T? = when (xs) {
        is FLCons if (xs.tail is FLNil && 0 == count) -> null
        is FLCons if xs.tail is FLNil -> xs.head
        is FLNil -> null
        is FLCons -> go(xs.tail, count+1)
    }
    return go(this,0)
}

fun <T: Any> FList<T>.ne(): Boolean = this !is FLNil

fun <T: Any> FList<T>.pick(): T? = (this as? FLCons)?.head

fun <T: Any> FList<T>.prepend(item: T): FList<T> = FLCons(item, this)

fun <T: Any> FList<T>.reverse(): FList<T> = foldLeft(FLNil<T>() as FList<T>) { b, a -> FLCons(a, b) }

fun <T: Any> FList<T>.size(): Int = foldLeft(0) { acc, _ -> acc + 1 }

fun <T: Any> FList<T>.tail(): FList<T> = when (this) {
    is FLNil -> this
    is FLCons -> tail
}