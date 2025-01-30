package org.xrpn.flib.internal.shred

import org.xrpn.flib.attribute.Kind
import org.xrpn.flib.internal.shredset.FSequence

internal interface FRotating<F, A : Any> : FSequencing<F, A> {

    /**
     * Rotate left. (A, B, C). [frotl] () becomes (B, C, A). Return the rotated
     * [Sequence], or [null] if [fa] has less than 3 elements.
     */
    fun frotl(fa: Kind<FSequence<F, A>, A>): Kind<F, A>?

    /**
     * Rotate right. (A, B, C). [frotr] () becomes (C, A, B). Return the rotated
     * [Sequence], or [null] if [fa] has less than 3 elements.
     */
    fun frotr(fa: Kind<FSequence<F, A>, A>): Kind<F, A>?

    /**
     * Swap head and next. (A, B, C). [fswaph] () becomes (B, A, C). Return the
     * rotated [Sequence], or [null] if [fa] has less than 2 elements.
     */
    fun fswaph(fa: Kind<FSequence<F, A>, A>): Kind<F, A>?
}