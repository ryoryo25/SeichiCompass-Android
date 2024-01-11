package com.ryoryo.seichicompass.model

import android.os.Bundle
import com.ryoryo.seichicompass.lib.Constants

object SeichiInfoHelper {

    const val BUNDLE_SEICHI_INFO = "com.ryoryo.seichicompass.SeichiInfo"

    val DEFAULT_VALUE = SeichiInfo(
        -1, "N/A", "N/A", "N/A", Coordinate(0.0, 0.0)
    )

    fun bundleToSeichiInfo(bundle: Bundle?): SeichiInfo {
        if (bundle != null) {
            return SeichiInfo(
                bundle.getInt("id"),
                bundle.getString("title")!!,
                bundle.getString("description")!!,
                bundle.getString("infoSource")!!,
                Coordinate(
                    bundle.getDouble("latitude"),
                    bundle.getDouble("longitude")
                )
            )
        }
        return DEFAULT_VALUE
    }

    fun seichiInfoToBundle(seichiInfo: SeichiInfo): Bundle {
        val bundle = Bundle()
        bundle.putInt("id", seichiInfo.id)
        bundle.putString("title", seichiInfo.title)
        bundle.putString("description", seichiInfo.description)
        bundle.putString("infoSource", seichiInfo.infoSource)
        bundle.putDouble("latitude", seichiInfo.coordinate.latitude)
        bundle.putDouble("longitude", seichiInfo.coordinate.longitude)
        return bundle
    }
}