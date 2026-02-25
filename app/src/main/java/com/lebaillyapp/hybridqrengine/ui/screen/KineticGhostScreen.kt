package com.lebaillyapp.hybridqrengine.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.lebaillyapp.hybridqrengine.model.KineticShaderConfig
import com.lebaillyapp.hybridqrengine.ui.component.*
import com.lebaillyapp.hybridqrengine.ui.component.settingsPan.KineticSettingsPanel

@Composable
fun KineticGhostScreen() {
    var currentQrMatrix by remember { mutableStateOf(generateRealisticQrMock()) }
    var particleSliderValue by remember { mutableStateOf(5000f) }
    var speedSliderValue by remember { mutableStateOf(3f) }
    var isInverted by remember { mutableStateOf(false) }

    var config by remember { mutableStateOf(KineticShaderConfig()) }

    val maxParticuleAllowed = 15000f

    val qrBitmap = remember(currentQrMatrix) {
        Bitmap.createBitmap(21, 21, Bitmap.Config.ARGB_8888).apply {
            currentQrMatrix.forEachIndexed { i, value ->
                setPixel(i % 21, i / 21, if (value == 1) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF0A0A0A))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(3.dp))

                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    filterQuality = FilterQuality.None
                )

                Spacer(Modifier.height(30.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    KineticGhostQr(
                        modifier = Modifier.size(320.dp),
                        qrBitmap = qrBitmap,
                        particleCount = particleSliderValue.toInt(),
                        isInverted = isInverted,
                        speedMultiplier = speedSliderValue,
                        config = config,
                        maxParticles = maxParticuleAllowed.toInt()
                    )
                }


            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                KineticSettingsPanel(
                    config = config,
                    onConfigChange = { config = it },
                    particleCount = particleSliderValue,
                    onParticleChange = { particleSliderValue = it },
                    speed = speedSliderValue,
                    onSpeedChange = { speedSliderValue = it },
                    isInverted = isInverted,
                    onInvertChange = { isInverted = it },
                    onRegen = { currentQrMatrix = generateRealisticQrMock() },
                    maxParticles = maxParticuleAllowed
                )
            }
        }
    }
}