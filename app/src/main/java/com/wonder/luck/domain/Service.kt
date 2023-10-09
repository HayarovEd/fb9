package com.wonder.luck.domain

interface Service {
    fun vpnActive(): Boolean
    fun batteryLevel(): Int
    fun checkIsEmu(): Boolean
}