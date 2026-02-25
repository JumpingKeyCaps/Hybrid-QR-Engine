package com.lebaillyapp.hybridqrengine.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lebaillyapp.hybridqrengine.model.GhostShaderConfig
import com.lebaillyapp.hybridqrengine.ui.component.*
import com.lebaillyapp.hybridqrengine.ui.component.settingsPan.GhostSettingsPanel

@Composable
fun GhostMedallionScreen() {
    // Note: generateRealisticQrMock() doit être accessible dans ce scope
    var currentQrMatrix by remember { mutableStateOf(generateRealisticQrMock()) }
    var particleCount by remember { mutableStateOf(4000f) }
    var particleMax by remember { mutableStateOf(10000f) }
    var speed by remember { mutableStateOf(3f) }
    var isInverted by remember { mutableStateOf(false) }
    var config by remember { mutableStateOf(GhostShaderConfig()) }

    val qrBitmap = remember(currentQrMatrix) {
        Bitmap.createBitmap(21, 21, Bitmap.Config.ARGB_8888).apply {
            currentQrMatrix.forEachIndexed { i, value ->
                setPixel(i % 21, i / 21, if (value == 1) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        // AJOUT : fillMaxWidth() ici pour que l'alignement horizontal fonctionne sur tout l'écran
        Column(
            modifier = Modifier.fillMaxSize().fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))

            Text("RAW MATRIX", color = Color.DarkGray, fontSize = 9.sp)
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                filterQuality = FilterQuality.None
            )


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GhostMedallionQr(
                    modifier = Modifier.size(350.dp),
                    qrBitmap = qrBitmap,
                    particleCount = particleCount.toInt(),
                    isInverted = isInverted,
                    speedMultiplier = speed,
                    qrInnerSize = config.innerSize,
                    dataBrightness = config.dataBrightness,
                    bgBrightness = config.bgBrightness,
                    dataAlpha = config.dataAlpha,
                    bgAlpha = config.bgAlpha,
                    shapeSoftness = config.shapeSoftness,
                    contrastBoost = config.contrastBoost
                )
            }

            // On ajuste le spacer du bas pour qu'il ne pousse pas trop
            Spacer(Modifier.height(280.dp))
        }

        // Le panneau de contrôle est déjà bien calé en bas
        Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)) {
            GhostSettingsPanel(
                config = config,
                onConfigChange = { config = it },
                particleCount = particleCount,
                onParticleChange = { particleCount = it },
                speed = speed,
                onSpeedChange = { speed = it },
                isInverted = isInverted,
                onInvertChange = { isInverted = it },
                onRegen = { currentQrMatrix = generateRealisticQrMock() },
                particleMax = particleMax
            )
        }
    }
}