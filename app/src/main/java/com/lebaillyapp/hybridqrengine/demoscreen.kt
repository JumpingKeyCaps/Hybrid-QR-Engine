package com.lebaillyapp.hybridqrengine

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RuntimeShader
import android.graphics.Shader
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

@Composable
fun StealthQrPointCloud(modifier: Modifier = Modifier) {
    val qrMatrix = remember { generateRealisticQrMock() }
    val moduleSize = 24f
    val particleCount = 2000

    // On prépare le bitmap du QR
    val qrBitmap = remember(qrMatrix) {
        Bitmap.createBitmap(21, 21, Bitmap.Config.ARGB_8888).apply {
            qrMatrix.forEachIndexed { i, value ->
                val x = i % 21
                val y = i / 21
                setPixel(x, y, if (value == 1) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
    }

    val shader = remember { RuntimeShader(STEALTH_POINT_SHADER) }

    // État des particules : [x0, y0, x1, y1, ...]
    // On utilise un FloatArray pour éviter de créer des milliers d'objets Offset (trop de Garbage Collector)
    val particles = remember {
        FloatArray(particleCount * 2) { Math.random().toFloat() * 800f }
    }

    // Vitesses individuelles pour l'effet organique
    val velocities = remember {
        FloatArray(particleCount * 2) { (Math.random().toFloat() - 0.5f) * 2f }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. UPDATE POSITIONS (CPU)
            // On fait bouger chaque point selon sa vitesse + un petit bruit
            for (i in 0 until particleCount) {
                // X update
                particles[i * 2] += velocities[i * 2]
                // Y update
                particles[i * 2 + 1] += velocities[i * 2 + 1]

                // Screen Wrap (si ça sort d'un côté, ça revient de l'autre)
                if (particles[i * 2] < 0) particles[i * 2] = w
                if (particles[i * 2] > w) particles[i * 2] = 0f
                if (particles[i * 2 + 1] < 0) particles[i * 2 + 1] = h
                if (particles[i * 2 + 1] > h) particles[i * 2 + 1] = 0f
            }

            // 2. SETUP SHADER
            shader.setFloatUniform("uModuleSize", moduleSize)
            shader.setInputShader(
                "uQrMatrix",
                BitmapShader(qrBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            )

            // 3. DRAW
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    this.shader = shader
                    this.strokeWidth = 5f // Taille des points
                    this.strokeCap = android.graphics.Paint.Cap.ROUND
                }

                // On envoie le tableau de 4000 floats (2000 points) d'un coup au GPU
                canvas.nativeCanvas.drawPoints(particles, paint)
            }
        }
    }
}

/**
 * Generates a realistic QR Code Version 1 (21x21) mock
 * Includes proper Finder Patterns, Timing Patterns, and pseudo-random data
 */
fun generateRealisticQrMock(): IntArray {
    val size = 21
    val matrix = IntArray(size * size) { 0 }

    fun set(x: Int, y: Int, value: Int) {
        if (x in 0 until size && y in 0 until size) {
            matrix[y * size + x] = value
        }
    }

    // === FINDER PATTERNS (3 corners) ===
    fun drawFinderPattern(ox: Int, oy: Int) {
        // Outer border (7x7 black square)
        for (y in 0..6) {
            for (x in 0..6) {
                set(ox + x, oy + y, 1)
            }
        }
        // White ring (5x5 inside)
        for (y in 1..5) {
            for (x in 1..5) {
                set(ox + x, oy + y, 0)
            }
        }
        // Black center (3x3)
        for (y in 2..4) {
            for (x in 2..4) {
                set(ox + x, oy + y, 1)
            }
        }

        // White separator (1px border around finder)
        for (i in -1..7) {
            set(ox + i, oy - 1, 0)
            set(ox + i, oy + 7, 0)
            set(ox - 1, oy + i, 0)
            set(ox + 7, oy + i, 0)
        }
    }

    drawFinderPattern(0, 0)           // Top-left
    drawFinderPattern(size - 7, 0)    // Top-right
    drawFinderPattern(0, size - 7)    // Bottom-left

    // === TIMING PATTERNS (alternating black/white) ===
    for (i in 8 until size - 8) {
        set(i, 6, if (i % 2 == 0) 1 else 0)  // Horizontal
        set(6, i, if (i % 2 == 0) 1 else 0)  // Vertical
    }

    // === DARK MODULE (always black at coordinate 8, 4*version+9) ===
    set(8, size - 8, 1)

    // === FORMAT INFORMATION (around finders - fake pattern) ===
    // Top-left horizontal
    for (i in 0..8) {
        if (matrix[8 * size + i] == 0) {
            set(i, 8, if ((i * 7) % 3 == 0) 1 else 0)
        }
    }
    // Top-left vertical
    for (i in 0..8) {
        if (matrix[i * size + 8] == 0) {
            set(8, i, if ((i * 11) % 3 == 0) 1 else 0)
        }
    }

    // === DATA AREA (pseudo-random but consistent) ===
    for (y in 0 until size) {
        for (x in 0 until size) {
            // Skip if already set (finders, timing, format)
            if (matrix[y * size + x] != 0) continue

            // Skip finder regions
            if ((x < 9 && y < 9) ||
                (x >= size - 8 && y < 9) ||
                (x < 9 && y >= size - 8)) {
                continue
            }

            // Generate realistic-looking data pattern
            val hash = (x * 31 + y * 17 + x * y * 3) % 7
            val value = when {
                hash < 3 -> 1  // ~43% black
                else -> 0      // ~57% white
            }
            set(x, y, value)
        }
    }

    return matrix
}







private const val STEALTH_POINT_SHADER = """
uniform float uModuleSize;
uniform shader uQrMatrix;

half4 main(float2 fragCoord) {
    // On calcule la position relative au QR (centré ou non, ici top-left)
    float2 moduleCoord = floor(fragCoord / uModuleSize);
    
    // Si hors limites du QR 21x21
    if (moduleCoord.x < 0.0 || moduleCoord.x >= 21.0 || 
        moduleCoord.y < 0.0 || moduleCoord.y >= 21.0) {
        return half4(0.15, 0.15, 0.15, 0.3); // Particules "fond" très sombres
    }

    // Sampling du QR
    // moduleUV va de 0.0 à 1.0 sur la matrice
    float2 moduleUV = (moduleCoord + 0.5) / 21.0;
    half4 qrSample = uQrMatrix.eval(moduleUV * 21.0);
    
    // Inversion : Si rouge est bas (0.0), c'est un module noir
    float isBlack = 1.0 - qrSample.r;

    if (isBlack > 0.5) {
        return half4(1.0, 1.0, 1.0, 1.0); // Éclat blanc sur noir
    } else {
        return half4(0.3, 0.3, 0.3, 0.4); // Gris discret sur blanc
    }
}
"""