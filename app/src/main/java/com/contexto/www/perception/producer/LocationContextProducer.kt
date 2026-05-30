package com.contexto.www.perception.producer

import com.contexto.www.perception.model.LocationData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Mock implementation of FusedLocationProviderClient wrapper.
 * In production, this would use callbackFlow to wrap the Google Play Services API.
 */
class LocationContextProducer : ContextProducer<LocationData> {
    override val contextStream: Flow<LocationData> = flow {
        // Mocking location updates every 2 minutes as requested for battery optimization
        while (true) {
            emit(LocationData.Coordinates(
                latitude = 37.7749,
                longitude = -122.4194,
                accuracy = 10f
            ))
            delay(120_000L) 
        }
    }
}
