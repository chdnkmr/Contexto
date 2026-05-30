package com.contexto.www.perception.producer

import com.contexto.www.perception.model.UserActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Mock implementation of ActivityRecognitionClient transitions.
 */
class ActivityContextProducer : ContextProducer<UserActivity> {
    override val contextStream: Flow<UserActivity> = flow {
        // Mocking activity changes
        while (true) {
            emit(UserActivity.Stationary)
            delay(300_000L) // Stay stationary for 5 mins
            emit(UserActivity.Moving)
            delay(600_000L) // Moving for 10 mins
        }
    }
}
