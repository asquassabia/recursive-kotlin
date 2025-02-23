package org.xrpn.flib.pattern

interface KLift<F> {

    /**
     * [lift] transforms, i.e. 'lifts', something of type [TL] and turns it
     * into an [F]<[TL]>. If [F] happens to be some undetermined container or
     * wrapper, [TL] is 'lifted' into [F]. At this time, we don't know anything
     * about [F], so saying [F]<[TL]> is a manner to think of what 'lift' does.
     */

    fun <TL: Any> lift(fa:Kind<F,*>, a: TL): Kind<F, TL>
}