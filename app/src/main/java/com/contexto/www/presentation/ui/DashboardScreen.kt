package com.contexto.www.presentation.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.contexto.www.perception.model.LocationData
import com.contexto.www.perception.model.UserActivity
import com.contexto.www.presentation.model.DashboardUiState
import com.contexto.www.presentation.model.HistoricalActionLog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(uiState: DashboardUiState) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CONTEXTO", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PerceptionCard(uiState.currentSnapshot)
            
            AiStatusBar(uiState.isAiThinking)
            
            Text(
                text = "SYSTEM LOGS",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            ActivityTimeline(uiState.recentActions)
        }
    }
}

@Composable
fun PerceptionCard(snapshot: com.contexto.www.perception.model.CurrentContextSnapshot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val activityIcon = when (snapshot.activity) {
                    UserActivity.Stationary -> Icons.Default.Home
                    UserActivity.Moving -> Icons.Default.DirectionsWalk
                    UserActivity.Driving -> Icons.Default.DirectionsCar
                    UserActivity.Unknown -> Icons.Default.QuestionMark
                }
                Icon(activityIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Activity: ${snapshot.activity.javaClass.simpleName}", style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                val locationText = when (val loc = snapshot.location) {
                    is LocationData.Coordinates -> "%.4f, %.4f".format(loc.latitude, loc.longitude)
                    else -> "Unknown"
                }
                Text("Location: $locationText", style = MaterialTheme.typography.bodyMedium)
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(Modifier.width(8.dp))
                Text("Ambient Noise: ${snapshot.ambientNoiseDb} dB", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AiStatusBar(isThinking: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "ThinkingPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isThinking) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(alpha)
                    .padding(2.dp)
            )
            Text(
                "AGENT REASONING...",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.alpha(alpha)
            )
        } else {
            Text(
                "AGENT IDLE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun ActivityTimeline(logs: List<HistoricalActionLog>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(logs, key = { it.id }) { log ->
            TimelineItem(log)
        }
    }
}

@Composable
fun TimelineItem(log: HistoricalActionLog) {
    val sdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeStr = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(log.actionName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(timeStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "> reasoning: ${log.reasoning}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
