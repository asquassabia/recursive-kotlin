package org.xrpn.flib.api

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FNel

fun <T: Any, B> FList<T>.fold(z: B, f: (B, T) -> B): B = this.foldLeft(z,f)
fun <T: Any, B> FList<T>.foldLeft(z: B, f: (B, T) -> B): B {
    tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B = when (xs) {
        is FLNil -> z
        is FLCons -> go(xs.tail, f(z, xs.head), f)
        is FNel -> go(xs.fix().tail, f(z, xs.fix().head), f)
    }
    return go(this, z, f)
}
fun <T: Any> FList<T>.head(): T? = when (this) {
    is FLNil -> null
    is FLCons -> this.head
    is FNel -> this.fix().head
}
fun <T: Any> FList<T>.prepend(item: T): FList<T> = FLCons(item, this)
fun <T: Any> FList<T>.reverse(): FList<T> =
    @Suppress("UNCHECKED_CAST")
    this.foldLeft(FLNil as FList<T>) { b, a -> FLCons(a, b) })
fun <T: Any> FList<T>.tail(): FList<T> = when (this) {
    is FLNil -> this
    is FLCons -> this.tail
    is FNel -> this.fix().tail
}

