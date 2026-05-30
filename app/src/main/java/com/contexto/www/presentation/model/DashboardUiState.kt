package com.contexto.www.presentation.model

import androidx.compose.runtime.Immutable
import com.contexto.www.perception.model.CurrentContextSnapshot

@Immutable
data class DashboardUiState(
    val currentSnapshot: CurrentContextSnapshot = CurrentContextSnapshot(),
    val isAiThinking: Boolean = false,
    val recentActions: List<HistoricalActionLog> = emptyList()
)

@Immutable
data class HistoricalActionLog(
    val id: String,
    val timestamp: Long,
    val actionName: String,
    val reasoning: String,
    val status: ActionStatus = ActionStatus.COMPLETED
)

enum class ActionStatus {
    COMPLETED, FAILED, PENDING
}
