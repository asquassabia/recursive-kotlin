package org.xrpn.flib.internal.shredset

import org.xrpn.flib.internal.shred.FBuilding
import org.xrpn.flib.internal.shred.FBuildingSafe
import org.xrpn.flib.internal.shred.FFolding
import org.xrpn.flib.internal.shred.FFoldingSafe
import org.xrpn.flib.internal.shred.FSequencing
import org.xrpn.flib.internal.shred.FSequencingSafe

/**
 * Signature of a container with a natural order independent of its content
 * [A]. It may hold none, one, or more elements.
 */
internal interface FSequence<K, A : Any> :
    FBase<K, A>
    , FBuilding<K, A>
    , FBuildingSafe<K, A>
    , FFolding<K, A>
    , FFoldingSafe<K, A>
    , FSequencing<K, A>
    , FSequencingSafe<K, A>
//    , FMatching<K,A>
//    , FPruning<K,A>
//    , FFiltering<K,A>
//    , FZipping<K, A>