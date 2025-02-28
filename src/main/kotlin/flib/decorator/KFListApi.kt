package org.xrpn.flib.decorator

import org.xrpn.flib.FIX_TODO

fun <T: Any> SFList<T>.append(item: T): SFList<T> = TODO("$FIX_TODO not yet implemented")
fun <T: Any> SFList<T>.count(isMatch: (T) -> Boolean): Int = this.ops.fcount(this,isMatch)
fun <T: Any, B> SFList<T>.fold(z: B, f: (B, T) -> B): B = ops.ffoldLeft(this,z,f)
fun <T: Any, B> SFList<T>.foldLeft(z: B, f: (B, T) -> B): B = ops.ffoldLeft(this,z,f)
fun <T: Any, B> SFList<T>.foldRight(z: B, f: (T, B) -> B): B =
    if (size < 500) ops.ffoldRight(this,z,f)
    else ops.ffoldRightSafe(this,z,f)
fun <T: Any> SFList<T>.head(): T? = this.ops.fhead(this)
fun <T: Any> SFList<T>.init(): SFList<T> =
    (if (size < 500) ops.finit(this)
    else ops.finitSafe(this)) as SFList<T>
fun <T: Any> SFList<T>.last(): T? = ops.flast(this)
fun <T: Any> SFList<T>.pick(): T? = this.ops.fpick(this)
fun <T: Any> SFList<T>.prepend(item: T): SFList<T> = (this.ops.fprepend(this,item)) as SFList<T>
fun <T: Any> SFList<T>.reverse(): SFList<T> = (this.ops.freverse(this)) as SFList<T>
fun <T: Any> SFList<T>.tail(): SFList<T> = (this.ops.ftail(this)) as SFList<T>

