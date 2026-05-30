package com.contexto.www.execution.router

import com.contexto.www.cognition.engine.CognitiveEngine
import com.contexto.www.execution.executor.ActionExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Routes AgentActions produced by the CognitiveEngine to the appropriate ActionExecutor.
 * Execution is isolated to a background I/O dispatcher to prevent blocking the engine.
 */
class ToolRouter(
    private val scope: CoroutineScope,
    private val cognitiveEngine: CognitiveEngine,
    private val executors: List<ActionExecutor>
) {
    private var routingJob: Job? = null

    /**
     * Starts listening to the action stream and routing to executors.
     */
    fun startRouting() {
        routingJob?.cancel()
        routingJob = cognitiveEngine.actionStream
            .onEach { action ->
                routeAction(action)
            }
            .launchIn(scope)
    }

    private suspend fun routeAction(action: com.contexto.www.cognition.model.AgentAction) {
        // Execute on Dispatchers.IO as actions often involve OS/Hardware interactions
        withContext(Dispatchers.IO) {
            for (executor in executors) {
                if (executor.execute(action)) {
                    // Action handled by an executor, stop routing this specific action
                    break
                }
            }
        }
    }

    fun stopRouting() {
        routingJob?.cancel()
        routingJob = null
    }
}
