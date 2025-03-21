package org.xrpn.flib.adt

sealed interface FRes {
    fun <T> r(): T {
        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is FRDone<*> -> done as T
            is FRFail<*> -> fail as T
        }
    }
}

interface FRDone<T> : FRes, FRK<T> {
    val done: T
    companion object {
        fun <T> of(t: T): FRDone<T> = object : FRDone<T> { override val done = t }
    }
}
interface FRFail<T> : FRes, FRK<T> {
    val fail: T
    companion object {
        fun <T> of(t: T): FRFail<T> = object : FRFail<T> { override val fail = t }
    }
}