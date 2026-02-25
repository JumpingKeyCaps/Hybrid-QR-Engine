package com.lebaillyapp.hybridqrengine.ui.component.settingsPan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lebaillyapp.hybridqrengine.model.GhostShaderConfig

@Composable
fun GhostSettingsPanel(
    config: GhostShaderConfig,
    onConfigChange: (GhostShaderConfig) -> Unit,
    particleCount: Float,
    particleMax: Float,
    onParticleChange: (Float) -> Unit,
    speed: Float,
    onSpeedChange: (Float) -> Unit,
    isInverted: Boolean,
    onInvertChange: (Boolean) -> Unit,
    onRegen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            // Header: Inv + Regen
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("INV", color = Color.White, fontSize = 10.sp)
                    Switch(checked = isInverted, onCheckedChange = onInvertChange, modifier = Modifier.scale(0.6f))
                }
                Button(onClick = onRegen, modifier = Modifier.height(24.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                    Text("REGEN", fontSize = 9.sp)
                }
            }

            // Sliders de base
            SettingsSlider("SPD", speed, 0f..12f) { onSpeedChange(it) }
            SettingsSlider("QTY", particleCount, 100f..particleMax) { onParticleChange(it) }

            Divider(Modifier.padding(vertical = 4.dp), color = Color.DarkGray)

            // Sliders Shader
            SettingsSlider("SIZE", config.innerSize, 0.1f..0.9f) { onConfigChange(config.copy(innerSize = it)) }
            SettingsSlider("SOFT", config.shapeSoftness, 0f..1f) { onConfigChange(config.copy(shapeSoftness = it)) }
            SettingsSlider("D_BRT", config.dataBrightness, 0f..1f) { onConfigChange(config.copy(dataBrightness = it)) }
            SettingsSlider("B_ALPHA", config.bgAlpha, 0f..0.3f) { onConfigChange(config.copy(bgAlpha = it)) }
        }
    }
}

@Composable
fun SettingsSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ${"%.2f".format(value)}", color = Color.Gray, fontSize = 9.sp, modifier = Modifier.weight(0.3f))
        Slider(value = value, onValueChange = onValueChange, valueRange = range, modifier = Modifier.weight(0.7f).height(16.dp))
    }
}