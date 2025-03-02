package org.xrpn.flib.internal.impl

import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FListApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FListSafe
import org.xrpn.flib.api.append
import org.xrpn.flib.api.appendSafe
import org.xrpn.flib.api.count
import org.xrpn.flib.api.empty
import org.xrpn.flib.api.foldLeft
import org.xrpn.flib.api.foldRight
import org.xrpn.flib.api.foldRightSafe
import org.xrpn.flib.api.head
import org.xrpn.flib.api.init
import org.xrpn.flib.api.initSafe
import org.xrpn.flib.api.last
import org.xrpn.flib.api.ne
import org.xrpn.flib.api.pick
import org.xrpn.flib.api.prepend
import org.xrpn.flib.api.reverse
import org.xrpn.flib.api.size
import org.xrpn.flib.api.tail
import org.xrpn.flib.decorator.SFList
import org.xrpn.flib.internal.shredset.FSequence
import org.xrpn.flib.pattern.Kind

internal interface FLKShreds<T: Any>: FSequence<FList<*>, T> {
    companion object {

//        private fun <T: Any> FLK<T>.of(item: T): FLK<T> = when (this) {
//            is FLCons<T> -> FLCons(item,this)
//            is FLNil -> FLCons(item,FLNil())
//            is FListApi<*> -> SFList.of(FLCons(item,this.fix()))
//            is FListSafe<*> -> SFList.of(FLCons(item,this.fix()))
//        }

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

        private fun <T: Any> FList<T>.like(flk: FLK<*>): FLKApi<T> = when (this) {
            is FLCons -> when (flk) {
                is FListApi<*>, is FListSafe<*> -> SFList.of(this)
            }
            is FLNil -> when (flk) {
                is FListApi<*>, is FListSafe<*> -> SFList.of()
            }
        }

        fun <T : Any> buildShreds() : FLKShreds<T> = object : FLKShreds<T> {
            override fun fsize(fa: Kind<FList<*>,T>): Int = (fa as FLK<T>).fix().size()
            override fun fempty(fa: Kind<FList<*>,T>): Boolean = (fa as FLK<T>).fix().empty()
            override fun fne(fa: Kind<FList<*>,T>): Boolean = (fa as FLK<T>).fix().ne()
            override fun fequal(lhs: Kind<FList<*>,T>, rhs: Kind<FList<*>,T>): Boolean = lhs.equals(rhs)
            override fun fpick(fa: Kind<FList<*>,T>): T? = (fa as FLK<T>).fix().pick()
            override fun fcount(fa: Kind<FList<*>,T>, isMatch: (T) -> Boolean): Int = (fa as FLK<T>).fix().count(isMatch)
            override fun fprepend(fa: Kind<FList<*>,T>, item: T): FLKApi<T> = (fa as FLK<T>).fix().prepend(item).like(fa)
            override fun fappend(fa: Kind<FList<*>,T>, item: T): FLKApi<T> = (fa as FLK<T>).fix().append(item).like(fa)
            override fun fappendSafe(fa: Kind<FList<*>,T>, item: T): FLKApi<T> = (fa as FLK<T>).fix().appendSafe(item).like(fa)
            override fun <B> ffoldLeft(fa: Kind<FList<*>,T>, z: B, f: (B, T) -> B): B = (fa as FLK<T>).fix().foldLeft(z,f)
            override fun <B> ffoldRight(fa: Kind<FList<*>,T>, z: B, f: (T, B) -> B): B = (fa as FLK<T>).fix().foldRight(z,f)
            override fun <B> ffoldRightSafe(fa: Kind<FList<*>,T>, z: B, f: (T, B) -> B): B = (fa as FLK<T>).fix().foldRightSafe(z,f)
            override fun fhead(fa: Kind<FList<*>,T>): T? = (fa as FLK<T>).fix().head()
            override fun ftail(fa: Kind<FList<*>,T>): FLKApi<T> = (fa as FLK<T>).fix().tail().like(fa)
            override fun freverse(fa: Kind<FList<*>,T>): FLKApi<T> = (fa as FLK<T>).fix().reverse().like(fa)
            override fun finit(fa: Kind<FList<*>,T>): FLKApi<T> = (fa as FLK<T>).fix().init().like(fa)
            override fun finitSafe(fa: Kind<FList<*>,T>): FLKApi<T> = (fa as FLK<T>).fix().initSafe().like(fa)
            override fun flast(fa: Kind<FList<*>,T>): T? = (fa as FLK<T>).fix().last()
        }

        fun <T : Any> buildApi(fa: Kind<FList<*>,T>) : FListApi<T> = object : FListApi<T> {
            val ops = buildShreds<T>()
            override fun append(item: T): FLKApi<T> = ops.fappend(fa, item) as FLKApi<T>
            override fun count(isMatch: (T) -> Boolean): Int = ops.fcount(fa, isMatch)
            override fun <B> fold(z: B, f: (B, T) -> B): B = ops.ffoldLeft(fa, z, f)
            override fun <B> foldLeft(z: B, f: (B, T) -> B): B  = ops.ffoldLeft(fa, z, f)
            override fun <B> foldRight(z: B, f: (T, B) -> B): B  = ops.ffoldRight(fa, z, f)
            override fun head(): T?  = ops.fhead(fa)
            override fun init(): FLKApi<T> = ops.finit(fa) as FLKApi<T>
            override fun last(): T? = ops.flast(fa)
            override fun pick(): T? = ops.fpick(fa)
            override fun prepend(item: T): FLKApi<T> = ops.fprepend(fa, item) as FLKApi<T>
            override fun reverse(): FLKApi<T> = ops.freverse(fa) as FLKApi<T>
            override fun tail(): FLKApi<T> = ops.ftail(fa) as FLKApi<T>
            override val size: Int by lazy { ops.fsize(fa) }
            override val empty: Boolean = ops.fempty(fa)
            override val ne: Boolean = ops.fne(fa)
            override val show by lazy { (ops.ffoldLeft(fa,"${fa::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }
            override val hash by lazy { (ops.ffoldLeft(fa,1549L) { acc: Long, h: T -> 31L * acc + h.hashCode() }).let{ (it xor (it ushr 32)).toInt() } }
        }

        fun <T : Any> buildSafeApi(fa: Kind<FList<*>,T>) : FListApi<T> = object : FListApi<T> {
            val ops = buildShreds<T>()
            override fun append(item: T): FLKApi<T> = ops.fappendSafe(fa, item) as FLKApi<T>
            override fun count(isMatch: (T) -> Boolean): Int = ops.fcount(fa, isMatch)
            override fun <B> fold(z: B, f: (B, T) -> B): B = ops.ffoldLeft(fa, z, f)
            override fun <B> foldLeft(z: B, f: (B, T) -> B): B  = ops.ffoldLeft(fa, z, f)
            override fun <B> foldRight(z: B, f: (T, B) -> B): B  = ops.ffoldRightSafe(fa, z, f)
            override fun head(): T?  = ops.fhead(fa)
            override fun init(): FLKApi<T> = ops.finitSafe(fa) as FLKApi<T>
            override fun last(): T? = ops.flast(fa)
            override fun pick(): T? = ops.fpick(fa)
            override fun prepend(item: T): FLKApi<T> = ops.fprepend(fa, item) as FLKApi<T>
            override fun reverse(): FLKApi<T> = ops.freverse(fa) as FLKApi<T>
            override fun tail(): FLKApi<T> = ops.ftail(fa) as FLKApi<T>
            override val size: Int by lazy { ops.fsize(fa) }
            override val empty: Boolean = ops.fempty(fa)
            override val ne: Boolean = ops.fne(fa)
            override val show by lazy { (ops.ffoldLeft(fa,"${fa::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }
            override val hash by lazy { (ops.ffoldLeft(fa,1549L) { acc: Long, h: T -> 31L * acc + h.hashCode() }).let{ (it xor (it ushr 32)).toInt() } }
        }

    }
}