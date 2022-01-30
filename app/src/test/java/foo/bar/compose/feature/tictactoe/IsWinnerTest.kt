package foo.bar.compose.feature.tictactoe

import foo.bar.compose.feature.tictactoe.Player.*
import junit.framework.Assert.assertEquals
import org.junit.Test

typealias N = Nobody

class IsWinnerTest {

    @Test
    fun `when empty board, no winners`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(N, N, N),
            listOf(N, N, N),
            listOf(N, N, N)
        )

        // act

        // assert
        assertEquals(false, isWinner(Nobody, board))
        assertEquals(false, isWinner(X, board))
        assertEquals(false, isWinner(O, board))
    }

    @Test
    fun `when board column, x wins, o looses`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(X, O, N),
            listOf(X, O, O),
            listOf(X, N, X)
        )

        // act

        // assert
        assertEquals(false, isWinner(N, board))
        assertEquals(true, isWinner(X, board))
        assertEquals(false, isWinner(O, board))
    }

    @Test
    fun `when board row, x wins, o looses`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(X, X, X),
            listOf(O, O, N),
            listOf(O, N, X)
        )

        // act

        // assert
        assertEquals(false, isWinner(N, board))
        assertEquals(true, isWinner(X, board))
        assertEquals(false, isWinner(O, board))
    }

    @Test
    fun `when board anti-diagonal, x wins, o looses`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(O, O, X),
            listOf(O, X, N),
            listOf(X, N, X)
        )

        // act

        // assert
        assertEquals(false, isWinner(N, board))
        assertEquals(true, isWinner(X, board))
        assertEquals(false, isWinner(O, board))
    }

    @Test
    fun `when board 1, x looses, o looses`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(O, O, X),
            listOf(X, X, O),
            listOf(O, O, X)
        )

        // act

        // assert
        assertEquals(false, isWinner(N, board))
        assertEquals(false, isWinner(X, board))
        assertEquals(false, isWinner(O, board))
    }

    @Test
    fun `when board 2, x looses, o wins`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(O, O, O),
            listOf(X, X, O),
            listOf(O, O, X)
        )

        // act

        // assert
        assertEquals(false, isWinner(N, board))
        assertEquals(false, isWinner(X, board))
        assertEquals(true, isWinner(O, board))
    }

    @Test
    fun `when board 3, x looses, o wins`() {

        // arrange
        val board: List<List<Player>> = listOf(
            listOf(O, O, O),
            listOf(X, X, O),
            listOf(O, O, O)
        )

        // act

        // assert
        assertEquals(false, isWinner(N, board))
        assertEquals(false, isWinner(X, board))
        assertEquals(true, isWinner(O, board))
    }
}
