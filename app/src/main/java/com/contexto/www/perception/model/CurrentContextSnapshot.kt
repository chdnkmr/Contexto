package com.contexto.www.perception.model

import androidx.annotation.Keep

@Keep
data class CurrentContextSnapshot(
    val location: LocationData = LocationData.Unknown,
    val activity: UserActivity = UserActivity.Unknown,
    val ambientNoiseDb: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface LocationData {
    data class Coordinates(
        val latitude: Double,
        val longitude: Double,
        val accuracy: Float
    ) : LocationData
    data object Unknown : LocationData
}

sealed interface UserActivity {
    data object Stationary : UserActivity
    data object Moving : UserActivity
    data object Driving : UserActivity
    data object Unknown : UserActivity
}
