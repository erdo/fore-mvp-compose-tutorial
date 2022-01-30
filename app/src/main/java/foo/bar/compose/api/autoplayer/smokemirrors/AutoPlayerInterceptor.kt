package foo.bar.compose.api.autoplayer.smokemirrors

import co.early.fore.kt.core.delegate.Fore
import foo.bar.compose.feature.tictactoe.Game
import foo.bar.compose.feature.tictactoe.Player
import foo.bar.compose.feature.tictactoe.applyMove
import foo.bar.compose.feature.tictactoe.isWinner
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import kotlin.random.Random

/**
 * Our AutoPlayer service is actually a static response we get over HTTP. To
 * make the game suck less, we use an interceptor here to pretend the server
 * gives us something smarter. Obvs this class wouldn't exist in a real app
 */
class AutoPlayerInterceptor(private val game: () -> Game) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())

        val move = calculateMove()

        val responseBuilder = response.newBuilder()
            .body(
                body = "{ \"xPos\":${move.first}, \"yPos\":${move.second} }\n".toResponseBody(
                    contentType = response.body?.contentType()
                )
            )

        return responseBuilder.build()
    }

    // if there are any winning moves then we choose one of them
    // else if there are any blocking moves that prevent a loss we choose one of those
    // else we select a random available move
    private fun calculateMove(): Pair<Int, Int> {

        val availableMoves = game().state.board.flatMapIndexed { x, column ->
            column.mapIndexed { index, player -> index to player }
                .filter { it.second == Player.Nobody }
                .map { x to it.first }
        }

        val winningMoves = availableMoves.filter { move ->
            val updatedBoard = applyMove(game().state.board, move.first, move.second, Player.O)
            isWinner(Player.O, updatedBoard)
        }

        val blockingMoves = availableMoves.filter { move ->
            val updatedBoard = applyMove(game().state.board, move.first, move.second, Player.X)
            isWinner(Player.X, updatedBoard)
        }

        val selectedMove = when {
            winningMoves.isNotEmpty() -> winningMoves[Random.nextInt(winningMoves.size)]
            blockingMoves.isNotEmpty() -> blockingMoves[Random.nextInt(blockingMoves.size)]
            else -> availableMoves[Random.nextInt(availableMoves.size)]
        }

        Fore.getLogger().i("fake auto player chooses: x${selectedMove.first}, y${selectedMove.second}")

        return selectedMove
    }
}
