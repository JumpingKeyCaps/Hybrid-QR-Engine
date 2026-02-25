package com.lebaillyapp.hybridqrengine.model

data class GhostShaderConfig(
    val innerSize: Float = 0.65f,
    val dataBrightness: Float = 0.95f,
    val bgBrightness: Float = 0.22f,
    val dataAlpha: Float = 0.98f,
    val bgAlpha: Float = 0.06f,
    val shapeSoftness: Float = 0.45f,
    val contrastBoost: Float = 1.1f
)