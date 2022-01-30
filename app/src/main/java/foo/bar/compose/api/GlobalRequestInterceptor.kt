package foo.bar.compose.api

import foo.bar.compose.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * This will be specific to your own app.
 *
 * Typically you would construct this class with some kind of Session object or similar that
 * you would use to customize the headers according to the logged in status of the user, for example
 */
class GlobalRequestInterceptor(
        /*, private final Session session */
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val original = chain.request()

        val requestBuilder = original.newBuilder()

        requestBuilder.addHeader("Content-Type", "application/json")
        //requestBuilder.addHeader("X-MyApp-Auth-Token", !session.hasSession() ? "expired" : session.getSessionToken());
        requestBuilder.addHeader("User-Agent", "forecompose-user-agent-${BuildConfig.VERSION_NAME}")

        return chain.proceed(requestBuilder.build())
    }
}
