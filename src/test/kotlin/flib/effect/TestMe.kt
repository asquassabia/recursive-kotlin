package flib.effect

import org.xrpn.flib.ERR_BY_TAG
import org.xrpn.flib.ERR_TAG
import org.xrpn.flib.internal.IdMe
import org.xrpn.flib.internal.shredset.SizeMe

class TestMe(): IdMe, SizeMe {
    override val size: Int
        get() = 0
    override val hash: Int
        get() = show.hashCode()
    override val show: String
        get() = showMe
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        javaClass != other?.javaClass -> false
        other is TestMe -> hash == other.hash
        else -> false
    }
    override fun hashCode(): Int = hash
    override fun toString(): String = show

    companion object {
        val exm = "DEF"
        val showMe = "ABCD"
        val t = IllegalStateException("$exm")
        val msgOracle = "class flib.effect.TestMe($showMe)\n"
        val tmsgStartOracle = "java.lang.IllegalStateException: $exm\n\tat flib.effect.TestMe.<clinit>"
        val emitter = TestMe()
        val errorMessage = "error report"
        val circumstances = "circumstances report"
        val errMsgPrefix = "$ERR_TAG $errorMessage $ERR_BY_TAG"
        val errLogOracle = "$errMsgPrefix \n\tclass flib.effect.TestMe($showMe)\n"
        val errLogNNLOracle = "$errMsgPrefix class flib.effect.TestMe($showMe)"
        val errLogNullOracle = "$errMsgPrefix \n\t[org.xrpn.flib.internal.effect.FLibLogCtx, kotlin.Any]()\n"
        val exceptionReport = "$ERR_TAG $circumstances $ERR_BY_TAG \n\tclass flib.effect.TestMe($showMe)\n$tmsgStartOracle"
    }
}
