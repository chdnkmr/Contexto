package com.contexto.www.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contexto.www.perception.aggregator.ContextAggregatorImpl
import com.contexto.www.presentation.model.ActionStatus
import com.contexto.www.presentation.model.DashboardUiState
import com.contexto.www.presentation.model.HistoricalActionLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Contexto Dashboard.
 * Fuses real-time perception data and historical action logs into a single UI state.
 */
class ContextoViewModel(
    aggregator: ContextAggregatorImpl
) : ViewModel() {

    // Simulated historical log flow (In production, this would come from a Room DAO)
    private val _historicalLogs = MutableStateFlow(
        listOf(
            HistoricalActionLog(
                id = "1",
                timestamp = System.currentTimeMillis() - 3600000,
                actionName = "Mute Volume",
                reasoning = "User entered a 'Meeting' geofence.",
                status = ActionStatus.COMPLETED
            ),
            HistoricalActionLog(
                id = "2",
                timestamp = System.currentTimeMillis() - 7200000,
                actionName = "Play Soundscape",
                reasoning = "Ambient noise level exceeded 70dB while stationary.",
                status = ActionStatus.COMPLETED
            )
        )
    )

    // Simulated "AI Thinking" state
    private val _isAiThinking = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        aggregator.snapshot,
        _historicalLogs,
        _isAiThinking
    ) { snapshot, logs, thinking ->
        DashboardUiState(
            currentSnapshot = snapshot,
            recentActions = logs,
            isAiThinking = thinking
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    /**
     * Simulation of AI process triggering
     */
    fun simulateInference() {
        _isAiThinking.value = true
        // Logic would normally happen in the engine
    }
}
