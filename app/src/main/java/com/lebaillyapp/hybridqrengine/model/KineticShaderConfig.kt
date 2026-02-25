package com.lebaillyapp.hybridqrengine.model

data class KineticShaderConfig(
    val innerSize: Float = 0.85f,
    val hue1: Float = 180f,
    val hue2: Float = 300f,
    val bgColorAlpha: Float = 0.25f,
    val particleSizeMin: Float = 5.5f,
    val particleSizeMax: Float = 6.0f,
    val staseEffect: Float = 0.20f
)