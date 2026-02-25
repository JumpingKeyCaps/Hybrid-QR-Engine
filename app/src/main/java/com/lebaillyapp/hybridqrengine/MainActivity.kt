package com.lebaillyapp.hybridqrengine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lebaillyapp.hybridqrengine.ui.component.GhostMedallionQr
import com.lebaillyapp.hybridqrengine.ui.screen.GhostMedallionScreen
import com.lebaillyapp.hybridqrengine.ui.screen.KineticGhostScreen
import com.lebaillyapp.hybridqrengine.ui.screen.StealthScreen
import com.lebaillyapp.hybridqrengine.ui.theme.HybridQrEngineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HybridQrEngineTheme {
               // StealthScreen()
                KineticGhostScreen()
              //  GhostMedallionScreen()
            }
        }
    }
}

