package com.lebaillyapp.hybridqrengine.ui.component

import android.graphics.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.lebaillyapp.hybridqrengine.R

@Composable
fun GhostMedallionQr(
    modifier: Modifier = Modifier,
    qrBitmap: Bitmap,
    particleCount: Int,
    isInverted: Boolean = false,
    speedMultiplier: Float = 1f,
    qrInnerSize: Float = 0.75f, // Pilotable par slider maintenant
    dataBrightness: Float = 0.95f,
    bgBrightness: Float = 0.22f,
    dataAlpha: Float = 0.98f,
    bgAlpha: Float = 0.06f,
    shapeSoftness: Float = 0.45f,
    contrastBoost: Float = 1.1f,
    maxParticles: Int = 10000
) {
    val context = LocalContext.current
    val shaderSource = remember {
        context.resources.openRawResource(R.raw.medallion_ghost).bufferedReader().use { it.readText() }
    }
    val shader = remember(shaderSource) { RuntimeShader(shaderSource) }

    val particles = remember { FloatArray(maxParticles * 2) { Math.random().toFloat() * 1200f } }
    val velocities = remember { FloatArray(maxParticles * 2) { (Math.random().toFloat() - 0.5f) } }
    val sizes = remember { FloatArray(maxParticles) { (2f + Math.random().toFloat() * 6f) } }

    val infiniteTransition = rememberInfiniteTransition(label = "hublot")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    Box(modifier = modifier.clip(CircleShape).background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val _tick = animProgress
            val w = size.width
            val h = size.height
            val centerX = w / 2f
            val centerY = h / 2f

            // Calcul de la structure
            val qrVisualSize = w * qrInnerSize.coerceIn(0.1f, 0.95f)
            val moduleSize = qrVisualSize / 21f
            val qrTopLeftX = centerX - (qrVisualSize / 2f)
            val qrTopLeftY = centerY - (qrVisualSize / 2f)

            // Injection des UNIFORMS de réglages
            shader.setFloatUniform("uDataBrightness", dataBrightness)
            shader.setFloatUniform("uBgBrightness", bgBrightness)
            shader.setFloatUniform("uDataAlpha", dataAlpha)
            shader.setFloatUniform("uBgAlpha", bgAlpha)
            shader.setFloatUniform("uShapeSoftness", shapeSoftness)
            shader.setFloatUniform("uContrastBoost", contrastBoost)

            // Injection des UNIFORMS de structure
            shader.setFloatUniform("uModuleSize", moduleSize)
            shader.setFloatUniform("uOffset", centerX, centerY)
            shader.setFloatUniform("uRadius", w / 2f)
            shader.setFloatUniform("uInvert", if (isInverted) 1.0f else 0.0f)
            shader.setInputShader("uQrMatrix", BitmapShader(qrBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    this.shader = shader
                    this.strokeCap = Paint.Cap.ROUND
                }

                for (i in 0 until particleCount) {
                    var px = (particles[i * 2] + w) % w
                    var py = (particles[i * 2 + 1] + h) % h

                    val relX = px - qrTopLeftX
                    val relY = py - qrTopLeftY
                    var resistance = 1.0f

                    // Physique calée sur qrInnerSize
                    if (relX in 0f..qrVisualSize && relY in 0f..qrVisualSize) {
                        val mx = (relX / moduleSize).toInt().coerceIn(0, 20)
                        val my = (relY / moduleSize).toInt().coerceIn(0, 20)
                        val pixel = qrBitmap.getPixel(mx, my)
                        if (if (!isInverted) pixel == -16777216 else pixel == -1) {
                            resistance = 0.25f
                        }
                    }

                    px += velocities[i * 2] * speedMultiplier * resistance
                    py += velocities[i * 2 + 1] * speedMultiplier * resistance

                    particles[i * 2] = px
                    particles[i * 2 + 1] = py
                    paint.strokeWidth = sizes[i]
                    canvas.nativeCanvas.drawPoint(px % w, py % h, paint)
                }
            }
        }
    }
}