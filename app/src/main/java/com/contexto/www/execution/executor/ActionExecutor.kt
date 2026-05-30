package com.contexto.www.execution.executor

import com.contexto.www.cognition.model.AgentAction

/**
 * Generic contract for executing agentic tools.
 */
interface ActionExecutor<in T : AgentAction> {
    /**
     * Executes the specific command logic.
     */
    suspend fun execute(action: T)
}
