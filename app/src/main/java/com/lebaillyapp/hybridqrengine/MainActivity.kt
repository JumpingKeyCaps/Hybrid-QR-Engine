package com.lebaillyapp.hybridqrengine

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lebaillyapp.hybridqrengine.ui.component.StealthQrPointCloud
import com.lebaillyapp.hybridqrengine.ui.component.generateRealisticQrMock
import com.lebaillyapp.hybridqrengine.ui.theme.HybridQrEngineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HybridQrEngineTheme {
                var currentQrMatrix by remember { mutableStateOf(generateRealisticQrMock()) }
                var particleSliderValue by remember { mutableStateOf(4000f) }
                var speedSliderValue by remember { mutableStateOf(3f) }
                var isInverted by remember { mutableStateOf(false) }
                val maxParticuleAllowed = 10000

                val qrBitmap = remember(currentQrMatrix) {
                    Bitmap.createBitmap(21, 21, Bitmap.Config.ARGB_8888).apply {
                        currentQrMatrix.forEachIndexed { i, value ->
                            val x = i % 21
                            val y = i / 21
                            setPixel(x, y, if (value == 1) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(Color(0xFF121212))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(30.dp))

                            // 1. DEBUG VIEW (Plus petite)
                            Text("DEBUG", color = Color.Gray, fontSize = 9.sp)
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                filterQuality = androidx.compose.ui.graphics.FilterQuality.None
                            )

                            // 2. SHADER VIEW
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                StealthQrPointCloud(
                                    qrBitmap = qrBitmap,
                                    particleCount = particleSliderValue.toInt(),
                                    isInverted = isInverted,
                                    speedMultiplier = speedSliderValue,
                                    maxParticles = maxParticuleAllowed,
                                    modifier = Modifier.size(260.dp)
                                )
                            }

                            // On laisse juste la place pour la petite carte
                            Spacer(Modifier.height(130.dp))
                        }

                        // --- CARD DE CONTRÔLE COMPACTE ---
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Ligne 1 : Inversion + Bouton Regen
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Inv", color = Color.White, fontSize = 12.sp)
                                        Spacer(Modifier.width(4.dp))
                                        Switch(
                                            checked = isInverted,
                                            onCheckedChange = { isInverted = it },
                                            modifier = Modifier.scale(0.7f) // On réduit visuellement le switch
                                        )
                                    }

                                    Button(
                                        onClick = { currentQrMatrix = generateRealisticQrMock() },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                                    ) {
                                        Text("REGEN", fontSize = 10.sp)
                                    }
                                }

                                // Ligne 2 : Vitesse
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("SPD: ${"%.1f".format(speedSliderValue)}", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.weight(0.20f))
                                    Slider(
                                        value = speedSliderValue,
                                        onValueChange = { speedSliderValue = it },
                                        valueRange = 0f..12f,
                                        modifier = Modifier.weight(0.80f).height(20.dp)
                                    )
                                }

                                // Ligne 3 : Particules
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("QTY: ${particleSliderValue.toInt()}", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.weight(0.20f))
                                    Slider(
                                        value = particleSliderValue,
                                        onValueChange = { particleSliderValue = it },
                                        valueRange = 100f..maxParticuleAllowed.toFloat(),
                                        modifier = Modifier.weight(0.80f).height(20.dp)
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

