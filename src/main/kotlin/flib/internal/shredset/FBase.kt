package org.xrpn.flib.internal.shredset

import org.xrpn.flib.internal.shred.FComparing
import org.xrpn.flib.internal.shred.FCounting
import org.xrpn.flib.internal.shred.FInspecting
import org.xrpn.flib.internal.shred.FSelecting

/** Minimal operation set of any container */
internal interface FBase<K, A : Any> :
    FCounting<K, A>
    , FComparing<K, A>
    , FInspecting<K, A>
    , FSelecting<K, A>