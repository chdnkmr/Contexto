package com.contexto.www.execution.executor

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.contexto.www.cognition.model.AgentAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles system volume adjustments and DND mode toggling.
 * Respects Android 14+ background and safety policies.
 */
class VolumeActionExecutor(private val context: Context) : ActionExecutor<AgentAction.MuteSystemVolume> {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun execute(action: AgentAction.MuteSystemVolume) = withContext(Dispatchers.IO) {
        try {
            if (action.level == 0) {
                // Check for Do Not Disturb access if we want to toggle silence mode completely
                if (notificationManager.isNotificationPolicyAccessGranted) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    Log.d("VolumeExecutor", "System muted via RINGER_MODE_SILENT")
                } else {
                    // Fallback to setting volumes to 0 if DND access isn't granted
                    setVolumesToLevel(0)
                    Log.d("VolumeExecutor", "System muted via volume levels (Fallback)")
                }
            } else {
                setVolumesToLevel(action.level)
                Log.d("VolumeExecutor", "System volume set to: ${action.level}")
            }
        } catch (e: Exception) {
            Log.e("VolumeExecutor", "Failed to execute volume action", e)
        }
    }

    private fun setVolumesToLevel(level: Int) {
        val streams = listOf(
            AudioManager.STREAM_RING,
            AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_SYSTEM
        )
        streams.forEach { stream ->
            try {
                // Ensure we don't exceed max volume
                val max = audioManager.getStreamMaxVolume(stream)
                val target = level.coerceIn(0, max)
                audioManager.setStreamVolume(stream, target, 0)
            } catch (e: SecurityException) {
                Log.w("VolumeExecutor", "SecurityException setting volume for stream $stream")
            }
        }
    }
}
