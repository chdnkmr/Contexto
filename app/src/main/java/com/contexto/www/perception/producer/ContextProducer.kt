package com.contexto.www.perception.producer

import kotlinx.coroutines.flow.Flow

/**
 * Contract for context data streams.
 */
interface ContextProducer<T> {
    val contextStream: Flow<T>
}
