package flib.testtool

import io.kotest.core.spec.style.ExpectSpec
import org.xrpn.flib.internal.tool.Capture

/**
 * DANGER:
 * this will be unpredictable when Kotest runs with more than one thread ( 1 < parallelism).
 * System.out and System.err are shared resources, protected by the operating system. When
 * we try to capture them for the purpose of testing, we go against one of Kotest design
 * principles never to share data among tests. In a synchronous environment, e.g. when
 * all (and this means *all*) tests run on a single thread, there is a chance that shared
 * data may work under mutex locks. Otherwise, YMMV. You may always acquire a lock on System.err
 * and System.out during the test, but I'm just kidding.
 */

internal abstract class CaptureSpec(expose: CaptureSpec.(() -> Capture) -> Unit = {}) : ExpectSpec({
    expose(this as CaptureSpec, makeCapture)
}) {
    companion object {
        val makeCapture: () -> Capture = { Capture.build() }
    }
}