package com.contexto.www.perception.producer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Mock implementation of ambient noise detection.
 * In production, this would use AudioRecord or MediaRecorder to sample decibels.
 */
class NoiseContextProducer : ContextProducer<Int> {
    override val contextStream: Flow<Int> = flow {
        while (true) {
            // Emitting random decibel levels between 30 and 90
            emit(Random.nextInt(30, 90))
            delay(10_000L) // Sample every 10 seconds
        }
    }
}
