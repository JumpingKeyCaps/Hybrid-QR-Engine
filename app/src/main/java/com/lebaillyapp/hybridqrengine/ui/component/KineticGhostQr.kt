package com.lebaillyapp.hybridqrengine.ui.component

import android.graphics.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.lebaillyapp.hybridqrengine.model.KineticShaderConfig

@Composable
fun KineticGhostQr(
    modifier: Modifier = Modifier,
    qrBitmap: Bitmap,
    particleCount: Int,
    isInverted: Boolean = false,
    speedMultiplier: Float = 1f,
    config: KineticShaderConfig = KineticShaderConfig(),
    maxParticles: Int = 15000
) {
    val particles = remember { FloatArray(maxParticles * 2) { Math.random().toFloat() * 2000f } }
    val velocities = remember { FloatArray(maxParticles * 2) { (Math.random().toFloat() - 0.5f) } }
    val baseSizes = remember { FloatArray(maxParticles) { Math.random().toFloat() } }
    val particleType = remember { IntArray(maxParticles) { if (Math.random() > 0.5) 1 else 0 } }

    val infiniteTransition = rememberInfiniteTransition(label = "kinetic")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    Box(modifier = modifier.background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val _tick = animProgress
            val (w, h) = size.width to size.height
            val qrVisualSize = w * config.innerSize
            val moduleSize = qrVisualSize / 21f
            val ox = (w - qrVisualSize) / 2f
            val oy = (h - qrVisualSize) / 2f

            val colorA = Color.hsv(config.hue1, 0.8f, 1f).toArgb()
            val colorB = Color.hsv(config.hue2, 0.8f, 1f).toArgb()
            // Couleur des particules "fond" : Gris clair avec l'alpha de la config
            val colorBG = Color.White.copy(alpha = config.bgColorAlpha).toArgb()

            drawIntoCanvas { canvas ->
                val paint = Paint().apply { isAntiAlias = false }

                for (i in 0 until particleCount.coerceAtMost(maxParticles)) {
                    var x = (particles[i * 2] + w) % w
                    var y = (particles[i * 2 + 1] + h) % h

                    val rx = x - ox
                    val ry = y - oy
                    var isDataZone = false

                    if (rx in 0f..qrVisualSize && ry in 0f..qrVisualSize) {
                        val mx = (rx / moduleSize).toInt().coerceIn(0, 20)
                        val my = (ry / moduleSize).toInt().coerceIn(0, 20)
                        val pixel = qrBitmap.getPixel(mx, my)
                        isDataZone = if (!isInverted) (pixel == -16777216) else (pixel == -1)
                    }

                    val resistance = if (isDataZone) config.staseEffect else 1.0f
                    x = (x + (velocities[i * 2] * speedMultiplier * resistance) + w) % w
                    y = (y + (velocities[i * 2 + 1] * speedMultiplier * resistance) + h) % h
                    particles[i * 2] = x
                    particles[i * 2 + 1] = y

                    if (isDataZone) {
                        paint.color = if (particleType[i] == 1) colorA else colorB
                        paint.strokeWidth = config.particleSizeMax * (0.7f + baseSizes[i] * 0.6f)
                    } else {
                        paint.color = colorBG
                        paint.strokeWidth = config.particleSizeMin
                    }
                    canvas.nativeCanvas.drawPoint(x, y, paint)
                }
            }
        }
    }
}