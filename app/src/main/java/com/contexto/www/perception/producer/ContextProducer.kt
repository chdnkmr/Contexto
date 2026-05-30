package com.contexto.www.perception.producer

import kotlinx.coroutines.flow.Flow

/**
 * Generic interface for components that produce environmental context data.
 */
interface ContextProducer<T> {
    /**
     * A cold or hot flow of context updates.
     */
    val contextStream: Flow<T>
}
