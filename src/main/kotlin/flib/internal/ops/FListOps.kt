package org.xrpn.flib.internal.ops

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.attribute.Monad
import org.xrpn.flib.impl.FListKind
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.shredset.FSequence

internal interface FSequenceKind<T> : Kind<FListKind<T>, T>, FSequence<FListKind<T>, T> where T: Any

internal class FListOps<T: Any> private constructor (private val i: FSequenceKind<T> = buildFListOps<T>()) {
    @Volatile
    private var instance: FSequenceKind<T>? = null
    fun get(): FSequenceKind<T> = instance ?: synchronized(this) {
        instance ?: i.also { instance = it }
    }
    companion object {
        fun <T:Any> build() = FListOps<T>()
        private fun <T: Any> buildFListOps() = object : FSequenceKind<T>, Monad<FList<T>>, FLibLog {
            override fun fsize(fa: Kind<FListKind<T>, T>): Int = ffoldLeft(fa,0) { acc, _ -> acc + 1}
            override fun fempty(fa: Kind<FListKind<T>, T>): Boolean = fpick(fa) == null
            override fun fequal(lhs: Kind<FListKind<T>, T>, rhs: Kind<FListKind<T>, T>): Boolean =
                rhs.fix().equals(lhs.fix())
            override fun fpick(fa: Kind<FListKind<T>, T>): T? = fhead(fa)
            override fun fcount(fa: Kind<FListKind<T>, T>, isMatch: (T) -> Boolean): Int {
                val f: (acc: Int, T) -> Int = { acc, item -> if (isMatch(item)) acc + 1 else acc }
                return ffoldLeft(fa,0,f)
            }
            override fun fprepend(fa: Kind<FListKind<T>, T>, item: T): Kind<FListKind<T>, T> =
                FListKind.of(FLCons(item, fa.fix().list))
            override fun fappend(fa: Kind<FListKind<T>, T>, item: T): Kind<FListKind<T>, T> {
                TODO("Not yet implemented")
            }
            override fun <B> ffoldLeft(fa: Kind<FListKind<T>, T>, z: B, f: (B, T) -> B): B {
                tailrec fun go(xs: FList<T>, z: B, f: (B, T) -> B): B =
                    when (xs) {
                        is FLNil -> z
                        is FLCons -> go(xs.tail, f(z, xs.head), f)
                    }
                return go(fa.fix().list, z, f)
            }
            override fun <B> ffoldRight(fa: Kind<FListKind<T>, T>, z: B, f: (T, B) -> B): B {
                fun go(xs: FList<T>, z: B, f: (T, B) -> B): B = when (xs) {
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                }
                return go(fa.fix().list, z, f)
            }
            override fun <B> ffoldRightSafe(fa: Kind<FListKind<T>, T>, z: B, f: (T, B) -> B): B {
                data class args(val xs: FList<T>, val z: B, val f: (T, B) -> B)
                fun goDeep(a: args): B = DeepRecursiveFunction<args, B> { a -> when (a.xs) {
                    is FLNil -> z
                    is FLCons -> a.f(a.xs.head, callRecursive(args(a.xs.tail, z, f)))
                }}(a)
                return goDeep(args(fa.fix().list, z, f))
            }
            override fun fhead(fa: Kind<FListKind<T>, T>): T? = when (val l = fa.fix().list) {
                is FLNil -> null
                is FLCons -> l.head
            }
            override fun ftail(fa: Kind<FListKind<T>, T>): Kind<FListKind<T>, T> = when (val l = fa.fix().list) {
                is FLNil -> fa
                is FLCons -> FListKind.of(l.tail)
            }
            override fun freverse(fa: Kind<FListKind<T>, T>): Kind<FListKind<T>, T> =
                FListKind.of(ffoldLeft(fa, FListKind.empty<T>().list) { b, a -> FLCons(a, b) })
            override fun finit(fa: Kind<FListKind<T>, T>): Kind<FListKind<T>, T> {
                fun go(xs: FList<T>, z: FList<T>, f: (T, FList<T>) -> FList<T>): FList<T> = when (xs) {
                    is FLCons if xs.tail is FLNil -> z
                    is FLNil -> z
                    is FLCons -> f(xs.head, go(xs.tail, z, f))
                }
                return if (fa.fix().list is FLNil) fa.fix() else FListKind.of(go(fa.fix().list, FLNil) { item, l -> FLCons(item, l)})
            }
            override fun finitSafe(fa: Kind<FListKind<T>, T>): Kind<FListKind<T>, T> {
                data class args(val xs: FList<T>, val z: FList<T>, val f: (T, FList<T>) -> FList<T>)
                fun goDeep(a: args) = DeepRecursiveFunction<args,FList<T>> { (xs,z,f) -> when (xs) {
                    is FLCons if xs.tail is FLNil -> z
                    is FLNil -> z
                    is FLCons -> f(xs.head, callRecursive(args(xs.tail, z, f)))
                }}(a)
                return if (fa.fix().list is FLNil) fa
                else FListKind.of(goDeep(args(fa.fix().list,FLNil,{ item, l -> FLCons(item, l)})))
            }
            override fun flast(fa: Kind<FListKind<T>, T>): T? {
                tailrec fun go(xs: FList<T>): T? = when (xs) {
                    is FLCons if xs.tail is FLNil -> xs.head
                    is FLNil -> null
                    is FLCons -> go(xs.tail)
                }
                return go(fa.fix().list)
            }

            override fun <A> lift(a: A): Kind<FList<T>, A> {
                TODO("Not yet implemented")
            }

            override fun <A, B> flatMap(
                fa: Kind<FList<T>, A>,
                f: (A) -> Kind<FList<T>, B>
            ): Kind<FList<T>, B> {
                TODO("Not yet implemented")
            }
        }
    }
}
