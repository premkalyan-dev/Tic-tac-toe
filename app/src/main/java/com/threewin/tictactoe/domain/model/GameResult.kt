package com.threewin.tictactoe.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GameMode : Parcelable {
    VS_FRIEND, VS_COMPUTER
}

@Parcelize
data class GameResult(
    val winner: Player?,
    val isDraw: Boolean,
    val totalMoves: Int,
    val timeTakenSeconds: Long,
    val difficulty: DifficultyLevel?,
    val gameMode: GameMode
) : Parcelable
