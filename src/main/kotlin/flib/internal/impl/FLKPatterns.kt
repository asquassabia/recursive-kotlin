package org.xrpn.flib.internal.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.FLK
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FListNonEmpty
import org.xrpn.flib.adt.FNel
import org.xrpn.flib.api.foldLeft
import org.xrpn.flib.decorator.KFList
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.pattern.KMonad
import org.xrpn.flib.pattern.Kind

internal interface FLKPatterns: KMonad<FList<*>> {
    companion object {

        private fun <T: Any> FLK<*>.of(): FLK<T> = when (this) {
            is FList -> @Suppress("UNCHECKED_CAST") FLNil as FList<T>
            is KFList -> KFList.of()
            else -> TODO("$FIX_TODO impossible code path")
        }
        private fun <T: Any> FLK<T>.of(item: T): FLK<T> = when (this) {
            is FNel<T> -> FNel.of(FLCons(item,this.fix()), this.kind)
            is FLCons<T> -> FLCons(item,this)
            is FLNil -> @Suppress("UNCHECKED_CAST") FLCons(item,FLNil as FList<T>)
            is KFList<T> -> KFList.of(FLCons(item,this.fix()))
            else -> TODO("$FIX_TODO impossible code path")
        }
        private fun <T: Any> FLNil.like(flk: FLK<*>): FLK<T>? = @Suppress("UNCHECKED_CAST") (when (flk) {
            is FListNonEmpty -> null
            is FList -> this
            is KFList -> KFList.of()
            else -> TODO("$FIX_TODO impossible code path")
        } as FLK<T>?)

        private fun <T: Any> FListNonEmpty<T>.like(flk: FLK<*>, kind: FLK<T>? = null): FLK<T> = when (this) {
            is FLCons<T> -> when (flk) {
                is FListNonEmpty -> FNel.of(this, kind)
                is FList -> this
                is KFList -> KFList.of(this)
                else -> TODO("$FIX_TODO impossible code path")
            }
            is FNel<T> ->  when (flk) {
                is FListNonEmpty -> this
                is FList -> this.fix()
                is KFList -> KFList.of(this.fix())
                else -> TODO("$FIX_TODO impossible code path")
            }
        }
        private fun <T: Any> FLK<T>.append(fl: FList<T>): FLK<T> = when (this) {
            is FLNil -> fl
            is FNel<T> -> when (fl) {
                is FLNil -> this
                is FNel<T> -> TODO("not implemented yet [append bulk]")
                is FLCons<T> -> TODO("not implemented yet [append bulk]")
            }
            is FLCons<T> -> when (fl) {
                is FLNil -> this
                is FNel<T> -> TODO("not implemented yet [append bulk]")
                is FLCons<T> -> TODO("not implemented yet [append bulk]")
            }
            is KFList<T> ->  when (fl) {
                is FLNil -> this
                is FNel<T> if this.empty -> KFList.of(fl)
                is FLCons<T> if this.empty -> KFList.of(fl)
                else -> TODO("not implemented yet [append bulk]")
            }
            else -> TODO("$FIX_TODO impossible code path")
        }

        private fun build(): FLKPatterns = object : FLKPatterns, FLibLog {

            override fun <T1 : Any> lift(fa: FLK<*>, a: T1): FLK<T1> =
                @Suppress("UNCHECKED_CAST")
                fa.of<T1>().append(FLCons(a,FLNil as FList<T1>))

            override fun <T2 : Any, T3 : Any> flatMap(
                fa: FLK<T2>,
                f: (T2) -> FLK<T3>
            ): FLK<T3> {
                val fx = { list: FList<T3>, element: T3 -> FLCons(element, list) }
                tailrec fun go(xs: FList<T2>, out: FList<T3>): FList<T3> = when (xs) {
                    is FLNil -> out
                    is FLCons -> go(xs, f(xs.head).fix().foldLeft(out, fx) )
                    is FNel -> go(xs.fix(), f(xs.fix().head).fix().foldLeft(out, fx) )
                }
                @Suppress("UNCHECKED_CAST")
                (return when (val res: FList<T3> = go(fa.fix() as FList<T2>, FLNil as FList<T3>)) {
                    is FListNonEmpty<*> -> res.like(fa)
                    is FLNil -> fa.of<T3>()
                } as FLK<T3> )
            }
        }
    }
}