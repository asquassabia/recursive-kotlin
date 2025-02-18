package org.xrpn.flib.internal.ops

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FLNel
import org.xrpn.flib.attribute.KLift
import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.impl.KFList
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.shredset.FSequence

internal interface KFLOps<F,T: Any> : FSequence<F, T>
internal interface KFLAtt<F: FList<*>> : KLift<F>

internal class KFListOps<T: Any> private constructor (private val i: KFLOps<FList<T>, T> = buildOps<T>()) {
    @Volatile
    private var instance: KFLOps<FList<T>, T>? = null
    fun get(): KFLOps<FList<T>, T> = instance ?: synchronized(this) {
        instance ?: i.also { instance = it }
    }
    companion object {
        fun <T : Any> build() = KFListOps<T>()
        fun <T : Any> k2c(fa: FLK<T>) = fa as KFList<T>
        private fun <T : Any> buildOps() = object : KFLOps<FList<T>, T>, FLibLog {
            override fun fsize(fa: Kind<FList<T>, T>): Int = ffoldLeft(fa, 0) { acc, _ -> acc + 1 }
            override fun fempty(fa: Kind<FList<T>, T>): Boolean = fpick(fa) == null
            override fun fequal(lhs: Kind<FList<T>, T>, rhs: Kind<FList<T>, T>): Boolean =
                k2c(rhs).equals(lhs.fix())

            override fun fpick(fa: Kind<FList<T>, T>): T? = fhead(fa)
            override fun fcount(fa: Kind<FList<T>, T>, isMatch: (T) -> Boolean): Int {
                val f: (acc: Int, T) -> Int = { acc, item -> if (isMatch(item)) acc + 1 else acc }
                return ffoldLeft(fa, 0, f)
            }

            override fun fprepend(fa: Kind<FList<T>, T>, item: T): KFList<T> = k2c(fa).let {
                when (val fl = it.fix()) {
                    is FLNil -> KFList.of(FLCons(item, FLNil))
                    is FLCons -> KFList.of(FLCons(item, fl))
                    is FLNel -> KFList.of(FLCons(item, fl.fnel))
                }
            }

            override fun fappend(fa: Kind<FList<T>, T>, item: T): KFList<T> {
                TODO("$FIX_TODO Not yet implemented")
            }

            override fun <B> ffoldLeft(fa: Kind<FList<T>, T>, z: B, f: (B, T) -> B): B {
                tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B =
                    when (xs) {
                        is FLNil -> z
                        is FLCons -> go(xs.tail, f(z, xs.head), f)
                        is FLNel -> go(xs.fnel.tail, f(z, xs.fnel.head), f)
                    }
                return go(k2c(fa).fix(), z, f)
            }

            override fun <B> ffoldRight(fa: Kind<FList<T>, T>, z: B, f: (T, B) -> B): B {
                fun go(xs: FList<T>, z: B, f: (T, B) -> B): B = when (xs) {
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                    is FLNel -> f(xs.fnel.head, go(xs.fnel.tail, z, f))
                }
                return go(k2c(fa).fix(), z, f)
            }

            override fun <B> ffoldRightSafe(fa: Kind<FList<T>, T>, z: B, f: (T, B) -> B): B {
                data class args(val xs: FList<T>, val z: B, val f: (T, B) -> B)

                fun goDeep(a: args): B = DeepRecursiveFunction<args, B> { a ->
                    when (a.xs) {
                        is FLNil -> z
                        is FLCons -> a.f(a.xs.head, callRecursive(args(a.xs.tail, z, f)))
                        is FLNel -> a.f(a.xs.fnel.head, callRecursive(args(a.xs.fnel.tail, z, f)))
                    }
                }(a)
                return goDeep(args(k2c(fa).fix(), z, f))
            }

            override fun fhead(fa: Kind<FList<T>, T>): T? = when (val l = k2c(fa).fix()) {
                is FLNil -> null
                is FLCons -> l.head
                is FLNel -> l.fnel.head
            }

            override fun ftail(fa: Kind<FList<T>, T>): KFList<T> = when (val l = k2c(fa).fix()) {
                is FLNil -> k2c(fa)
                is FLCons -> KFList.of(l.tail)
                is FLNel -> KFList.of(l.fnel.tail)
            }

            override fun freverse(fa: Kind<FList<T>, T>): KFList<T> =
                KFList.of(ffoldLeft(fa, KFList.of<T>().fix()) { b, a -> FLCons(a, b) })

            override fun finit(fa: Kind<FList<T>, T>): KFList<T> {
                fun go(xs: FList<T>, z: FList<T>, f: (T, FList<T>) -> FList<T>): FList<T> = when (xs) {
                    is FLCons if xs.tail is FLNil -> z
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                    is FLNel -> f(xs.fnel.head, go(xs.fnel.tail, z, f))
                }
                return if (k2c(fa).empty) k2c(fa)
                else KFList.of(go(k2c(fa).fix(), FLNil, { item, l -> FLCons(item, l) }))
            }

            override fun finitSafe(fa: Kind<FList<T>, T>): KFList<T> {
                data class args(val xs: FList<T>, val z: FList<T>, val f: (T, FList<T>) -> FList<T>)

                fun goDeep(a: args) = DeepRecursiveFunction<args, FList<T>> { (xs, z, f) ->
                    when (xs) {
                        is FLCons if xs.tail is FLNil -> z
                        is FLNil -> z
                        is FLCons -> f(xs.head, callRecursive(args(xs.tail, z, f)))
                        is FLNel -> f(xs.fnel.head, callRecursive(args(xs.fnel.tail, z, f)))
                    }
                }(a)
                return if (k2c(fa).empty) k2c(fa)
                else KFList.of(goDeep(args(k2c(fa).fix(), FLNil, { item, l -> FLCons(item, l) })))
            }

            override fun flast(fa: Kind<FList<T>, T>): T? {
                tailrec fun go(xs: FList<T>): T? = when (xs) {
                    is FLNel -> TODO("$FIX_TODO impossible code path")
                    is FLCons if xs.tail is FLNil -> xs.head
                    is FLNil -> null
                    is FLCons -> go(xs.tail)
                }
                return when (val fl = k2c(fa).fix()) {
                    is FLNel -> go(fl.fnel)
                    else -> go(fl)
                }
            }
        }

        private fun <F : FList<*>> buildAtt() = object : KFLAtt<F>, FLibLog {
            @Suppress("UNCHECKED_CAST")
            override fun <A : Any> lift(a: A): Kind<F, A>? = KFList.of(FLCons(a, FLNil)) as Kind<F, A>

        }
    }
}
