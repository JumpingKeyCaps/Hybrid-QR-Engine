package com.lebaillyapp.hybridqrengine.ui.component

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.lebaillyapp.hybridqrengine.R
import kotlin.random.Random

@Composable
fun StealthQrPointCloud(
    modifier: Modifier = Modifier,
    qrBitmap: Bitmap,
    particleCount: Int,
    isInverted: Boolean = false,
    speedMultiplier: Float = 1f,
    maxParticles: Int = 15000
) {
    val context = LocalContext.current

    val shaderSource = remember {
        context.resources.openRawResource(R.raw.stealth_qr).bufferedReader().use { it.readText() }
    }
    val shader = remember(shaderSource) { RuntimeShader(shaderSource) }

    // Initialisation des particules et de leurs directions (vélocités unitaires)
    val particles = remember {
        FloatArray(maxParticles * 2) { (Math.random().toFloat() * 1000f) }
    }
    val velocities = remember {
        FloatArray(maxParticles * 2) { (Math.random().toFloat() - 0.5f) }
    }

    // Transition pour forcer le refresh du Canvas à chaque frame
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    Box(modifier = modifier.background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val _refresh = animProgress

            val canvasW = size.width
            val canvasH = size.height
            val moduleSize = canvasW / 21f

            // Update positions CPU : Direction * Vitesse
            for (i in 0 until particleCount) {
                particles[i * 2] = (particles[i * 2] + (velocities[i * 2] * speedMultiplier) + canvasW) % canvasW
                particles[i * 2 + 1] = (particles[i * 2 + 1] + (velocities[i * 2 + 1] * speedMultiplier) + canvasH) % canvasH
            }

            // Envoi des paramètres au Shader
            shader.setFloatUniform("uModuleSize", moduleSize)
            shader.setFloatUniform("uOffset", 0f, 0f)
            shader.setFloatUniform("uInvert", if (isInverted) 1.0f else 0.0f)
            shader.setInputShader(
                "uQrMatrix",
                BitmapShader(qrBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            )

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    this.shader = shader
                    this.strokeWidth = 6f
                    this.strokeCap = Paint.Cap.ROUND
                    this.isAntiAlias = true
                }
                // On dessine uniquement le nombre de points actifs
                canvas.nativeCanvas.drawPoints(particles, 0, particleCount * 2, paint)
            }
        }
    }
}

/**
 * Génère une structure de QR Code Version 1 (21x21) réaliste
 */
fun generateRealisticQrMock(): IntArray {
    val size = 21
    val matrix = IntArray(size * size) { 0 }

    fun set(x: Int, y: Int, v: Int) {
        if (x in 0 until size && y in 0 until size) matrix[y * size + x] = v
    }

    // 1. FINDER PATTERNS
    fun drawFinder(ox: Int, oy: Int) {
        for (y in 0..6) {
            for (x in 0..6) {
                val isBorder = x == 0 || x == 6 || y == 0 || y == 6
                val isCenter = x in 2..4 && y in 2..4
                set(ox + x, oy + y, if (isBorder || isCenter) 1 else 0)
            }
        }
        for (i in -1..7) {
            set(ox + i, oy - 1, 0); set(ox + i, oy + 7, 0)
            set(ox - 1, oy + i, 0); set(ox + 7, oy + i, 0)
        }
    }

    drawFinder(0, 0)
    drawFinder(14, 0)
    drawFinder(0, 14)

    // 2. TIMING PATTERNS
    for (i in 8..12) {
        set(i, 6, if (i % 2 == 0) 1 else 0)
        set(6, i, if (i % 2 == 0) 1 else 0)
    }

    // 3. FORMAT & DATA RANDOM
    for (y in 0 until size) {
        for (x in 0 until size) {
            val isReserved = (x < 9 && y < 9) || (x > 11 && y < 9) || (x < 9 && y > 11)
            if (!isReserved && x != 6 && y != 6) {
                set(x, y, if (Random.nextBoolean()) 1 else 0)
            }
        }
    }
    return matrix
}