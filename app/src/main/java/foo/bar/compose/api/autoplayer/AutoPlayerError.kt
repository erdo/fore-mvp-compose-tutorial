package foo.bar.compose.api.autoplayer

import co.early.fore.net.MessageProvider
import foo.bar.compose.message.ErrorMsg
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 *
 * <Code>
 *
 * The server returns custom error codes in this form:
 *
 * {
 * "errorCode":"AUTOPLAYER_REQUIRES_PAYMENT"
 * }
 *
 * </Code>
 *
 * example https://run.mocky.io/v3/ed17f6eb-48e1-41dd-a506-206e310ad39d
 *
 */
@Serializable
class AutoPlayerError(private val errorCode: ErrorCode?) : MessageProvider<ErrorMsg> {

    @Serializable
    enum class ErrorCode constructor(val errorMessage: ErrorMsg) {

        @SerialName("AUTOPLAYER_UNAVAILABLE")
        AutoPlayerUnavailable(ErrorMsg.AutoPlayerUnavailable),

        @SerialName("AUTOPLAYER_ASLEEP")
        USER_LOCKED(ErrorMsg.AutoPlayerAsleep),

        @SerialName("AUTOPLAYER_REQUIRES_PAYMENT")
        USER_NOT_ENABLED(ErrorMsg.AutoPlayerNeedsPayment);

    }

    override fun getMessage(): ErrorMsg {
        return errorCode?.errorMessage ?: ErrorMsg.Misc
    }
}
