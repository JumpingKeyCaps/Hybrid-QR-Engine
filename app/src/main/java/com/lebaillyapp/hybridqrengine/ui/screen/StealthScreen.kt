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
import androidx.compose.ui.unit.sp
import com.lebaillyapp.hybridqrengine.ui.component.*
import com.lebaillyapp.hybridqrengine.ui.component.settingsPan.StealthSettingsPanel

@Composable
fun StealthScreen() {
    var currentQrMatrix by remember { mutableStateOf(generateRealisticQrMock()) }
    var particleSliderValue by remember { mutableStateOf(4000f) }
    var speedSliderValue by remember { mutableStateOf(3f) }
    var isInverted by remember { mutableStateOf(false) }

    val maxParticuleAllowed = 10000f

    val qrBitmap = remember(currentQrMatrix) {
        Bitmap.createBitmap(21, 21, Bitmap.Config.ARGB_8888).apply {
            currentQrMatrix.forEachIndexed { i, value ->
                setPixel(i % 21, i / 21, if (value == 1) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color(0xFF121212))) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(30.dp))

                Text("DEBUG", color = Color.Gray, fontSize = 9.sp)
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    filterQuality = FilterQuality.None
                )

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    StealthQrPointCloud(
                        qrBitmap = qrBitmap,
                        particleCount = particleSliderValue.toInt(),
                        isInverted = isInverted,
                        speedMultiplier = speedSliderValue,
                        maxParticles = maxParticuleAllowed.toInt(),
                        modifier = Modifier.size(260.dp)
                    )
                }
                Spacer(Modifier.height(220.dp))
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                StealthSettingsPanel(
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