package org.xrpn.flib

import org.xrpn.flib.adt.FList
import org.xrpn.flib.pattern.Kind

// interface FLK<in T>: Kind<FList<*>, T>
typealias FLK<T> = Kind<FList<*>, T>
