package com.example.jogodavelha

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var buttons: Array<Array<Button>>
    private lateinit var tvStatus: TextView
    private lateinit var tvScoreX: TextView
    private lateinit var tvScoreO: TextView
    private lateinit var tvScoreDraws: TextView
    private lateinit var btnReset: Button

    private var activePlayer = "X"
    private var isGameActive = true
    private var board = Array(3) { Array(3) { "" } }


    private var scoreX = 0
    private var scoreO = 0
    private var scoreDraws = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupBoard()

        btnReset.setOnClickListener {
            resetBoard()
        }
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        tvScoreX = findViewById(R.id.tvScoreX)
        tvScoreO = findViewById(R.id.tvScoreO)
        tvScoreDraws = findViewById(R.id.tvScoreDraws)
        btnReset = findViewById(R.id.btnReset)
        buttons = arrayOf(
            arrayOf(findViewById(R.id.btn00), findViewById(R.id.btn01), findViewById(R.id.btn02)),
            arrayOf(findViewById(R.id.btn10), findViewById(R.id.btn11), findViewById(R.id.btn12)),
            arrayOf(findViewById(R.id.btn20), findViewById(R.id.btn21), findViewById(R.id.btn22))
        )
    }

    private fun setupBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].setOnClickListener {
                    if (isGameActive && board[i][j].isEmpty()) {
                        makeMove(i, j)
                    }
                }
            }
        }
    }

    private fun makeMove(row: Int, col: Int) {
        board[row][col] = activePlayer
        buttons[row][col].text = activePlayer

        if (activePlayer == "X") {
            buttons[row][col].setTextColor(Color.parseColor("#D32F2F"))
        } else {
            buttons[row][col].setTextColor(Color.parseColor("#1976D2"))
        }

        val winningCombination = checkWin()
        if (winningCombination != null) {
            highlightWinningCells(winningCombination)
            tvStatus.text = "Jogador $activePlayer Venceu!"
            isGameActive = false
            updateScore(activePlayer)
        } else if (isBoardFull()) {
            tvStatus.text = "Empate!"
            isGameActive = false
            updateScore("DRAW")
        } else {
            activePlayer = if (activePlayer == "X") "O" else "X"
            tvStatus.text = "Turno do Jogador: $activePlayer"
        }
    }

    private fun checkWin(): List<Pair<Int, Int>>? {
            for (i in 0..2) {
            if (board[i][0].isNotEmpty() && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return listOf(Pair(i, 0), Pair(i, 1), Pair(i, 2))
            }
        }

        for (j in 0..2) {
            if (board[0][j].isNotEmpty() && board[0][j] == board[1][j] && board[0][j] == board[2][j]) {
                return listOf(Pair(0, j), Pair(1, j), Pair(2, j))
            }
        }

        if (board[0][0].isNotEmpty() && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
        }
            if (board[0][2].isNotEmpty() && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            return listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
        }
        return null
    }

    private fun highlightWinningCells(winningCells: List<Pair<Int, Int>>) {
        for ((row, col) in winningCells) {
            buttons[row][col].backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C8E6C9"))
        }
    }

    private fun isBoardFull(): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) return false
            }
        }
        return true
    }

    private fun updateScore(result: String) {
        when (result) {
            "X" -> {
                scoreX++
                tvScoreX.text = "Jogador X: $scoreX"
            }
            "O" -> {
                scoreO++
                tvScoreO.text = "Jogador O: $scoreO"
            }
            "DRAW" -> {
                scoreDraws++
                tvScoreDraws.text = "Empates: $scoreDraws"
            }
        }
    }

    private fun resetBoard() {
        board = Array(3) { Array(3) { "" } }
        isGameActive = true
        activePlayer = "X"
        tvStatus.text = "Turno do Jogador: X"

        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].text = ""
                buttons[i][j].backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            }
        }
    }
}