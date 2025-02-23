package org.xrpn.flib.internal.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLKDecorator
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.decorator.KFList
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.shredset.FSequence
import org.xrpn.flib.pattern.Kind

internal interface FLKShreds<T: Any>: FSequence<FList<*>, T> {
    companion object {

        private fun <T: Any> FLK<T>.of(item: T): FLK<T> = when (this) {
            // is FNel<T> -> FNel.of(FLCons(item,this.fix()), this.kind)
            is FLCons<T> -> FLCons(item,this)
            is FLNil -> FLCons(item,FLNil())
            is FLKDecorator<T> -> KFList.of(FLCons(item,this.fix()))
        }

//        private fun <T: Any> FLK<T>.of(fl: FList<T>): FLK<T> = when (this) {
//            is FLNil -> fl
////            is FNel<T> -> when (fl) {
////                is FLNil -> this
////                is FLCons<T> -> TODO("not implemented yet [append bulk]")
////            }
//            is FLCons<T> -> when (fl) {
//                is FLNil -> this
//                is FLCons<T> -> TODO("not implemented yet [append bulk]")
//            }
//            is FLKDecorator<T> ->  when (fl) {
//                is FLNil -> this
//                is FLCons<T> -> TODO("not implemented yet [append bulk]")
//            }
//        }

        private fun <T: Any> FList<T>.like(flk: FLK<*>): FLK<T> = when (this) {
            is FLCons -> when (flk) {
                is FLCons -> this
                // is FNel -> FNel.of(this, null)
                is FLKDecorator -> KFList.of(this)
                is FLNil -> this
            }
            is FLNil -> when (flk) {
                is FLCons -> this
                // is FNel -> TODO("$FIX_TODO impossible code path")
                is FLKDecorator -> KFList.of()
                is FLNil -> this
            }
        }


        fun <T : Any> build() : FLKShreds<T> = object : FLKShreds<T>, FLibLog {
            override fun fsize(fa: Kind<FList<*>,T>): Int = ffoldLeft(fa, 0) { acc, _ -> acc + 1 }
            override fun fempty(fa: Kind<FList<*>,T>): Boolean = fpick(fa) == null
            override fun fequal(lhs: Kind<FList<*>,T>, rhs: Kind<FList<*>,T>): Boolean = lhs.equals(rhs)
            override fun fpick(fa: Kind<FList<*>,T>): T? = fhead(fa)
            override fun fcount(fa: Kind<FList<*>,T>, isMatch: (T) -> Boolean): Int {
                fa as FLK<T>
                val f: (acc: Int, T) -> Int = { acc, item -> if (isMatch(item)) acc + 1 else acc }
                return ffoldLeft(fa, 0, f)
            }
            override fun fprepend(fa: Kind<FList<*>,T>, item: T): Kind<FList<*>,T> {
                fa as FLK<T>
                return when (fa.fix()) {
                    is FLNil -> fa.of(item)
                    is FLCons -> fa.of(item)
                }
            }

            override fun fappend(fa: Kind<FList<*>,T>, item: T) =
                TODO("$FIX_TODO Not yet implemented")

            override fun <B> ffoldLeft(fa: Kind<FList<*>,T>, z: B, f: (B, T) -> B): B {
                fa as FLK<T>
                tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B = when (xs) {
                    is FLNil -> z
                    is FLCons -> go(xs.tail, f(z, xs.head), f)
                }
                return go(fa.fix(), z, f)
            }

            override fun <B> ffoldRight(fa: Kind<FList<*>,T>, z: B, f: (T, B) -> B): B {
                fa as FLK<T>
                fun go(xs: FList<T>, z: B, f: (T, B) -> B): B = when (xs) {
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                }
                return go(fa.fix(), z, f)
            }

            override fun <B> ffoldRightSafe(fa: Kind<FList<*>,T>, z: B, f: (T, B) -> B): B {
                fa as FLK<T>
                data class args(val xs: FList<T>, val z: B, val f: (T, B) -> B)
                fun goDeep(a: args): B = DeepRecursiveFunction<args, B> { a ->
                    when (a.xs) {
                        is FLNil -> z
                        is FLCons -> a.f(a.xs.head, callRecursive(args(a.xs.tail, z, f)))
                    }
                }(a)
                return goDeep(args(fa.fix(), z, f))
            }

            override fun fhead(fa: Kind<FList<*>,T>): T? = when (val l = (fa as FLK<T>).fix()) {
                is FLNil -> null
                is FLCons -> l.head
            }

            override fun ftail(fa: Kind<FList<*>,T>): FLK<T> {
                fa as FLK<T>
                return when (val fl = fa.fix()) {
                    is FLNil -> fa
                    is FLCons -> fl.tail.like(fa)
                }
            }

            override fun freverse(fa: Kind<FList<*>,T>): FLK<T> =
                ffoldLeft(fa, FLNil<T>()) { b: FList<T>, a -> FLCons(a, b) }.like(fa as FLK<T>)

            override fun finit(fa: Kind<FList<*>,T>): FLK<T> {
                fa as FLK<T>
                fun go(xs: FList<T>, z: FList<T>, f: (T, FList<T>) -> FList<T>): FList<T> = when (xs) {
                    is FLCons if xs.tail is FLNil -> z
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                    // is FNel -> f(xs.nel.head, go(xs.nel.tail, z, f))
                }
                return go(fa.fix(), FLNil<T>(), { item, l -> FLCons(item, l) }).like(fa)
            }

            override fun finitSafe(fa: Kind<FList<*>,T>): FLK<T> {
                fa as FLK<T>
                data class args(val xs: FList<T>, val z: FList<T>, val f: (T, FList<T>) -> FList<T>)
                fun goDeep(a: args) = DeepRecursiveFunction<args, FList<T>> { (xs, z, f) ->
                    when (xs) {
                        is FLCons if xs.tail is FLNil -> z
                        is FLNil -> z
                        is FLCons -> f(xs.head, callRecursive(args(xs.tail, z, f)))
                        // is FNel -> f(xs.nel.head, callRecursive(args(xs.nel.tail, z, f)))
                    }
                }(a)
                return goDeep(args(fa.fix(), FLNil<T>(), { item, l -> FLCons(item, l) })).like(fa)
            }

            override fun flast(fa: Kind<FList<*>,T>): T? {
                fa as FLK<T>
                tailrec fun go(xs: FList<T>): T? = when (xs) {
                    is FLCons if xs.tail is FLNil -> xs.head
                    is FLNil -> null
                    is FLCons -> go(xs.tail)
                }
                return go(fa.fix())
            }
        }
    }
}