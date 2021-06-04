package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.random.nextInt


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var currentResourceId: Int = 0
    private var isPlaying: Boolean = true
    private var currentlySelectedColor: Int = -1
    private var score = 0
    private var gameOverText = ""
    private var showDialog = true
    private var job: Job? = null
    private var prevValue = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        orangeTile.setOnClickListener(this)
        orangeTile.tag = 0
        blueTile.setOnClickListener(this)
        blueTile.tag = 1
        yellowTile.setOnClickListener(this)
        yellowTile.tag = 2
        greenTile.setOnClickListener(this)
        greenTile.tag = 3
        play_stop_button.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.orangeTile,
            R.id.blueTile,
            R.id.yellowTile,
            R.id.greenTile -> {
                if (v.tag == currentlySelectedColor) {
                    disableTiles()
                    isPlaying = true
                    score++
                    tv_score.text = "Your score : $score"
                } else {
                    job?.cancel()
                    job = null
                    gameOver()
                }
            }
            R.id.play_stop_button -> {
                if (play_stop_button.text == "Play") {
                    showDialog = true
                    showCountdownTimer()
                } else {
                    showDialog = false
                }

            }
        }
    }

    private fun showCountdownTimer() {

        CoroutineScope(Dispatchers.Main).launch {
            disableTiles()
            play_stop_button.isEnabled = false
            var timeLeft = 3000L
            countdownDisplay.visibility = View.VISIBLE
            repeat(3) {
                countdownDisplay.text = (timeLeft / 1000).toString()
                delay(1000)
                timeLeft -= 1000
            }
            enableTiles()
            startGame()
            countdownDisplay.visibility = View.GONE
            play_stop_button.text = "Stop"
            play_stop_button.isEnabled = true

        }
    }

    private fun startGame() {
        isPlaying = false

        currentlySelectedColor = Random.nextInt(0..3)
        while (currentlySelectedColor == prevValue) {
            currentlySelectedColor = Random.nextInt(0..3)
        }
        prevValue = currentlySelectedColor
        when (currentlySelectedColor) {
            0 -> {
                currentResourceId = R.id.orangeTile
                job = makeTileActive(currentResourceId)
            }
            1 -> {
                currentResourceId = R.id.blueTile
                job = makeTileActive(currentResourceId)
            }
            2 -> {
                currentResourceId = R.id.yellowTile
                job = makeTileActive(currentResourceId)
            }
            3 -> {
                currentResourceId = R.id.greenTile
                job = makeTileActive(currentResourceId)
            }
        }


    }

    private fun makeTileActive(tile: Int): Job {

        return CoroutineScope(Dispatchers.Main).launch {
            enableTiles()
            findViewById<View>(tile).setBackgroundColor(resources.getColor(R.color.grey))
            delay(1010)
            resetTilesBgColor(tile)
            if (!isPlaying) {
                gameOver()
            } else {
                startGame()
            }
        }

    }

    private fun resetTilesBgColor(tile: Int) {
        when (findViewById<View>(tile).tag) {
            0 -> findViewById<View>(tile).setBackgroundColor(resources.getColor(R.color.orange))
            1 -> findViewById<View>(tile).setBackgroundColor(resources.getColor(R.color.blue))
            2 -> findViewById<View>(tile).setBackgroundColor(resources.getColor(R.color.yellow))
            3 -> findViewById<View>(tile).setBackgroundColor(resources.getColor(R.color.green))
        }
    }

    private fun gameOver() {
//        Toast.makeText(this@MainActivity, "GAME OVER, Your score : $score", Toast.LENGTH_SHORT).show()
        isPlaying = true
        resetTilesBgColor(currentResourceId)
        currentResourceId = 0
        currentlySelectedColor = -1
        prevValue = -1
        play_stop_button.text = "Play"
        gameOverText = "Your score : $score"
        if (showDialog) showRestartDialog()
        score = 0
        gameOverText = "Your score : $score"
        tv_score.text = gameOverText
        disableTiles()
    }

    private fun disableTiles() {
        orangeTile.isEnabled = false
        blueTile.isEnabled = false
        yellowTile.isEnabled = false
        greenTile.isEnabled = false
    }

    private fun enableTiles() {
        orangeTile.isEnabled = true
        blueTile.isEnabled = true
        yellowTile.isEnabled = true
        greenTile.isEnabled = true
    }

    private fun showRestartDialog() {
        AlertDialog.Builder(this)
            .setTitle("GAME OVER")
            .setMessage(gameOverText)
            .setPositiveButton("Restart") { _, _ ->
                showCountdownTimer()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
