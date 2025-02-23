package org.xrpn.flib.internal.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLKDecorator
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.decorator.KFList
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.pattern.KMonad
import org.xrpn.flib.pattern.Kind

internal interface FLKPatterns<A>: KMonad<FList<*>> {
    companion object {

        private fun <T: Any> Kind<FList<*>, *>.empty(): FLK<T> = when (this) {
            is FList -> FLNil<T>()
            is KFList -> KFList.of()
            else -> TODO("$FIX_TODO impossible code path")
        }

        private fun <T: Any> FLK<T>.of(item: T): FLK<T> = when (this) {
            // is FNel<T> -> FNel.of(FLCons(item,this.fix()), this.kind)
            is FLCons<T> -> FLCons(item,this)
            is FLNil -> @Suppress("UNCHECKED_CAST") FLCons(item,FLNil<T>())
            is FLKDecorator<T> -> KFList.of(FLCons(item,this.fix()))
        }

//        private fun <T: Any> FLK<T>.of(item: T): FLK<T> = when (this) {
//            is FNel<T> -> FNel.of(FLCons(item,this.fix()), this.kind)
//            is FLCons<T> -> FLCons(item,this)
//            is FLNil -> FLCons(item,FLNil())
//            is FLKDecorator<T> -> KFList.of(FLCons(item,this.fix()))
//        }
//        private fun <T: Any> FLNil<T>.like(flk: FLK<*>): FLK<T>? = when (flk) {
//            is FListNonEmpty -> null
//            is FList -> this
//            is KFList -> KFList.of()
//            else -> TODO("$FIX_TODO impossible code path")
//        }
//
//        private fun <T: Any> FListNonEmpty<T>.like(flk: FLK<*>, kind: FLK<T>? = null): FLK<T> = when (this) {
//            is FLCons<T> -> when (flk) {
//                is FListNonEmpty -> FNel.of(this, kind)
//                is FList -> this
//                is KFList -> KFList.of(this)
//                else -> TODO("$FIX_TODO impossible code path")
//            }
//            is FNel<T> ->  when (flk) {
//                is FListNonEmpty -> this
//                is FList -> this.fix()
//                is KFList -> KFList.of(this.fix())
//                else -> TODO("$FIX_TODO impossible code path")
//            }
//        }


        private fun <A> build(): FLKPatterns<A> = object : FLKPatterns<A>, FLibLog {

            override fun <TL : Any> lift(
                fa: Kind<FList<*>, *>,
                a: TL
            ): FLK<TL> = fa.empty<TL>().of(a)

            override fun <TA : Any, TB : Any> flatMap(
                fa: Kind<FList<*>, TA>,
                f: (TA) -> Kind<FList<*>, TB>
            ): FLK<TB> {
                fa as FLK<TA>
                return when (fa.fix()) {
                    is FLNil<TA> -> FLNil<TB>()
                    is FLCons<TA> -> {
                        fun <T: Any, B> FList<T>.foldLeft(z: B, f: (B, T) -> B): B {
                            tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B = when (xs) {
                                is FLNil -> z
                                is FLCons -> go(xs.tail, f(z, xs.head), f)
                            }
                            return go(this, z, f)
                        }
                        tailrec fun go(xs: FList<TA>, out: FList<TB>): FList<TB> = when (xs) {
                            is FLNil -> out
                            is FLCons -> {
                                val fl: FList<TB> = (f(xs.head) as FLK<TB>).fix()
                                go(xs.tail, fl.foldLeft(out){ flTB, tb -> FLCons(tb, flTB) })
                            }
                        }
                        go(fa.fix(), FLNil<TB>())
                    }
                } as FLK<TB>
            }
        }
    }
}