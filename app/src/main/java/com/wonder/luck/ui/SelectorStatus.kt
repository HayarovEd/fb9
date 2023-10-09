package com.wonder.luck.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.wonder.luck.ui.ErrorScreen
import com.wonder.luck.ui.LoadingScreen
import com.wonder.luck.ui.WebScreen

@Composable
fun SelectorStatus (
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    when (val result = state.value.status) {
        ApplicationStatus.Loading -> {
            LoadingScreen()
        }
        ApplicationStatus.Mock -> {
            ContentGame(
                gameStatus = state.value.gameStatus,
                score = state.value.score,
                bestScore = state.value.bestScore,
                image = state.value.single,
                alfa = state.value.alfa,
                leftTime = state.value.leftTime,
                onEvent = viewModel::onEvent
            )
        }
        is ApplicationStatus.Succsess -> {
            WebScreen(
                url = result.url
            )
        }

        is ApplicationStatus.Error -> {
            ErrorScreen(error = result.error)
        }
    }
}