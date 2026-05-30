package com.contexto.www.perception.model

/**
 * Aggregated snapshot of the user's current environment.
 * Used as the primary input for the Cognition Layer.
 */
data class CurrentContextSnapshot(
    val location: UserLocation = UserLocation.Unknown,
    val activity: PhysicalActivity = PhysicalActivity.Unknown,
    val ambientNoiseDb: Float = 0f,
    val activeAppPackage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface UserLocation {
    data class Coordinates(val lat: Double, val lng: Double) : UserLocation
    data object Unknown : UserLocation
}

sealed interface PhysicalActivity {
    data object Stationary : PhysicalActivity
    data object Walking : PhysicalActivity
    data object Running : PhysicalActivity
    data object Automotive : PhysicalActivity
    data object Unknown : PhysicalActivity
}
