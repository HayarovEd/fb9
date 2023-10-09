package com.wonder.luck.domain

interface Keeper {
    fun getSharedUrl(): String?
    fun setSharedUrl(url:String)
    fun getSharedTo(): Boolean
    fun setSharedTo(to:Boolean)
    fun getBestScore():Int?
    fun setBestScore(bestScore: Int)
}