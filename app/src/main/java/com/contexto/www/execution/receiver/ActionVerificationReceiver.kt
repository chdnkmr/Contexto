package com.contexto.www.execution.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.contexto.www.cognition.model.AgentAction
import com.contexto.www.execution.executor.VolumeActionExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Receiver responsible for handling user input from verification notifications.
 * Triggers the deferred AgentAction upon explicit approval.
 */
class ActionVerificationReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_APPROVE = "com.contexto.www.action.APPROVE"
        const val ACTION_DENY = "com.contexto.www.action.DENY"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_ACTION_TYPE = "extra_action_type"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val actionType = intent.getStringExtra(EXTRA_ACTION_TYPE)
        
        // Cancel the notification immediately
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationId != -1) {
            notificationManager.cancel(notificationId)
        }

        when (intent.action) {
            ACTION_APPROVE -> {
                Log.d("ActionReceiver", "User APPROVED action: $actionType")
                handleApproval(context, intent, actionType)
            }
            ACTION_DENY -> {
                Log.d("ActionReceiver", "User DENIED action: $actionType")
            }
        }
    }

    private fun handleApproval(context: Context, intent: Intent, actionType: String?) {
        receiverScope.launch {
            try {
                // In a full production DI setup, these executors would be injected or retrieved from a Singleton/Registry
                when (actionType) {
                    "MuteSystemVolume" -> {
                        val level = intent.getIntExtra("volume_level", 0)
                        val executor = VolumeActionExecutor(context)
                        executor.execute(AgentAction.MuteSystemVolume(level))
                    }
                    // Handle other approved actions here
                    else -> Log.w("ActionReceiver", "Unknown action type approved: $actionType")
                }
            } catch (e: Exception) {
                Log.e("ActionReceiver", "Error executing approved action", e)
            }
        }
    }
}
