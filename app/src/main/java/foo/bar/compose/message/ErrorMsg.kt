package foo.bar.compose.message

import foo.bar.compose.R
import kotlinx.serialization.Serializable

@Serializable
sealed class ErrorMsg(val msgRes: Int) {
    object Misc: ErrorMsg(R.string.msg_error_misc)
    object Network: ErrorMsg(R.string.msg_error_network)
    object Security: ErrorMsg(R.string.msg_error_misc)
    object Server: ErrorMsg(R.string.msg_error_server)
    object Client: ErrorMsg(R.string.msg_error_client)
    object RateLimited: ErrorMsg(R.string.msg_error_rate_limited)
    object SessionTimeOut: ErrorMsg(R.string.msg_error_session_timeout)
    object Busy: ErrorMsg(R.string.msg_error_busy)
    object AutoPlayerUnavailable: ErrorMsg(R.string.msg_error_autoplayer_unavailable)
    object AutoPlayerAsleep: ErrorMsg(R.string.msg_error_autoplayer_asleep)
    object AutoPlayerNeedsPayment: ErrorMsg(R.string.msg_error_autoplayer_needspayment)
    object AutoPlayerInvalidMove: ErrorMsg(R.string.msg_error_autoplayer_invalid_move)
    object NoError: ErrorMsg(R.string.empty_string)
}
