package com.ryoryo.seichicompass.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeichiInfo(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("info_source") val infoSource: String,
    val coordinate: Coordinate
)