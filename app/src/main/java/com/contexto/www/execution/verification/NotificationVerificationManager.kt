package com.contexto.www.execution.verification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.contexto.www.cognition.model.AgentAction
import com.contexto.www.execution.executor.ActionExecutor
import com.contexto.www.execution.receiver.ActionVerificationReceiver

/**
 * Handles human-in-the-loop verification via high-priority interactive notifications.
 */
class NotificationVerificationManager(private val context: Context) : ActionExecutor<AgentAction.RequestHumanConfirmation> {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        private const val CHANNEL_ID = "verification_channel"
        private const val NOTIFICATION_ID_BASE = 2000
    }

    init {
        createVerificationChannel()
    }

    override suspend fun execute(action: AgentAction.RequestHumanConfirmation) {
        val notificationId = NOTIFICATION_ID_BASE + action.hashCode()

        // Create Intents for Approve/Deny
        val approveIntent = Intent(context, ActionVerificationReceiver::class.java).apply {
            this.action = ActionVerificationReceiver.ACTION_APPROVE
            putExtra(ActionVerificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
            // Simplified: Passing a flag or identifying string for the action. 
            // In production, use a persistent ID or serialized state.
            putExtra(ActionVerificationReceiver.EXTRA_ACTION_TYPE, action.triggerAction.javaClass.simpleName)
            if (action.triggerAction is AgentAction.MuteSystemVolume) {
                putExtra("volume_level", action.triggerAction.level)
            }
        }

        val denyIntent = Intent(context, ActionVerificationReceiver::class.java).apply {
            this.action = ActionVerificationReceiver.ACTION_DENY
            putExtra(ActionVerificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }

        val approvePendingIntent = PendingIntent.getBroadcast(
            context, notificationId, approveIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val denyPendingIntent = PendingIntent.getBroadcast(
            context, notificationId + 1, denyIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Agent Request")
            .setContentText(action.promptText)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "Approve", approvePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Deny", denyPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createVerificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Action Verifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Required for confirming autonomous agent actions"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
