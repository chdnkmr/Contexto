package com.contexto.www.execution.executor

import com.contexto.www.cognition.model.AgentAction

/**
 * Interface for executing a specific type of AgentAction.
 * Implementations will handle Android OS specifics (e.g., AudioManager, NotificationManager).
 */
interface ActionExecutor {
    /**
     * Executes the given action.
     * @return true if the action was handled by this executor, false otherwise.
     */
    suspend fun execute(action: AgentAction): Boolean
}
