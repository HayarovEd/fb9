package com.wonder.luck.ui

sealed class MainEvent {
    object EndGame:MainEvent()
    object StartGame:MainEvent()
    class getAnswer(val chooseInt: Int):MainEvent()
}