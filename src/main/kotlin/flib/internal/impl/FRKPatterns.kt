package org.xrpn.flib.internal.impl

import org.xrpn.flib.adt.FRDone
import org.xrpn.flib.adt.FRFail
import org.xrpn.flib.adt.FRK
import org.xrpn.flib.adt.FRes
import org.xrpn.flib.pattern.KFunctor
import org.xrpn.flib.pattern.KMonad
import org.xrpn.flib.pattern.Kind

internal interface FRKPatterns: KMonad<FRes>, KFunctor<FRes> {

    companion object {

        internal fun <T> liftToFail(e: T): Kind<FRFail<*>, T> =
            @Suppress("UNCHECKED_CAST")
            (FRFail.of(e) as Kind<FRFail<*>, T>)

        internal fun <T> liftToDone(t: T): Kind<FRDone<*>, T> =
            @Suppress("UNCHECKED_CAST")
            (FRDone.of(t) as Kind<FRDone<*>, T>)

        internal fun build(): FRKPatterns = object : FRKPatterns {

            override fun <TA : Any, TB : Any> flatMap(
                fa: Kind<FRes, TA>,
                f: (TA) -> Kind<FRes, TB>
            ): FRK<TB> = f((fa as FRK<TA>).fix().r()) as FRK<TB>

            override fun <TL : Any> lift(
                fa: Kind<FRes, *>,
                a: TL
            ): FRK<TL> = when (fa.fix()) {
                is FRFail<*> -> liftToFail(a) as FRK<TL>
                is FRDone<*> -> liftToDone(a) as FRK<TL>
            }
        }
    }
}
