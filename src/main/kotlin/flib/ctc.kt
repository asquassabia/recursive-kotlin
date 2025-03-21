package org.xrpn.flib

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicBoolean

val EQUALS_DEBUG: AtomicBoolean = AtomicBoolean(true)
val CAPTURE_SIZE: AtomicInteger = AtomicInteger(1024)
val SAFE_RECURSION_SIZE: AtomicInteger = AtomicInteger(1000)
val ERR_TAG: String = ":ERROR:"
val ERR_BY_TAG: String = ":BY:"
val FIX_TODO = "FIX TEMP TODO"