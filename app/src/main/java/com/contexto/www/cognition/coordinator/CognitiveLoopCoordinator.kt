package com.contexto.www.cognition.coordinator

import android.util.Log
import com.contexto.www.cognition.engine.CognitiveEngineImpl
import com.contexto.www.execution.router.ToolRouterImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Orchestrator that bridges Perception, Cognition, and Execution.
 * Handles defensive parsing of LLM outputs and routes them to the Tool Router.
 */
class CognitiveLoopCoordinator(
    private val scope: CoroutineScope,
    private val cognitiveEngine: CognitiveEngineImpl,
    private val toolRouter: ToolRouterImpl
) {

    init {
        observeCognitiveOutput()
    }

    private fun observeCognitiveOutput() {
        cognitiveEngine.rawAiResponses
            .onEach { rawResponse ->
                handleRawResponse(rawResponse)
            }
            .launchIn(scope)
    }

    private suspend fun handleRawResponse(rawResponse: String) = withContext(Dispatchers.Default) {
        try {
            val cleanJson = sanitizeLlmOutput(rawResponse)
            if (cleanJson.isNotEmpty()) {
                toolRouter.parseAndRoute(cleanJson)
            }
        } catch (e: Exception) {
            Log.e("CognitiveCoordinator", "Error handling LLM response", e)
        }
    }

    /**
     * Strips accidental conversational filler or markdown code fences from LLM output.
     * Example: ```json { "action": "MUTE" } ``` -> { "action": "MUTE" }
     */
    private fun sanitizeLlmOutput(input: String): String {
        var sanitized = input.trim()
        
        // Remove Markdown code fences if present
        if (sanitized.startsWith("```")) {
            // Remove starting ```json or ```
            sanitized = sanitized.substringAfter("\n")
            // Remove ending ```
            sanitized = sanitized.substringBeforeLast("```")
        }
        
        // Defensive check: find first { and last } to isolate JSON block
        val firstBrace = sanitized.indexOf('{')
        val lastBrace = sanitized.lastIndexOf('}')
        
        return if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            sanitized.substring(firstBrace, lastBrace + 1)
        } else {
            ""
        }
    }
}
