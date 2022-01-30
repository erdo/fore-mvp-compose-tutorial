package foo.bar.compose.ui.tictactoe.common

import android.widget.Button
import android.widget.GridLayout
import foo.bar.compose.feature.tictactoe.boardSize

/**
 * some specific stuff related to this tic tac toe view that I wouldn't expect to be relevant
 * to any other situation - we just have loads of buttons and we do the same thing to each of
 * them, so we put it here
 */
object ButtonProcessor {
    fun processButtons(boardLayout: GridLayout, buttonOperation: (button: Button, xPos: Int, yPos: Int) -> Unit) {
        var cellNumber = 0
        for (y in boardSize-1 downTo 0) {
            for (x in 0 until boardSize) {
                buttonOperation(boardLayout.getChildAt(cellNumber++) as Button, x, y)
            }
        }
    }
}