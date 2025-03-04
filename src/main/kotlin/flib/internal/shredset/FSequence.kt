package org.xrpn.flib.internal.shredset

import org.xrpn.flib.internal.shred.FBuilding
import org.xrpn.flib.internal.shred.FFolding
import org.xrpn.flib.internal.shred.FSequencing

/**
 * Signature of a container with a natural order independent of its content
 * [A]. It may hold none, one, or more elements.
 */
internal interface FSequence<K, A : Any> :
    FBase<K, A>
    , FBuilding<K, A>
    , FFolding<K, A>
    , FSequencing<K, A>
//    , FMatching<K,A>
//    , FPruning<K,A>
//    , FFiltering<K,A>
//    , FZipping<K, A>