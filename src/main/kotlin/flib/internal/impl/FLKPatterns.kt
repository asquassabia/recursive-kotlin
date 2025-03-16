package org.xrpn.flib.internal.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.api.foldLeft
import org.xrpn.flib.api.reverse
import org.xrpn.flib.internal.tool.like
import org.xrpn.flib.rs.SFList
import org.xrpn.flib.pattern.KFunctor
import org.xrpn.flib.pattern.KMonad
import org.xrpn.flib.pattern.Kind

internal interface FLKPatterns: KMonad<FList<*>>, KFunctor<FList<*>> {
    companion object {

        private fun <T: Any> Kind<FList<*>, *>.empty(): FLK<T> = when (this) {
//            is FList -> FLNil<T>()
            is SFList -> SFList.of()
            else -> TODO("$FIX_TODO impossible code path")
        }

        private fun <T: Any> FLK<T>.of(item: T): FLK<T> = when (this) {
            // is FNel<T> -> FNel.of(FLCons(item,this.fix()), this.kind)
//            is FLCons<T> -> FLCons(item,this)
//            is FLNil -> @Suppress("UNCHECKED_CAST") FLCons(item,FLNil<T>())
            is FLKApi<*> -> SFList.of(FLCons(item,this.fix()))
        }

        internal fun build(): FLKPatterns = object : FLKPatterns {

            override fun <TL : Any> lift(
                fa: Kind<FList<*>, *>,
                a: TL
            ): FLK<TL> = fa.empty<TL>().of(a)

            /* 2.850 sec for map { i -> -i } over 1E6 records
            override fun <A: Any, B: Any> map(fa: Kind<FList<*>, A>, f: (A) -> B): FLKApi<B> =
                @Suppress("UNCHECKED_CAST")
                (super.map(fa,f).fix() as FList<B>).like(fa as FLK<A>)

            override fun <A: Any, B: Any> map(fa: Kind<FList<*>, A>, f: (A) -> B): FLKApi<B> {
                fa as FLK<A>
                val safe = fa.size <= SAFE_RECURSION_SIZE.get()
                return if (safe) {
                    fun go(xs: FList<A>, f: (A) -> B): FList<B> = when (xs) {
                        is FLCons<A> -> FLCons(f(xs.head), if (xs.tail is FLNil) FLNil<B>() else go(xs.tail, f))
                        is FLNil -> FLNil<B>()
                    }
                    go(fa.fix(), f)
                } else {
                    data class args(val xs: FList<A>, val f: (A) -> B)
                    fun goDeep(a: args) : FList<B> = DeepRecursiveFunction<args, FList<B>> { (xs, f) -> when (xs) {
                        is FLCons -> FLCons(f(xs.head), if (xs.tail is FLNil) FLNil<B>() else callRecursive(args(xs.tail,f)))
                        is FLNil -> FLNil<B>()
                    }
                    }(a)
                    // 0.640 sec for map { i -> -i } over 1E6 records
                    goDeep(args(fa.fix(), f))
                }.like(fa)
            }
            */

            override fun <TA: Any, TB: Any> map(fa: Kind<FList<*>, TA>, f: (TA) -> TB): FLKApi<TB> {
                fa as FLK<TA>
                tailrec fun go(xs: FList<TA>, f: (TA) -> TB, out: FList<TB>): FList<TB> = when (xs) {
                    is FLNil -> out.reverse()
                    is FLCons<TA> -> go (xs.tail, f, FLCons(f(xs.head), out))
                }
                // 0.150 sec for map { i -> -i } over 1E6 records
                return go(fa.fix(), f, FLNil<TB>()).like(fa)
            }

            override fun <TA : Any, TB : Any> flatMap(
                fa: Kind<FList<*>, TA>,
                f: (TA) -> Kind<FList<*>, TB>
            ): FLKApi<TB> {
                fa as FLK<TA>
                tailrec fun go(xs: FList<TA>, out: FList<TB>): FList<TB> = when (xs) {
                    is FLNil -> out.reverse()
                    is FLCons -> {
                        val fl: FList<TB> = (f(xs.head) as FLK<TB>).fix()
                        val fx = { fl: FList<TB>, tb: TB -> FLCons(tb, fl) }
                        go(xs.tail, fl.foldLeft(out,fx))
                    }
                }
                return when (fa.fix()) {
                    is FLNil<TA> -> FLNil<TB>()
                    is FLCons<TA> -> {
                        go(fa.fix(), FLNil<TB>())
                    }
                }.like(fa)
            }
        }
    }
}