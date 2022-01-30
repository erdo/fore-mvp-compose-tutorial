package foo.bar.compose.api.autoplayer

import io.ktor.client.*
import io.ktor.client.request.*

/**
 * These stubs are hosted at https://www.mocky.io/
 *
 * success example response:
 * https://run.mocky.io/v3/86532780-6bb7-483f-8881-33a21e6d3669
 *
 * error example response:
 * https://run.mocky.io/v3/ed17f6eb-48e1-41dd-a506-206e310ad39d
 */
data class AutoPlayerService(
    val getAutoPlayersTurn: suspend () -> NextTurnPojo
) {

    companion object {

        fun create(httpClient: HttpClient): AutoPlayerService {

            val baseUrl = "https://www.mocky.io/v3/"
            val delaySeconds = 3

            return AutoPlayerService(
                // error response
                // getAutoPlayersTurn = { httpClient.get("${baseUrl}ed17f6eb-48e1-41dd-a506-206e310ad39d/?mocky-delay=${delaySeconds}s") }
                getAutoPlayersTurn = { httpClient.get("${baseUrl}86532780-6bb7-483f-8881-33a21e6d3669/?mocky-delay=${delaySeconds}s") }
            )
        }
    }
}
