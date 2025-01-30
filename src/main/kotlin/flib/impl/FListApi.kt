package org.xrpn.flib.impl

fun <T: Any> FListKind<T>.append(item: T): FListKind<T> = TODO()
fun <T: Any> FListKind<T>.count(isMatch: (T) -> Boolean): Int = this.flops.fcount(this,isMatch)
fun <T: Any> FListKind<T>.equal(rhs: FListKind<T>): Boolean = equals(rhs)
fun <T: Any, B> FListKind<T>.fold(z: B, f: (B, T) -> B): B = flops.ffoldLeft(this,z,f)
fun <T: Any, B> FListKind<T>.foldLeft(z: B, f: (B, T) -> B): B = flops.ffoldLeft(this,z,f)
fun <T: Any, B> FListKind<T>.foldRight(z: B, f: (T, B) -> B): B =
    if (size < 500) flops.ffoldRight(this,z,f)
    else flops.ffoldRightSafe(this,z,f)
fun <T: Any> FListKind<T>.head(): T? = this.flops.fhead(this)
fun <T: Any> FListKind<T>.init(): FListKind<T> =
    if (size < 500) flops.finit(this).fix()
    else flops.finit(this).fix()
fun <T: Any> FListKind<T>.last(): T? = flops.flast(this)
fun <T: Any> FListKind<T>.pick(): T? = this.flops.fpick(this)
fun <T: Any> FListKind<T>.prepend(item: T): FListKind<T> = this.flops.fprepend(this,item).fix()
fun <T: Any> FListKind<T>.reverse(): FListKind<T> = this.flops.freverse(this).fix()
fun <T: Any> FListKind<T>.tail(): FListKind<T> = this.flops.ftail(this).fix()
