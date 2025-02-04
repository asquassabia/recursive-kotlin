package org.xrpn.flib.internal.ops

import org.xrpn.flib.attribute.Elevator
import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.internal.attributeset.FunMon

@ConsistentCopyVisibility
data class Dummy<A:Any> private constructor (val value: A, internal val ops: FunMon<Dummy<A>, A> = DummyOps<A>().get())
: Kind<Dummy<A>,A>, Elevator<Dummy<*>> {
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> lift(t: T): Dummy<T> = of(t)
    companion object {
        fun <A: Any> of(a: A) = Dummy(a)
    }
}

internal class DummyOps<T: Any> (private val i: FunMon<Dummy<T>,T> = buildOps<T>()) {
    @Volatile
    private var instance: FunMon<Dummy<T>,T>? = null
    fun get(): FunMon<Dummy<T>,T> = instance ?: synchronized(this) {
        instance ?: i.also { instance = it }
    }
    companion object {
        private fun <A : Any> buildOps() = object : FunMon<Dummy<A>, A> {

            override fun <G, B : Any> flatMap(
                fa: Kind<Dummy<A>, A>,
                f: (A) -> Kind<G, B>
            ): Kind<G, B> = f(fa.fix().value)

            override fun <G, B : Any> map(
                fa: Kind<Dummy<A>, A>,
                f: (A) -> B
            ): Kind<G, B> = this.mmap(fa,f)  // (Dummy.of(f(fa.fix().value)) as Kind<Dummy<B>, B>) as Kind<G, B>
        }
    }
}

fun <A:Any, B: Any> Dummy<A>.map(f: (A) -> B): Dummy<B> = ops.map<Dummy<B>,B>(this,f).fix()
fun <A:Any, B: Any> Dummy<A>.flatMap(f: (A) -> Dummy<B>): Kind<Dummy<B>,B> = ops.flatMap<Dummy<B>,B>(this,f)
