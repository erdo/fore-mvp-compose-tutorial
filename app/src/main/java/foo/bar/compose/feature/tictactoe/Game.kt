package foo.bar.compose.feature.tictactoe

import co.early.fore.core.observer.Observable
import co.early.fore.kt.core.coroutine.awaitMain
import co.early.fore.kt.core.coroutine.launchIO
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.kt.core.observer.ObservableImp
import co.early.fore.kt.core.type.Either.Fail
import co.early.fore.kt.core.type.Either.Success
import co.early.fore.kt.net.ktor.CallWrapperKtor
import co.early.persista.PerSista
import foo.bar.compose.api.autoplayer.AutoPlayerError
import foo.bar.compose.api.autoplayer.AutoPlayerService
import foo.bar.compose.api.autoplayer.NextTurnPojo
import foo.bar.compose.feature.tictactoe.Player.*
import foo.bar.compose.message.ErrorMsg

const val boardSize = 3

/**
 * erdo: inspired by a java class taken from here: https://github.com/ericmaxwell2003/ticTacToe
 *
 * It's been re-written in kotlin, the board has been made Observable, we added a NOBODY player
 * so that player values don't need to be null, and we're making more use of immutable state.
 *
 * We've also added the concept of an auto player service which provides us a fake opponent from
 * the network
 *
 * In this game the mobile user is always X, network user is always O
 */
class Game (
    private val autoPlayerService: AutoPlayerService,
    private val callWrapper: CallWrapperKtor<ErrorMsg>,
    private val perSista: PerSista
) : Observable by ObservableImp() {

    var state: GameState = GameState(X)
    private set

    init {
        Fore.getLogger().i("init() id:${Thread.currentThread().id}")

        // reload any saved state
        perSista.read(state) {
            state = it
            notifyObservers()
            if (state.whoseTurn() == O && !state.gameFinished()){
                fetchAutoPlayerResponse()
            }
        }
    }

    fun newGame() {

        Fore.getLogger().i("newGame() id:${Thread.currentThread().id}")

        updateStateAndNotify(GameState(firstPlayer = X))
    }

    fun play(xPos: Int, yPos: Int) {

        Fore.getLogger().i("X plays x$xPos, y$yPos id:${Thread.currentThread().id}")

        checkMove(xPos, yPos)

        if (state.isLoading) {
            return
        }

        if (isUserPlayValid(xPos, yPos, state, X)) {

            val newBoard = applyMove(state.board, xPos, yPos, X)
            val isWinner = isWinner(X, newBoard)

            updateStateAndNotify(state.copy(board = newBoard, winner = if (isWinner) X else Nobody))

            if (!state.gameFinished()) {
                fetchAutoPlayerResponse()
            }
        }
    }

    fun retryAutoPlayer() {
        if (state.isLoading || state.gameFinished() || state.whoseTurn() == X) {
            return
        } else {
            fetchAutoPlayerResponse()
        }
    }

    private fun fetchAutoPlayerResponse() {

        updateStateAndNotify(state.copy(isLoading = true, error = ErrorMsg.NoError))

        launchIO {

            val deferredResult = callWrapper.processCallAsync(AutoPlayerError::class.java) {
                autoPlayerService.getAutoPlayersTurn()
            }

            when (val result = deferredResult.await()) {
                is Fail -> processError(result.value)
                is Success -> processAutoPlayerMove(result.value)
            }
        }
    }

    private suspend fun processError(errorMsg: ErrorMsg) {
        awaitMain { // pop to the UI thread to update the state
            updateStateAndNotify(state.copy(isLoading = false, error = errorMsg))
        }
    }

    private suspend fun processAutoPlayerMove(nextTurn: NextTurnPojo) {
        Fore.getLogger().i("O plays x${nextTurn.xPos}, y${nextTurn.yPos}")

        val newBoard = applyMove(state.board, nextTurn.xPos, nextTurn.yPos, O)
        val win = isWinner(O, newBoard)

        awaitMain { // pop to the UI thread to update the state
            updateStateAndNotify(
                state.copy(isLoading = false, board = newBoard, winner = if (win) O else Nobody)
            )
        }
    }

    private fun updateStateAndNotify(newState: GameState) {
        state = newState
        perSista.write(newState){} // fire and forget storage (so we can come back from process death)
        notifyObservers()
    }
}

private fun isUserPlayValid(xPos: Int, yPos: Int, gS: GameState, p: Player): Boolean {
    return !gS.gameFinished() && gS.whoseTurn() == p && gS.board[xPos][yPos] == Nobody
}

fun applyMove(board: List<List<Player>>, xPos: Int, yPos: Int, player: Player): List<List<Player>> {

    checkBoard(board)
    checkMove(xPos, yPos)

    return board.mapIndexed { x, col ->
        col.mapIndexed { y, p ->
            if (x == xPos && y == yPos) player else p
        }
    }
}

fun isWinner(player: Player, board: List<List<Player>>): Boolean {

    checkBoard(board)

    if (player == Nobody) {
        return false
    }

    val rowHits = Array(boardSize) { 0 }
    val columnHits = Array(boardSize) { 0 }
    var diagonalHits = 0
    var antiDiagonalHits = 0

    board.forEachIndexed { i, row ->
        row.forEachIndexed { j, tile ->
            rowHits[i] = rowHits[i] + oneIfTrue(tile == player)
            columnHits[j] = columnHits[j] + oneIfTrue(tile == player)
            diagonalHits += oneIfTrue(tile == player && i == j)
            antiDiagonalHits += oneIfTrue(tile == player && i == (boardSize - 1 - j))
        }
    }

    rowHits.map { rowScore ->
        if (rowScore == boardSize) return true
    }
    columnHits.map { colScore ->
        if (colScore == boardSize) return true
    }
    if (diagonalHits == boardSize) return true
    if (antiDiagonalHits == boardSize) return true

    return false
}

private fun checkMove(xPos: Int, yPos: Int){
    require(xPos < boardSize) { "xPos value must be between 0 and ${boardSize - 1}" }
    require(yPos < boardSize) { "yPos value must be between 0 and ${boardSize - 1}" }
}

private fun checkBoard(brd: List<List<Player>>){
    require(brd.size == boardSize) { "game board must be $boardSize x $boardSize" }
    require(brd[boardSize - 1].size == boardSize) { "game board must be $boardSize x $boardSize" }
}

private fun oneIfTrue(check: Boolean): Int {
    return if (check) 1 else 0
}
