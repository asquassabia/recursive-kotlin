package org.xrpn.flib.internal.attributeset

import org.xrpn.flib.attribute.Functor
import org.xrpn.flib.attribute.Monad

interface FunMon<F,A> :
    Functor<F,A>
    , Monad<F,A>