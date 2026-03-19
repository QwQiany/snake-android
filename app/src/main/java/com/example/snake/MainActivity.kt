package com.example.snake

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snake.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var highScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        highScore = getPreferences(Context.MODE_PRIVATE).getInt(KEY_HIGH_SCORE, 0)

        binding.gameView.setGameEventListener(object : SnakeGameView.GameEventListener {
            override fun onScoreChanged(score: Int) {
                updateScore(score)
            }

            override fun onStatusChanged(status: String) {
                binding.textStatusValue.text = status
                syncPauseButton()
            }

            override fun onGameOver(score: Int) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.game_over_message, score),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.buttonUp.setOnClickListener { binding.gameView.queueDirection(Direction.UP) }
        binding.buttonLeft.setOnClickListener { binding.gameView.queueDirection(Direction.LEFT) }
        binding.buttonDown.setOnClickListener { binding.gameView.queueDirection(Direction.DOWN) }
        binding.buttonRight.setOnClickListener { binding.gameView.queueDirection(Direction.RIGHT) }

        binding.buttonPause.setOnClickListener {
            if (binding.gameView.isRunning()) {
                binding.gameView.pauseGame()
            } else {
                binding.gameView.resumeGame()
            }
            syncPauseButton()
        }

        binding.buttonRestart.setOnClickListener {
            binding.gameView.restartGame()
            syncPauseButton()
        }

        binding.textBestValue.text = highScore.toString()
        binding.textStatusValue.text = getString(R.string.status_ready)
        syncPauseButton()
    }

    override fun onResume() {
        super.onResume()
        binding.gameView.resumeGame(fromUser = false)
        syncPauseButton()
    }

    override fun onPause() {
        binding.gameView.pauseGame(fromUser = false)
        super.onPause()
        syncPauseButton()
    }

    private fun updateScore(score: Int) {
        binding.textScoreValue.text = score.toString()
        if (score > highScore) {
            highScore = score
            getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_HIGH_SCORE, highScore)
                .apply()
        }
        binding.textBestValue.text = highScore.toString()
    }

    private fun syncPauseButton() {
        binding.buttonPause.text = getString(
            if (binding.gameView.isRunning()) {
                R.string.pause
            } else {
                R.string.resume
            }
        )
    }

    companion object {
        private const val KEY_HIGH_SCORE = "high_score"
    }
}
