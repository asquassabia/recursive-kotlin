package org.xrpn.flib.internal.ops

import org.xrpn.flib.adt.KWMsg
import org.xrpn.flib.adt.KWTrace
import org.xrpn.flib.adt.KWriter
import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.impl.KWf

/*
case class Debuggable[A](value: A, log: List[String]) {
def map[B](f: A => B): Debuggable[B] = {
val nextValue = f(value)
Debuggable(nextValue, this.log)
}
def flatMap[B](f: A => Debuggable[B]): Debuggable[B] = {
val nextValue: Debuggable[B] = f(value)
Debuggable(nextValue.value, this.log ::: nextValue.log)
}
}
 */

/*
 bind is very similar to flatMap – we can go from one to the other with a few simple changes
def bind<A,B>(fun: (A) -> MsgTracking<B>, tup: MsgTracking<A>): MsgTracking<B>
 (1) swap parameters
def bind[A,B](tup: Writer[A], fun: A -> Writer[B]): Writer[B]
 (2) rename tup and fun to ma and f
def bind[A,B](ma: Writer[A], f: A => Writer[B]): Writer[B]
 (3) replace Writer with F
def bind[A,B](ma: F[A], f: A => F[B]): F[B]
 (4) rename bind to flatMap
def flatMap[A,B](ma: F[A])(f: A => F[B]): F[B]

****
bind un-nests (one of the meanings of 'flatten') type constructors
flatMap flattens nested containers


// def bind[C, D](fc: F[C])(f: C => F[D]): F[D]
// def bind[MagTracking<C>, MagTracking<D>](fc: MagTracking<C>])(f: C => F[D]): F[D]

Just like flatMap first maps and then flattens

    def flatMap[A,B](ma: F[A])(f: A => F[B]): F[B] = flatten(map(ma)(f))

Bind first maps the given function onto the given Writer, i.e. applies the function to the
writer’s value component, and then flattens, in that it returns a Writer[A] rather than a
Writer[Writer[A]].

Also like flatMap, bind carries out the extra logic, required to compose functions
embellished with logging (functions returning a Writer). In this case the extra logic, is the
concatenation of two log strings.

*/

interface KWfBind<FF, AA, BB: Any> {
    fun bind(f: (AA) -> Kind<FF,BB>, kw: Kind<FF,AA>): Kind<FF,BB>
}
interface KWfCompose<A: Any, B: Any,C: Any> {
    fun compose(g: KWf<B,C>, f: KWf<A,B>): (A) -> Kind<KWriter<C>,C>
}
interface KWfChain<A: Any, B: Any,C: Any> {
    fun chain(f: KWf<A,B>, g: KWf<B,C>): (A) -> Kind<KWriter<C>,C>
}

@ConsistentCopyVisibility // this makes the visibility of .copy() private, like the constructor
internal data class KWfOps<A: Any, B: Any> private constructor (val f: (A) -> B): (String) -> (A) -> KWMsg<B> {
    override fun invoke(s: String): (a:A) -> KWMsg<B> = { a -> KWriter.of(f(a), s) as KWMsg<B> }
    companion object {

        fun<A: Any, B: Any> of(f: (A) -> B) = KWfOps(f)

        fun <AA: Any, BB: Any> bind(f: (AA) -> KWMsg<BB>, kw: KWriter<AA>): KWriter<BB> {
            val kwb: KWMsg<BB> = f(kw.item)
            return KWriter.push<AA,BB>(kwb.item,kwb.msg,kw as KWTrace<AA>)
        }

        fun <B:Any, C: Any, A: Any> compose(g: KWf<B,C>, f: KWf<A,B>, ): (A) -> KWriter<C> = { a:A ->
            val kwb: KWMsg<B> = f(a)
            val kwc: KWMsg<C> = g(kwb.item)
            KWriter.of(kwc.item,kwb.msg,kwc.msg)
        }

        fun <B:Any, C: Any, A: Any> chain(f: KWf<A,B>, g: KWf<B,C>): (A) -> KWriter<C> = compose(g,f)
    }
}