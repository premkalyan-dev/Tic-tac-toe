package com.threewin.tictactoe.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Player : Parcelable {
    X, O
}
