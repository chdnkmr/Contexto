package com.contexto.www.cognition.model

/**
 * Represent tools or actions the LLM can decide to execute.
 */
sealed interface AgentAction {
    data class MutePhone(val durationMinutes: Int) : AgentAction
    data class PlaySoundscape(val trackId: String) : AgentAction
    data class ShowConfirmationNotification(val message: String) : AgentAction
    data object NoAction : AgentAction
}
