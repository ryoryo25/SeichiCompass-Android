package com.ryoryo.seichicompass.lib

import com.ryoryo.seichicompass.model.Coordinate
import com.ryoryo.seichicompass.model.SeichiInfo

object Constants {
    const val API_BASE_URL = "http://www.ry-tom.mydns.jp:3333/api"
    const val CONVERTER_API = "${API_BASE_URL}/convert"

    const val EXTRA_NAME_SEICHI_INFO = "com.ryoryo.seichicompass.SeichiInfo"
    val DEFAULT_SEICHI_INFO = SeichiInfo(
        -1, "N/A", "N/A", "N/A", Coordinate(0.0, 0.0)
    )
}