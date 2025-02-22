package org.xrpn.flib.internal.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.FLK
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FNel
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.shredset.FSequence
import org.xrpn.flib.pattern.Kind

internal interface FLKShreds<T: Any>: FSequence<FList<*>, T> {
    companion object {
        fun <T : Any> build() : FLKShreds<T> = object : FLKShreds<T>, FLibLog {
            override fun fsize(fa: FLK<T>): Int = ffoldLeft(fa, 0) { acc, _ -> acc + 1 }
            override fun fempty(fa: FLK<T>): Boolean = fpick(fa) == null
            override fun fequal(lhs: FLK<T>, rhs: FLK<T>): Boolean = TODO()
//                k2c(rhs).equals(lhs.fix())

            override fun fpick(fa: FLK<T>): T? = fhead(fa)
            override fun fcount(fa: FLK<T>, isMatch: (T) -> Boolean): Int {
                val f: (acc: Int, T) -> Int = { acc, item -> if (isMatch(item)) acc + 1 else acc }
                return ffoldLeft(fa, 0, f)
            }

            override fun fprepend(fa: FLK<T>, item: T) = TODO()
//            : KFList<T> = k2c(fa).let {
//                when (val fl = it.fix()) {
//                    is FLNil -> KFList.of(FLCons(item, FLNil))
//                    is FLCons -> KFList.of(FLCons(item, fl))
//                    is FNel -> KFList.of(FLCons(item, fl.nel))
//                }
//            }

            override fun fappend(fa: FLK<T>, item: T) =
                TODO("$FIX_TODO Not yet implemented")

            override fun <B> ffoldLeft(fa: FLK<T>, z: B, f: (B, T) -> B): B {
                tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B = when (xs) {
                    is FLNil -> z
                    is FLCons -> go(xs.tail, f(z, xs.head), f)
                    is FNel -> go(xs.fix().tail, f(z, xs.fix().head), f)
                }
                @Suppress("UNCHECKED_CAST")
                return go(fa.fix() as FList<T>, z, f)
            }

            override fun <B> ffoldRight(fa: FLK<T>, z: B, f: (T, B) -> B): B {
                fun go(xs: FList<T>, z: B, f: (T, B) -> B): B = when (xs) {
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                    is FNel -> f(xs.fix().head, go(xs.fix().tail, z, f))
                }
                @Suppress("UNCHECKED_CAST")
                return go(fa.fix() as FList<T>, z, f)
            }

            override fun <B> ffoldRightSafe(fa: FLK<T>, z: B, f: (T, B) -> B): B {
                data class args(val xs: FList<T>, val z: B, val f: (T, B) -> B)

                fun goDeep(a: args): B = DeepRecursiveFunction<args, B> { a ->
                    when (a.xs) {
                        is FLNil -> z
                        is FLCons -> a.f(a.xs.head, callRecursive(args(a.xs.tail, z, f)))
                        is FNel -> a.f(a.xs.nel.head, callRecursive(args(a.xs.nel.tail, z, f)))
                    }
                }(a)
                @Suppress("UNCHECKED_CAST")
                return goDeep(args(fa.fix() as FList<T>, z, f))
            }

            override fun fhead(fa: FLK<T>): T? = when (val l = fa.fix() as FList<T>) {
                is FLNil -> null
                is FLCons -> l.head
                is FNel -> l.fix().head
            }

            override fun ftail(fa: FLK<T>) = TODO() //: KFList<T> = when (val l = k2c(fa).fix()) {
//                is FLNil -> k2c(fa)
//                is FLCons -> KFList.of(l.tail)
//                is FNel -> KFList.of(l.nel.tail)
//            }

            override fun freverse(fa: FLK<T>) = TODO() //: KFList<T> =
//                KFList.of(ffoldLeft(fa, KFList.of<T>().fix()) { b, a -> FLCons(a, b) })

            override fun finit(fa: FLK<T>) = TODO() //: KFList<T> {
//                fun go(xs: FList<T>, z: FList<T>, f: (T, FList<T>) -> FList<T>): FList<T> = when (xs) {
//                    is FLCons if xs.tail is FLNil -> z
//                    is FLNil -> z
//                    is FLCons -> f(xs.head, go(xs.tail, z, f))
//                    is FNel -> f(xs.nel.head, go(xs.nel.tail, z, f))
//                }
//                return if (k2c(fa).empty) k2c(fa)
//                else KFList.of(go(k2c(fa).fix(), FLNil, { item, l -> FLCons(item, l) }))
//            }

            override fun finitSafe(fa: FLK<T>) = TODO() //: KFList<T> {
//                data class args(val xs: FList<T>, val z: FList<T>, val f: (T, FList<T>) -> FList<T>)
//
//                fun goDeep(a: args) = DeepRecursiveFunction<args, FList<T>> { (xs, z, f) ->
//                    when (xs) {
//                        is FLCons if xs.tail is FLNil -> z
//                        is FLNil -> z
//                        is FLCons -> f(xs.head, callRecursive(args(xs.tail, z, f)))
//                        is FNel -> f(xs.nel.head, callRecursive(args(xs.nel.tail, z, f)))
//                    }
//                }(a)
//                return if (k2c(fa).empty) k2c(fa)
//                else KFList.of(goDeep(args(k2c(fa).fix(), FLNil, { item, l -> FLCons(item, l) })))
//            }

            override fun flast(fa: FLK<T>): T? {
                tailrec fun go(xs: FList<T>): T? = when (xs) {
                    is FNel -> TODO("$FIX_TODO impossible code path")
                    is FLCons if xs.tail is FLNil -> xs.head
                    is FLNil -> null
                    is FLCons -> go(xs.tail)
                }
                TODO()
//                return when (val fl = k2c(fa).fix()) {
//                    is FNel -> go(fl.nel)
//                    else -> go(fl)
//                }
            }
        }
    }
}