package org.xrpn.flib.pattern

interface DITarget<in D: Any> {
    fun accept(dependency: D)
}

data class InjectionTarget<D: Any>(var d: D) : DITarget<D> {
    override fun accept(dependency: D) { d = dependency }
}

interface DISource<out D: Any> {
    fun provide(): D
}

data class InjectionSource<D: Any>(val d: D) : DISource<D> {
    override fun provide(): D = d
}