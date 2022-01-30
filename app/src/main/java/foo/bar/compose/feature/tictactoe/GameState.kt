package foo.bar.compose.feature.tictactoe

import foo.bar.compose.R
import foo.bar.compose.feature.tictactoe.Player.*
import foo.bar.compose.message.ErrorMsg
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * This immutable game state is working quite hard for us, from the
 * basic structure we are able to tell not just the state of the board,
 * but also whose turn it is, and how many turns have been played.
 *
 * Code here is really easy to test: it's not threaded, it's all based
 * on immutable state.
 *
 * The downside is performance: notice how each time we ask who's
 * turn it is, the board state is counted up (the UI layer might ask
 * this question a lot as it re draws itself).
 *
 * For this reason, just to demonstrate the tradeoff, we will decide to
 * keep the "who won" calculation in the game model where it won't be run
 * as often (only when a player takes a turn) and where we can easily run
 * it using coroutines as we do with the network connection work. Here we
 * just keep the results of that calculation in the winner value.
 *
 * It's somewhat related to this advice to keep UIs snappy:
 * https://erdo.github.io/android-fore/05-extras.html#slow-getters
 */
@Serializable
data class GameState(
    val firstPlayer: Player,
    val winner: Player = Nobody,
    val board: List<List<Player>> = listOf(
        listOf(Nobody, Nobody, Nobody),
        listOf(Nobody, Nobody, Nobody),
        listOf(Nobody, Nobody, Nobody)
    ),
    @Transient
    val isLoading: Boolean = false,
    @Transient
    val error: ErrorMsg = ErrorMsg.NoError,
) {

    init {
        require(board.size == boardSize) {"game board must have $boardSize rows"}
        board.forEachIndexed { index, list ->
            require(list.size == boardSize) {"game board row[$index] must have $boardSize columns"}
        }
        require(firstPlayer != Nobody) {"somebody must be the first player"}
    }

    fun whoseTurn() = if (turnsTaken()%2 == 0) firstPlayer else oppositePlayer(firstPlayer)
    fun gameFinished(): Boolean = (winner != Nobody || boardComplete())

    private fun boardComplete(): Boolean = (turnsTaken() == boardSize * boardSize)
    private fun turnsTaken(): Int {
        return board.sumOf { row ->
            row.count {
                it != Nobody
            }
        }
    }
}

@Serializable
sealed class Player(val label: String, val winMessageRes: Int) {
    @Serializable
    object X : Player("X", R.string.winner_x)
    @Serializable
    object O : Player("O", R.string.winner_o)
    @Serializable
    object Nobody : Player("", R.string.winner_nobody)
}

fun oppositePlayer(player: Player): Player {
    return when (player) {
        X -> O
        O -> X
        else -> Nobody
    }
}
