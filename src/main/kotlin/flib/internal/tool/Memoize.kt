package org.xrpn.flib.internal.tool

import java.util.concurrent.ConcurrentHashMap

data class Memoize<T:Any, R:Any> (val f:(T) -> R):(T) -> R {
    val cache = ConcurrentHashMap<T, R>()
    override fun invoke(t: T): R = cache.getOrPut(t) { f(t) }
}