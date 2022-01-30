package foo.bar.compose.ui.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DimenRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.TextUnit
import co.early.fore.kt.core.ui.observeAsState
import foo.bar.compose.OG
import foo.bar.compose.R
import foo.bar.compose.feature.tictactoe.Game
import foo.bar.compose.feature.tictactoe.GameState
import foo.bar.compose.feature.tictactoe.Player
import foo.bar.compose.message.ErrorMsg
import foo.bar.compose.ui.tictactoe.common.AppTheme
import foo.bar.compose.ui.tictactoe.common.colorPastels

class TicTacToeActivityCompose : ComponentActivity() {

    //models we are interested in
    private val game: Game = OG[Game::class.java]

    fun startNonComposeActivity() {
        TicTacToeActivity.start(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                GameScreen(game) { startNonComposeActivity() }
            }
        }
    }
}

@Composable
fun GameScreen(
    game: Game,
    modifier: Modifier = Modifier,
    switchToNonComposeUI: () -> Unit
) {

    val viewState by game.observeAsState { game.state }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.common_space_small))
            .then(modifier),
    ) {

        HeaderLayout(viewState, retry = { game.retryAutoPlayer() }, Modifier.align(TopCenter))

        BoardLayout(
            viewState.isLoading,
            viewState.error,
            viewState.board,
            Modifier.align(Center)
        ) { x, y ->
            game.play(x, y)
        }

        Column(
            modifier = Modifier
                .wrapContentSize()
                .align(BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .wrapContentSize(),
                onClick = { game.newGame() },
                enabled = !viewState.isLoading
            ) {
                Text(stringResource(id = R.string.reset))
            }
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.common_space_small)))
            Button(
                modifier = Modifier
                    .wrapContentSize(),
                onClick = { switchToNonComposeUI() },
            ) {
                Text(stringResource(id = R.string.non_compose_ui))
            }
        }
    }
}

@Composable
fun HeaderLayout(
    viewState: GameState,
    retry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .then(modifier),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (viewState.gameFinished()) {
            Text(
                text = stringResource(viewState.winner.winMessageRes),
                color = colorResource(id = R.color.colorTextTitle),
                fontSize = fontSizeResource(id = R.dimen.common_textsize_extra_large),
                fontWeight = Bold
            )
        } else {
            Text(
                text = "${viewState.whoseTurn().label} ${stringResource(id = R.string.to_play)}",
                color = colorResource(id = R.color.colorTextTitle),
                fontSize = fontSizeResource(id = R.dimen.common_textsize_extra_large)
            )
        }

        if (viewState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(dimensionResource(id = R.dimen.common_space_small)),
            )
        }

        if (viewState.error != ErrorMsg.NoError) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = stringResource(viewState.error.msgRes),
                color = colorResource(R.color.colorWarning),
                fontSize = fontSizeResource(id = R.dimen.common_textsize_extra_large)
            )
            Button(
                modifier = Modifier.wrapContentSize(),
                onClick = { retry() }
            ) {
                Text(stringResource(id = R.string.retry_autoplayer))
            }
        }
    }
}

@Composable
fun BoardLayout(
    isLoading: Boolean,
    error: ErrorMsg?,
    board: List<List<Player>>,
    modifier: Modifier = Modifier,
    tileClick: (x: Int, y: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .then(modifier)
    ) {
        var colorIndex = 0
        for (y in board.size - 1 downTo 0) {
            Row {
                for (x in board.indices) {
                    if (isLoading || error != ErrorMsg.NoError) {
                        GameSquare(colorPastels[colorIndex++ % colorPastels.size], board[x][y]) { }
                    } else {
                        GameSquare(colorPastels[colorIndex++ % colorPastels.size], board[x][y]) {
                            tileClick(x, y)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameSquare(color: Color, player: Player, clicked: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.common_space_medium))
            .width(dimensionResource(id = R.dimen.common_space_medium))
            .padding(dimensionResource(id = R.dimen.common_space_small))
            .background(color, shape = RectangleShape)
            .clickable { clicked() },
        contentAlignment = Center
    ) {
        Text(
            style = TextStyle(fontSize = fontSizeResource(R.dimen.common_textsize_large)),
            text = player.label
        )
    }
}

@Composable
@ReadOnlyComposable
fun fontSizeResource(@DimenRes id: Int): TextUnit {
    return with(LocalDensity.current) {
        dimensionResource(id = id).toSp()
    }
}
