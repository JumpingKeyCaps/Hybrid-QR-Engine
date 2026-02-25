package com.lebaillyapp.hybridqrengine.ui.component.settingsPan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lebaillyapp.hybridqrengine.model.KineticShaderConfig

@Composable
fun KineticSettingsPanel(
    config: KineticShaderConfig,
    onConfigChange: (KineticShaderConfig) -> Unit,
    particleCount: Float,
    onParticleChange: (Float) -> Unit,
    speed: Float,
    onSpeedChange: (Float) -> Unit,
    isInverted: Boolean,
    onInvertChange: (Boolean) -> Unit,
    onRegen: () -> Unit,
    maxParticles: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.75f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Ligne 1 : Inversion et Regen
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Inv", color = Color.White, fontSize = 12.sp)
                    Switch(checked = isInverted, onCheckedChange = onInvertChange, modifier = Modifier.scale(0.7f))
                }
                Button(onClick = onRegen, modifier = Modifier.height(28.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("REGEN", fontSize = 10.sp)
                }
            }

            // Moteur
            CompactSlider("SPD", speed, 0f..15f) { onSpeedChange(it) }
            CompactSlider("QTY", particleCount, 100f..maxParticles) { onParticleChange(it) }

            HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))

            // Couleurs
            HueSlider("HUE A", config.hue1) { onConfigChange(config.copy(hue1 = it)) }
            HueSlider("HUE B", config.hue2) { onConfigChange(config.copy(hue2 = it)) }
            CompactSlider("ALPHA", config.bgColorAlpha, 0f..0.6f) { onConfigChange(config.copy(bgColorAlpha = it)) }
            HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))
            // Tailles et Physique
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(1f)) {  }
                Box(Modifier.weight(1f)) { }
            }
            CompactSlider("S-MAX", config.particleSizeMax, 2f..15f) { onConfigChange(config.copy(particleSizeMax = it)) }
            CompactSlider("S-MIN", config.particleSizeMin, 0.5f..15f) { onConfigChange(config.copy(particleSizeMin = it)) }
            HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))
            CompactSlider("STASE", config.staseEffect, 0.01f..1f) { onConfigChange(config.copy(staseEffect = it)) }
            CompactSlider("INNER", config.innerSize, 0.5f..1f) { onConfigChange(config.copy(innerSize = it)) }
        }
    }
}

@Composable
private fun HueSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    val rainbow = Brush.horizontalGradient(listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.width(45.dp))
        Box(Modifier.weight(1f).height(8.dp).clip(CircleShape).background(rainbow)) {
            Slider(value = value, onValueChange = onValueChange, valueRange = 0f..360f,
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.Transparent, inactiveTrackColor = Color.Transparent))
        }
    }
}

@Composable
private fun CompactSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ${"%.2f".format(value)}", color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.weight(0.20f))
        Slider(value = value, onValueChange = onValueChange, valueRange = range, modifier = Modifier.weight(0.80f).height(18.dp))
    }
}