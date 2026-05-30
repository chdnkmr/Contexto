package com.contexto.www.cognition.prompt

import com.contexto.www.perception.model.CurrentContextSnapshot
import com.contexto.www.perception.model.LocationData
import com.contexto.www.perception.model.UserActivity

/**
 * Utility for constructing token-efficient, highly structured system prompts.
 * Forces the local LLM to follow a strict JSON execution schema.
 */
object ContextPromptBuilder {

    private const val SYSTEM_INSTRUCTION = """
        You are an autonomous Android agent named Contexto. 
        Your goal is to assist the user by executing actions based on their current context and history.
        
        RULES:
        1. Output ONLY raw, valid JSON.
        2. Do not include any conversational filler or markdown code fences.
        3. Available Actions:
           - MUTE: {"action": "MUTE", "level": 0}
           - PLAY_SOUND: {"action": "PLAY_SOUND", "uri": "string"}
           - CONFIRM: {"action": "CONFIRM", "message": "string", "trigger": {nested_action}}
           - NO_ACTION: {"action": "NO_ACTION"}
    """

    fun build(snapshot: CurrentContextSnapshot, memories: List<String>): String {
        val contextStr = StringBuilder().apply {
            append("CURRENT CONTEXT:\n")
            append("- Activity: ${snapshot.activity.toFriendlyString()}\n")
            append("- Location: ${snapshot.location.toFriendlyString()}\n")
            append("- Noise Level: ${snapshot.ambientNoiseDb}dB\n")
            
            if (memories.isNotEmpty()) {
                append("\nRELEVANT MEMORIES:\n")
                memories.forEach { append("- $it\n") }
            }
        }.toString()

        return "$SYSTEM_INSTRUCTION\n\n$contextStr\n\nDECISION (JSON ONLY):"
    }

    private fun UserActivity.toFriendlyString() = when (this) {
        UserActivity.Stationary -> "Stationary"
        UserActivity.Moving -> "Moving"
        UserActivity.Driving -> "Driving"
        UserActivity.Unknown -> "Unknown"
    }

    private fun LocationData.toFriendlyString() = when (this) {
        is LocationData.Coordinates -> "Lat: ${this.latitude}, Lng: ${this.longitude}"
        LocationData.Unknown -> "Unknown"
    }
}
