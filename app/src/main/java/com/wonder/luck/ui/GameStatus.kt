package com.wonder.luck.ui

sealed interface GameStatus {
    object Pause:GameStatus
    object Play:GameStatus
}