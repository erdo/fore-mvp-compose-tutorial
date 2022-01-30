package foo.bar.compose.ui.tictactoe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import co.early.fore.core.ui.SyncableView
import co.early.fore.kt.core.ui.LifecycleObserver
import co.early.fore.kt.core.ui.showOrGone
import co.early.fore.kt.core.ui.showOrInvisible
import foo.bar.compose.OG
import foo.bar.compose.R
import foo.bar.compose.databinding.ActivityTictactoeBinding
import foo.bar.compose.feature.tictactoe.Game
import foo.bar.compose.message.ErrorMsg
import foo.bar.compose.ui.tictactoe.common.ButtonProcessor.processButtons

class TicTacToeActivity : FragmentActivity(R.layout.activity_tictactoe), SyncableView {

    //models we are interested in
    private val game: Game = OG[Game::class.java]

    private lateinit var vb: ActivityTictactoeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // looks like kotlin-android-extensions will finally need
        // to be abandoned when using compose with kotlin 1.6
        // https://issuetracker.google.com/issues/211475860
        vb = ActivityTictactoeBinding.inflate(layoutInflater)

        lifecycle.addObserver(LifecycleObserver(this, game))

        setupClickListeners()

        setContentView(vb.root)
    }

    private fun setupClickListeners() {
        vb.tttRestartBtn.setOnClickListener { game.newGame() }
        vb.tttRetryAutoPlayerBtn.setOnClickListener { game.retryAutoPlayer() }
        processButtons(vb.tttBoardGridLayout) { button: Button, xPos: Int, yPos: Int ->
            button.setOnClickListener { game.play(xPos, yPos) }
        }
    }

    override fun syncView() {

        game.state.apply {

            vb.tttWinText.setText(winner.winMessageRes)
            vb.tttWinText.showOrInvisible(gameFinished())
            vb.tttErrorText.setText(error.msgRes)
            vb.tttErrorText.showOrGone(error != ErrorMsg.NoError)
            vb.tttRetryAutoPlayerBtn.showOrGone(error != ErrorMsg.NoError)
            vb.tttRestartBtn.isEnabled = !isLoading
            vb.tttNextPlayerText.text = whoseTurn().label + " "
            vb.tttNextPlayerContainer.showOrInvisible(!gameFinished())
            vb.tttBusyProg.showOrInvisible(isLoading)
            vb.tttBoardShade.showOrInvisible(isLoading || error != ErrorMsg.NoError)

            processButtons(vb.tttBoardGridLayout) { button: Button, xPos: Int, yPos: Int ->
                board[xPos][yPos].let {
                    button.text = it.label
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, TicTacToeActivity::class.java)
            context.startActivity(intent)
        }
    }
}
