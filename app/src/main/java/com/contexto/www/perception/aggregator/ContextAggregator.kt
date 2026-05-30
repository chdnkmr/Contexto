package com.contexto.www.perception.aggregator

import com.contexto.www.perception.model.CurrentContextSnapshot
import com.contexto.www.perception.model.PhysicalActivity
import com.contexto.www.perception.model.UserLocation
import com.contexto.www.perception.producer.ContextProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn

/**
 * Aggregates multiple context streams into a single, debounced state.
 * Throttles changes to optimize battery life and reduce LLM processing frequency.
 */
class ContextAggregator(
    scope: CoroutineScope,
    locationProducer: ContextProducer<UserLocation>,
    activityProducer: ContextProducer<PhysicalActivity>,
    noiseProducer: ContextProducer<Float>,
    appContextProducer: ContextProducer<String?>
) {
    @OptIn(FlowPreview::class)
    val snapshot: StateFlow<CurrentContextSnapshot> = combine(
        locationProducer.contextStream,
        activityProducer.contextStream,
        noiseProducer.contextStream,
        appContextProducer.contextStream
    ) { location, activity, noise, app ->
        CurrentContextSnapshot(
            location = location,
            activity = activity,
            ambientNoiseDb = noise,
            activeAppPackage = app
        )
    }
        // Debounce to avoid micro-changes triggering the expensive cognition loop
        .debounce(1000L) 
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CurrentContextSnapshot()
        )
}
