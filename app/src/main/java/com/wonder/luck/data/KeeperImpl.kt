package com.wonder.luck.data

import android.app.Application
import android.content.Context
import com.wonder.luck.domain.Keeper
import javax.inject.Inject

class KeeperImpl @Inject constructor(
    application: Application
): Keeper {
    private val sharedPref =
        application.getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE)

    override fun getSharedUrl(): String? = sharedPref.getString(URL_SETTINGS, "")

    override fun setSharedUrl(url:String) {
        sharedPref.edit().putString(URL_SETTINGS, url).apply()
    }

    override fun getSharedTo(): Boolean = sharedPref.getBoolean(TO_SETTINGS, false)

    override fun setSharedTo(to:Boolean) {
        sharedPref.edit().putBoolean(TO_SETTINGS, to).apply()
    }

    override fun getBestScore(): Int {
        return sharedPref.getInt(BEST_SCORE, 0)
    }

    override fun setBestScore(bestScore: Int) {
        sharedPref.edit().putInt(BEST_SCORE, bestScore).apply()
    }
}