package com.contexto.www

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewmodel.compose.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.contexto.www.perception.aggregator.ContextAggregatorImpl
import com.contexto.www.perception.producer.ActivityContextProducer
import com.contexto.www.perception.producer.LocationContextProducer
import com.contexto.www.perception.producer.NoiseContextProducer
import com.contexto.www.perception.service.ContextPerceptionService
import com.contexto.www.presentation.ui.DashboardScreen
import com.contexto.www.presentation.viewmodel.ContextoViewModel
import com.contexto.www.presentation.viewmodel.ContextoViewModelFactory
import com.contexto.www.ui.theme.ContextoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var isSystemReady = false
    private lateinit var aggregator: ContextAggregatorImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Manual Injection for demonstration; in production use Hilt/Koin
        aggregator = ContextAggregatorImpl(
            scope = lifecycleScope,
            locationProducer = LocationContextProducer(),
            activityProducer = ActivityContextProducer(),
            noiseProducer = NoiseContextProducer()
        )

        splashScreen.setKeepOnScreenCondition { !isSystemReady }

        startPerceptionService()
        
        lifecycleScope.launch {
            delay(1500)
            isSystemReady = true
        }

        setContent {
            ContextoTheme {
                val viewModel: ContextoViewModel = viewModel(
                    factory = ContextoViewModelFactory(aggregator)
                )
                val uiState by viewModel.uiState.collectAsState()
                DashboardScreen(uiState = uiState)
            }
        }
    }

    private fun startPerceptionService() {
        val intent = Intent(this, ContextPerceptionService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
