package org.xrpn.flib.internal.impl

import org.xrpn.flib.SAFE_RECURSION_SIZE
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.adt.FList
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
import org.xrpn.flib.internal.shredset.FBatchSequence
import org.xrpn.flib.internal.shredset.FSequence
import org.xrpn.flib.internal.tool.like
import org.xrpn.flib.pattern.Kind

internal interface FLKShreds<T: Any>: FBatchSequence<FList<*>, T> {
    companion object {
        internal fun <T : Any> build() : FLKShreds<T> = object : FLKShreds<T> {
            override fun fsize(fa: Kind<FList<*>,T>): Int = (fa as FLK<T>).fix().size()
            override fun fempty(fa: Kind<FList<*>,T>): Boolean = (fa as FLK<T>).fix().empty()
            override fun fne(fa: Kind<FList<*>,T>): Boolean = (fa as FLK<T>).fix().ne()
            override fun fequal(lhs: Kind<FList<*>,T>, rhs: Kind<FList<*>,T>): Boolean = lhs.equals(rhs)
            override fun fpick(fa: Kind<FList<*>,T>): T? = (fa as FLK<T>).fix().pick()
            override fun fcount(fa: Kind<FList<*>,T>, isMatch: (T) -> Boolean): Int = (fa as FLK<T>).fix().count(isMatch)
            override fun fprepend(fa: Kind<FList<*>,T>, item: T): FLKApi<T> = (fa as FLK<T>).fix().prepend(item).like(fa)
            override fun fappend(fa: Kind<FList<*>,T>, item: T): FLKApi<T> {
                fa as FLK<T>
                return (if (fa.size <= SAFE_RECURSION_SIZE.get()) fa.fix().append(item)
                        else fa.fix().appendSafe(item)
                       ).like(fa)
            }
            override fun <B> ffoldLeft(fa: Kind<FList<*>,T>, z: B, f: (B, T) -> B): B = (fa as FLK<T>).fix().foldLeft(z,f)
            override fun <B> ffoldRight(fa: Kind<FList<*>,T>, z: B, f: (T, B) -> B): B {
                fa as FLK<T>
                return if (fa.size <= SAFE_RECURSION_SIZE.get()) fa.fix().foldRight(z, f)
                       else fa.fix().foldRightSafe(z, f)
            }
            override fun fhead(fa: Kind<FList<*>,T>): T? = (fa as FLK<T>).fix().head()
            override fun ftail(fa: Kind<FList<*>,T>): FLKApi<T> = (fa as FLK<T>).fix().tail().like(fa)
            override fun freverse(fa: Kind<FList<*>,T>): FLKApi<T> = (fa as FLK<T>).fix().reverse().like(fa)
            override fun finit(fa: Kind<FList<*>,T>): FLKApi<T> {
                fa as FLK<T>
                return (if (fa.size <= SAFE_RECURSION_SIZE.get()) fa.fix().init() else fa.fix().initSafe()).like(fa)
            }
            override fun flast(fa: Kind<FList<*>,T>): T? = (fa as FLK<T>).fix().last()
            override fun fprepend(
                faNew: Kind<FList<*>, T>,
                fa: Kind<FList<*>, T>
            ): Kind<FList<*>, T> {
                fa as FLK<T>
                return (ffoldLeft(freverse(faNew),fa.fix()) { l, item -> FLCons(item,l) }).like(fa)
            }

            override fun fappend(
                fa: Kind<FList<*>, T>,
                faNew: Kind<FList<*>, T>
            ): Kind<FList<*>, T> {
                faNew as FLK<T>
                fa as FLK<T>
                return (ffoldLeft(freverse(fa),faNew.fix()) { l, item -> FLCons(item,l) }).like(fa)
            }
        }
    }
}