package com.contexto.www.perception.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.contexto.www.perception.aggregator.ContextAggregatorImpl
import com.contexto.www.perception.producer.ActivityContextProducer
import com.contexto.www.perception.producer.LocationContextProducer
import com.contexto.www.perception.producer.NoiseContextProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Foreground Service that anchors the Perception Layer.
 * Declares 'location' and 'microphone' types for Android 14+ compatibility.
 */
class ContextPerceptionService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var aggregator: ContextAggregatorImpl

    companion object {
        private const val CHANNEL_ID = "perception_service_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundService()

        // Initialize Producers
        val locationProducer = LocationContextProducer()
        val activityProducer = ActivityContextProducer()
        val noiseProducer = NoiseContextProducer()

        // Initialize Aggregator
        aggregator = ContextAggregatorImpl(
            scope = serviceScope,
            locationProducer = locationProducer,
            activityProducer = activityProducer,
            noiseProducer = noiseProducer
        )

        // Observe the unified context stream
        aggregator.snapshot
            .onEach { snapshot ->
                // This is where the Reason-Act loop would be triggered
                // For now, we log or pass to Cognition Layer components
            }
            .launchIn(serviceScope)
    }

    private fun startForegroundService() {
        val notification = createNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_34) {
            // Android 14+ requires explicit type declaration
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or 
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Contexto Active")
            .setContentText("Monitoring environmental context locally...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Context Perception",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for background context monitoring"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Ensure all flows and producers are cleaned up
    }
}
