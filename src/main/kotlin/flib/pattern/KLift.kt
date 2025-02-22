package org.xrpn.flib.pattern

interface KLift<F> {

    /**
     * [lift] transforms, i.e. 'lifts', something of type [LA] and turns it
     * into an [F]<[LA]>. If [F] happens to be some undetermined container or
     * wrapper, [LA] is 'lifted' into [F]. At this time, we don't know anything
     * about [F], so saying [F]<[LA]> is a manner to think of what 'lift' does.
     */

    fun <LA: Any> lift(fa:Kind<F,*>, a: LA): Kind<F, LA>?
}