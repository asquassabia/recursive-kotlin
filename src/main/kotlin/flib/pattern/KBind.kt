package org.xrpn.flib.pattern

interface KBind<F, A, B> {

    /**
     * The function [kbind] is very similar to [KMonad.flatMap]. By swapping
     * the parameters, you can write kbind(fa: Kind<F,A>, f: (A) -> Kind<F,B>):
     * Kind<F,B> and you can extract the type parameters to turn it into fun
     * <A,B> flatMap(fa: Kind<F,A>, f: (A) -> Kind<F,B>): Kind<F,B> However,
     * for [KBind] the type parameters [A] and [B] are bound to a type at
     * declaration time. In the case pf [Monad].[flatMap] only [F] is known
     * at declaration time. This makes a big difference in the definition of
     * [kbind], that is, when you implement the [KBind] interface and will need
     * to writhe a body for [kbind]. Also, there is no requirement for [KBind] to
     * have a [Monad.lift] function.
     */

    fun kbind(f: (A) -> Kind<F, B>, fa: Kind<F, A>): Kind<F, B>
}