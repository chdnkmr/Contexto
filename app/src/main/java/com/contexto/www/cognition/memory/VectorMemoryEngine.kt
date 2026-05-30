package com.contexto.www.cognition.memory

import com.contexto.www.perception.model.CurrentContextSnapshot

/**
 * Interface for long-term memory retrieval using vector embeddings.
 */
interface VectorMemoryEngine {
    /**
     * Stores a piece of information as an embedding.
     */
    suspend fun storeEmbedding(text: String)

    /**
     * Queries relevant historical context based on the current snapshot.
     */
    suspend fun queryRelevantContext(snapshot: CurrentContextSnapshot): List<String>
}
