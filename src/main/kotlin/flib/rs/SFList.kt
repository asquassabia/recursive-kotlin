package org.xrpn.flib.rs

import org.xrpn.flib.EQUALS_DEBUG
import org.xrpn.flib.adt.FLCons
import org.xrpn.flib.adt.FListApi
import org.xrpn.flib.adt.FLNil
import org.xrpn.flib.adt.FList
import org.xrpn.flib.adt.FLKApi
import org.xrpn.flib.internal.effect.FLibLog
import org.xrpn.flib.internal.impl.FLKPatterns
import org.xrpn.flib.internal.impl.FLKShreds
import org.xrpn.flib.internal.tool.continuousMatches
import org.xrpn.flib.internal.tool.flistBuilder
import org.xrpn.flib.internal.tool.flistReverseBuilder
import org.xrpn.flib.pattern.InjectionTarget

/**
 * A utility wrapper that provides boilerplate functionality for [FList] without
 * polluting the adt with crud. The content is parametrized, and the actual type
 * instance must be an immutable type. All behavior assumes that this is the case.
 * Terrible things may happen if this assumption is violated. Enforcing the assumption
 * is at best baroque, if possible at all, and at worst imbued with a false sense of
 * security.
 */

class SFList<A: Any> private constructor(
    private val list: FList<A>,
    private val shallowEquals: Boolean = true,
    private val ioc: InjectionTarget<Pair<FLKShreds<A>,FLKPatterns<A>>> = 
        InjectionTarget(Pair(FLKShreds.build(),FLKPatterns.build<A>()))
): FLKApi<A> {
    fun isDeep(): Boolean = !shallowEquals
    override fun fix(): FList<A> = list
    override val size: Int by lazy { ioc.d.first.fsize(this) }
    val lHash by lazy { (ioc.d.first.ffoldLeft(this,1549L) { acc: Long, h: A -> 31L * acc + h.hashCode() }).let{ (it xor (it ushr 32)).toInt() } }
    override val hash: Int by lazy { (31L * this::class.simpleName!!.hashCode().toLong() + lHash.toLong()).let{ (it xor (it ushr 32)).toInt() } }
    override val empty: Boolean by lazy { ioc.d.first.fempty(this) }
    override val ne: Boolean by lazy { ioc.d.first.fne(this) }
    override val show by lazy { (ioc.d.first.ffoldLeft(this,"${this::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }
    override fun hashCode(): Int = hash
    override fun toString(): String = show
    override fun equals(other: Any?): Boolean = other?.let { (it as? SFList<*>)?.let {
        if (EQUALS_DEBUG.get()) {
            if(equal(it)) {
                val otherSize = continuousMatches(list, it.list)
                assert (size == otherSize) {(object : FLibLog {}).log(
                    msg = "same hash, but not equal\nthis =$show,\nother=${it.show}\nthis =$hash,\nother=${it.hash}",
                    emitter = this@SFList
                )}
                true
            } else false
        } else equal(it) && (shallowEquals || size == continuousMatches(list, it.list))
    }} == true

    override fun append(item: A): FLKApi<A> = ioc.d.first.fappend(this, item) as FLKApi<A>
    override fun count(isMatch: (A) -> Boolean): Int = ioc.d.first.fcount(this, isMatch)
    override fun <B> fold(z: B, f: (B, A) -> B): B = ioc.d.first.ffoldLeft(this, z, f)
    override fun <B> foldLeft(z: B, f: (B, A) -> B): B = ioc.d.first.ffoldLeft(this, z, f)
    override fun <B> foldRight(z: B, f: (A, B) -> B): B = ioc.d.first.ffoldRight(this, z, f) 
    override fun head(): A?  = ioc.d.first.fhead(this)
    override fun init(): FLKApi<A> = ioc.d.first.finit(this) as FLKApi<A>
    override fun last(): A? = ioc.d.first.flast(this)
    override fun pick(): A? = ioc.d.first.fpick(this)
    override fun prepend(item: A): FLKApi<A> = ioc.d.first.fprepend(this, item) as FLKApi<A>
    override fun reverse(): FLKApi<A> = ioc.d.first.freverse(this) as FLKApi<A>
    override fun tail(): FLKApi<A> = ioc.d.first.ftail(this) as FLKApi<A>
    override fun <S : Any> map(f: (A) -> S): FLKApi<S> = ioc.d.second.map(this,f) as FLKApi
    override fun <S : Any> flatMap(f: (A) -> FLKApi<S>): FLKApi<S> = ioc.d.second.flatMap(this,f) as FLKApi<S>
    override fun <S : Any> lift(s: S): FLKApi<S> = of(FLCons(s,FLNil<S>()))

    companion object {

        /** Builder of empty [SFList]<[T1]> */
        fun <T : Any> of(deepEquals: Boolean = false): FLKApi<T> {
            val sfl = SFList<T>(FLNil<T>(), !deepEquals)
            return sfl
        }

        /** Builder of [SFList]<[T]> with content [flist]. */
        fun <T : Any> of(flist: FList<T>, deepEquals: Boolean = false): FLKApi<T> {
            val sfl = SFList<T>(flist,!deepEquals)
            return sfl // SFListAlias(sfl) as FLKApi<Int>
        }

        fun <T : Any> of(flist: FList<T>, ioc: SFList<T>): FLKApi<T> {
            val sfl = SFList<T>(flist,ioc.shallowEquals, ioc.ioc)
            return sfl // SFListAlias(sfl) as FLKApi<Int>
        }

        fun <T : Any> toggleEquals(sfl: SFList<T>): SFList<T> {
            val sfl = SFList<T>(sfl.list,!sfl.shallowEquals, sfl.ioc)
            return sfl // SFListAlias(sfl) as FLKApi<Int>
        }

        fun ofIntSeq(offset:Int, top:Int, deepEquals: Boolean = false):FLKApi<Int> {
            require(offset < top) { "offset must be smaller than top." }
            val sfl = SFList(flistBuilder(offset, top), !deepEquals)
            return sfl // SFListAlias(sfl) as FLKApi<Int>
        }
        fun ofIntSeqRev(offset:Int, top:Int, deepEquals: Boolean = false): FLKApi<Int> {
            require(offset < top) { "offset must be smaller than top." }
            val sfl = SFList(flistReverseBuilder(offset, top), !deepEquals)
            return sfl // SFListAlias(sfl) as FLKApi<Int>
        }
    }
}