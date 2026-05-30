package com.contexto.www.execution.router

import android.util.Log
import com.contexto.www.cognition.model.AgentAction
import com.contexto.www.execution.executor.ActionExecutor
import com.contexto.www.execution.executor.VolumeActionExecutor
import com.contexto.www.execution.executor.MediaActionExecutor
import com.contexto.www.execution.verification.NotificationVerificationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Central broker for routing AgentActions to their respective executors.
 * Parses simulated LLM JSON responses into strongly typed AgentActions.
 */
class ToolRouterImpl(
    private val volumeExecutor: VolumeActionExecutor,
    private val mediaExecutor: MediaActionExecutor,
    private val verificationManager: NotificationVerificationManager
) {

    /**
     * Entry point for raw LLM output. Parses and dispatches actions on Dispatchers.IO.
     */
    suspend fun parseAndRoute(jsonCommand: String) = withContext(Dispatchers.IO) {
        try {
            val action = parseJsonToAgentAction(jsonCommand)
            executeAction(action)
        } catch (e: Exception) {
            Log.e("ToolRouter", "Failed to parse or route command: $jsonCommand", e)
        }
    }

    private fun parseJsonToAgentAction(json: String): AgentAction {
        val root = JSONObject(json)
        return when (root.optString("action")) {
            "MUTE" -> AgentAction.MuteSystemVolume(level = root.optInt("level", 0))
            "PLAY_SOUND" -> AgentAction.PlayLocalSoundscape(assetUri = root.optString("uri"))
            "CONFIRM" -> {
                val nestedJson = root.optJSONObject("trigger")?.toString() ?: ""
                val triggerAction = if (nestedJson.isNotEmpty()) parseJsonToAgentAction(nestedJson) else AgentAction.NoAction
                AgentAction.RequestHumanConfirmation(
                    triggerAction = triggerAction,
                    promptText = root.optString("message", "Confirm action?")
                )
            }
            else -> AgentAction.NoAction
        }
    }

    private suspend fun executeAction(action: AgentAction) {
        when (action) {
            is AgentAction.MuteSystemVolume -> volumeExecutor.execute(action)
            is AgentAction.PlayLocalSoundscape -> mediaExecutor.execute(action)
            is AgentAction.RequestHumanConfirmation -> verificationManager.execute(action)
            AgentAction.NoAction -> Log.d("ToolRouter", "No action required")
        }
    }
}
