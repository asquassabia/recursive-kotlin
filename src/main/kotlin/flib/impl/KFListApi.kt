package org.xrpn.flib.impl

import org.xrpn.flib.FIX_TODO
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FLK
import org.xrpn.flib.adt.FLNel
import org.xrpn.flib.adt.FListNonEmpty
import org.xrpn.flib.internal.ops.KFListOps

fun <T: Any> KFList<T>.append(item: T): KFList<T> = TODO("$FIX_TODO not yet implemented")

fun <T: Any> KFList<T>.count(isMatch: (T) -> Boolean): Int = this.ops.fcount(this,isMatch)
fun <TT: Any> FListNonEmpty<TT>.count(isMatch: (TT) -> Boolean): Int = KFListOps.k2c<TT>(this.kind as FLK<TT>).let{ it.ops.fcount(it,isMatch)}

fun <T: Any> KFList<T>.equal(rhs: KFList<T>): Boolean = equals(rhs)
fun <TT: Any> FListNonEmpty<TT>.equal(rhs: FListNonEmpty<TT>): Boolean = KFListOps.k2c<TT>(this.kind as FLK<TT>).ops.equals(rhs.kind as FLK<TT>)

fun <T: Any, B> KFList<T>.fold(z: B, f: (B, T) -> B): B = ops.ffoldLeft(this,z,f)
fun <T: Any, B> KFList<T>.foldLeft(z: B, f: (B, T) -> B): B = ops.ffoldLeft(this,z,f)
fun <T: Any, B> KFList<T>.foldRight(z: B, f: (T, B) -> B): B =
    if (size < 500) ops.ffoldRight(this,z,f)
    else ops.ffoldRightSafe(this,z,f)

fun <T: Any> KFList<T>.head(): T? = this.ops.fhead(this)

fun <T: Any> KFList<T>.init(): KFList<T> =
    (if (size < 500) ops.finit(this)
    else ops.finitSafe(this)) as KFList<T>
fun <T: Any> KFList<T>.last(): T? = ops.flast(this)
fun <T: Any> KFList<T>.pick(): T? = this.ops.fpick(this)

fun <T: Any> KFList<T>.prepend(item: T): KFList<T> = (this.ops.fprepend(this,item)) as KFList<T>
fun <TT: Any> FListNonEmpty<TT>.prepend(item: TT): FListNonEmpty<TT> = KFList.of(FLCons(item,(this as FLNel).fnel)).fnel()

fun <T: Any> KFList<T>.reverse(): KFList<T> = (this.ops.freverse(this)) as KFList<T>
fun <T: Any> KFList<T>.tail(): KFList<T> = (this.ops.ftail(this)) as KFList<T>

