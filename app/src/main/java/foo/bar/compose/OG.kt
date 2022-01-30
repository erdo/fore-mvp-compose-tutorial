package foo.bar.compose

import android.app.Application
import co.early.fore.kt.core.delegate.DebugDelegateDefault
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.net.InterceptorLogging
import co.early.fore.kt.net.ktor.CallProcessorKtor
import co.early.persista.PerSista
import foo.bar.example.forektorkt.api.CustomKtorBuilder
import foo.bar.compose.api.GlobalErrorHandler
import foo.bar.compose.api.GlobalRequestInterceptor
import foo.bar.compose.api.autoplayer.AutoPlayerService
import foo.bar.compose.feature.tictactoe.Game
import foo.bar.compose.api.autoplayer.smokemirrors.AutoPlayerInterceptor
import java.util.*

/**
 * Copyright © 2015-2021 early.co. All rights reserved.
 */
@Suppress("UNUSED_PARAMETER")
object OG {

    private var initialized = false
    private val dependencies = HashMap<Class<*>, Any>()

    fun setApplication(application: Application) {

        // create dependency graph
        if (BuildConfig.DEBUG) {
            Fore.setDelegate(DebugDelegateDefault("foo_"))
        }
        val logger = Fore.getLogger()

        lateinit var game: Game

        // networking classes common to all models
        val httpClient = CustomKtorBuilder.create(
            GlobalRequestInterceptor(),
            AutoPlayerInterceptor({ game }),
            InterceptorLogging(logger) //logging interceptor should be the last one
        )
        val callProcessor = CallProcessorKtor(
            errorHandler = GlobalErrorHandler(logger),
        )

        val autoPlayerService: AutoPlayerService = AutoPlayerService.create(httpClient)

        val perSista = PerSista(
            dataDirectory = application.filesDir,
            logger = logger,
        )

        game = Game(
            autoPlayerService = autoPlayerService,
            callProcessorKtor = callProcessor,
            perSista = perSista
        )

        // add models to the dependencies map if you will need them later
        dependencies[Game::class.java] = game
    }

    fun init() {
        if (!initialized) {
            initialized = true

            // run any necessary initialization code once object graph has been created here

        }
    }

    /**
     * This is how dependencies get injected, typically an Activity/Fragment/View will call this
     * during the onCreate()/onCreateView()/onFinishInflate() method respectively for each of the
     * dependencies it needs.
     *
     * Can use a DI library for similar behaviour using annotations
     *
     * Will return mocks if they have been set previously in putMock()
     *
     *
     * Call it like this:
     *
     * <code>
     *     yourModel = OG[YourModel::class.java]
     * </code>
     *
     * If you want to more tightly scoped object, one way is to pass a factory class here and create
     * an instance where you need it
     *
     */
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(model: Class<T>): T = dependencies[model] as T

    fun <T> putMock(clazz: Class<T>, instance: T) {
        dependencies[clazz] = instance as Any
    }
}
