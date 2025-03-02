package org.xrpn.flib.internal.tool

data class RunOnce<A, M: Any>(val f: (A) -> M): (A) -> M {
    var cache: M? = null
    override fun invoke(a:A): M = cache ?: f(a).also { cache = it }
}
