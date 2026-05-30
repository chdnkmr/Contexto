package com.contexto.www.cognition.model

import androidx.annotation.Keep

/**
 * Sealed command layer for all autonomous agent actions.
 */
@Keep
sealed interface AgentAction {
    data class MuteSystemVolume(val level: Int) : AgentAction
    data class PlayLocalSoundscape(val assetUri: String) : AgentAction
    data class RequestHumanConfirmation(
        val triggerAction: AgentAction,
        val promptText: String
    ) : AgentAction
    data object NoAction : AgentAction
}
