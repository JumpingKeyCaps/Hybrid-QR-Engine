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

@Composable
fun StealthSettingsPanel(
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Header: Inv + Regen
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Inv", color = Color.White, fontSize = 12.sp)
                    Switch(checked = isInverted, onCheckedChange = onInvertChange, modifier = Modifier.scale(0.7f))
                }
                Button(
                    onClick = onRegen,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333))
                ) {
                    Text("REGEN", fontSize = 10.sp)
                }
            }

            // Sliders standard
            CompactSlider("SPD", speed, 0f..12f) { onSpeedChange(it) }
            CompactSlider("QTY", particleCount, 100f..maxParticles) { onParticleChange(it) }

            HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Color(0xFF333333))

        }
    }
}

@Composable
private fun CompactSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$label: ${"%.1f".format(value)}", color = Color.LightGray, fontSize = 11.sp, modifier = Modifier.weight(0.25f))
        Slider(value = value, onValueChange = onValueChange, valueRange = range, modifier = Modifier.weight(0.75f).height(20.dp))
    }
}