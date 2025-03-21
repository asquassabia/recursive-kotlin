package org.xrpn.flib.adt

sealed interface FLRes {
    fun <T> r(): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return when (this) {
            is FLRDone<*> -> done as Lazy<T>
            is FLRFail<*> -> fail as Lazy<T>
        }
    }
}

interface FLRDone<T> : FLRes, FLRK<T> {
    val done: Lazy<T>
    companion object {
        fun <T> of(t: T): FLRDone<T> = object : FLRDone<T> { override val done = lazy { t } }
        fun <T> of(t: Lazy<T>): FLRDone<T> = object : FLRDone<T> { override val done = t }
    }
}
interface FLRFail<T> : FLRes, FLRK<T> {
    val fail: Lazy<T>
    companion object {
        fun <T> of(t: T): FLRFail<T> = object : FLRFail<T> { override val fail = lazy { t } }
        fun <T> of(t: Lazy<T>): FLRFail<T> = object : FLRFail<T> { override val fail = t }
    }
}