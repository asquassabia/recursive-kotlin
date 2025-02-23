package org.xrpn.flib.decorator

import org.xrpn.flib.FIX_TODO

fun <T: Any> KFList<T>.append(item: T): KFList<T> = TODO("$FIX_TODO not yet implemented")
fun <T: Any> KFList<T>.count(isMatch: (T) -> Boolean): Int = this.ops.fcount(this,isMatch)
fun <T: Any> KFList<T>.equal(rhs: KFList<T>): Boolean = equals(rhs)
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
fun <T: Any> KFList<T>.reverse(): KFList<T> = (this.ops.freverse(this)) as KFList<T>
fun <T: Any> KFList<T>.tail(): KFList<T> = (this.ops.ftail(this)) as KFList<T>

