package com.contexto.www.perception.aggregator

import com.contexto.www.perception.model.CurrentContextSnapshot
import com.contexto.www.perception.model.UserActivity
import com.contexto.www.perception.model.LocationData
import com.contexto.www.perception.producer.ContextProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

/**
 * Implementation of ContextAggregator that fuses multiple context streams.
 * Employs power-saving strategies like debouncing and distinct emissions.
 */
class ContextAggregatorImpl(
    scope: CoroutineScope,
    locationProducer: ContextProducer<LocationData>,
    activityProducer: ContextProducer<UserActivity>,
    noiseProducer: ContextProducer<Int>
) {
    @OptIn(FlowPreview::class)
    val snapshot: StateFlow<CurrentContextSnapshot> = combine(
        locationProducer.contextStream,
        activityProducer.contextStream,
        noiseProducer.contextStream
    ) { location, activity, noise ->
        CurrentContextSnapshot(
            location = location,
            activity = activity,
            ambientNoiseDb = noise,
            timestamp = System.currentTimeMillis()
        )
    }
        // Power Optimization: Don't trigger the Reason-Act loop for minor/frequent changes
        .debounce(5000L) 
        // Only emit if the structural content (Location, Activity, Noise level) actually changed
        .distinctUntilChanged { old, new ->
            old.location == new.location &&
            old.activity == new.activity &&
            Math.abs(old.ambientNoiseDb - new.ambientNoiseDb) < 5 // Minor noise changes are ignored
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = CurrentContextSnapshot()
        )
}
