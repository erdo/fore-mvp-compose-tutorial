package foo.bar.compose.api

import co.early.fore.kt.core.logging.Logger
import co.early.fore.kt.net.ktor.ErrorHandler
import co.early.fore.net.MessageProvider
import foo.bar.compose.message.ErrorMsg
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.UnknownServiceException
import java.nio.charset.CoderMalfunctionError

/**
 * You can probably use this class almost as is for your own app, but you'll probably also want to
 * customise the behaviour for specific HTTP codes etc, hence it's not in the fore library
 */
class GlobalErrorHandler(private val logWrapper: Logger) :
    ErrorHandler<ErrorMsg> {

    override suspend fun <CE : MessageProvider<ErrorMsg>> handleError(
        t: Throwable,
        customErrorClazz: Class<CE>?
    ): ErrorMsg {

        logWrapper.d("handling error in global error handler", t)

        val errorMessage = when (t) {

            is ResponseException -> {

                var msg = when (t) {
                    is ClientRequestException -> {
                        ErrorMsg.Server
                    } // in 400..499
                    is RedirectResponseException -> {
                        ErrorMsg.Server
                    } //in 300..399
                    is ServerResponseException -> {
                        ErrorMsg.Server
                    } //in 500..599
                    else -> {
                        ErrorMsg.Network
                    } //something else
                }

                val response = t.response

                logWrapper.e("handleError() HTTP:" + response.status)

                //get more specific with the error type
                msg = when (response.status.value) {
                    401 -> ErrorMsg.SessionTimeOut
                    429 -> ErrorMsg.RateLimited
                    404 -> ErrorMsg.Server //if this happens in prod, it's usually a server config issue IME
                    else -> null
                } ?: msg

                //let's get even more specifics about the error
                customErrorClazz?.let { clazz ->
                    msg = parseCustomError(msg, response, clazz)
                }

                msg
            }
            is NoTransformationFoundException -> ErrorMsg.Server // content type is probably wrong, check response from server in app logs
            is SerializationException -> ErrorMsg.Server //parsing issue, maybe response is not json, or does not match expected type, or is empty
            is IOException -> ErrorMsg.Network //airplane mode is on, no network coverage etc
            is UnknownServiceException -> ErrorMsg.Security //most likely https related, check for usesCleartextTraffic if required
            else -> ErrorMsg.Network
        }

        logWrapper.d("replyWithFailure() returning:$errorMessage")
        return errorMessage
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <CE : MessageProvider<ErrorMsg>> parseCustomError(
        provisionalErrorMessage: ErrorMsg,
        errorResponse: HttpResponse,
        customErrorClazz: Class<CE>
    ): ErrorMsg {

        var customError: ErrorMsg = provisionalErrorMessage

        try {

            val bodyContent = errorResponse.readText(Charsets.UTF_8)
            logWrapper.d("parseCustomError() attempting to parse this content:\n $bodyContent")
            val errorClass = Json.decodeFromString(serializer(customErrorClazz), bodyContent) as CE
            customError = errorClass.message

        } catch (t: Throwable) {

            logWrapper.e("parseCustomError() unexpected issue$t")

            when (t) {
                is IllegalStateException, is CoderMalfunctionError -> { logWrapper.e("Error handling 01") } //problem reading body text
                is SerializationException -> { logWrapper.e("Error handling 02") } //parsing error, @Serializable missing, wrong error class specified etc
                is UnsupportedEncodingException -> { logWrapper.e("Error handling 03") }
                is NullPointerException -> { logWrapper.e("Error handling 04") }
                else -> { logWrapper.e("Error handling 05") }
            }
        }

        logWrapper.d("parseCustomError() returning:$customError")
        return customError
    }
}
