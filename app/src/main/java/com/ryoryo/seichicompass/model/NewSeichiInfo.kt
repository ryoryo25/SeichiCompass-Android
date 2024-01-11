package com.ryoryo.seichicompass.model

import kotlinx.serialization.Serializable

@Serializable
data class NewSeichiInfo(
    val title: String,
    val description: String,
    val infoSource: String,
    val coordinateUrl: String
)