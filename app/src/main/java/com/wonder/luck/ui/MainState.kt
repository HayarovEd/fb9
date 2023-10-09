package com.wonder.luck.ui

data class MainState(
    val status: ApplicationStatus = ApplicationStatus.Loading,
    val gameStatus: GameStatus = GameStatus.Pause,
    val score: Int = 0,
    val bestScore: Int = 0,
    val leftTime: Long = 120,
    val alfa: List<Int> = emptyList(),
    val single: Int = 0
)
