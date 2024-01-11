package com.ryoryo.seichicompass.lib

import android.Manifest

object Permissions {
    const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    val LOCATIONS = arrayOf(COARSE_LOCATION, FINE_LOCATION)
}