package com.threewin.tictactoe.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val boardSize: Int = 3,
    val board: List<Player?> = List(boardSize * boardSize) { null },
    val currentTurn: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val winningLine: List<Int>? = null,
    val moveHistory: List<Int> = emptyList()
) : Parcelable
