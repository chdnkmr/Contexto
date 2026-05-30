package com.contexto.www.cognition.memory

import com.contexto.www.perception.model.CurrentContextSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

data class EmbeddingNode(
    val id: String,
    val vector: FloatArray,
    val textPayload: String
)

/**
 * Optimized local-first vector retrieval engine.
 * Uses k-Nearest Neighbors (k-NN) with Cosine Similarity for memory retrieval.
 */
class LocalVectorMemoryEngine : VectorMemoryEngine {

    private val nodes = mutableListOf<EmbeddingNode>()
    private val mutex = Mutex()

    override suspend fun storeEmbedding(text: String) {
        // In production, use a local model (e.g., MediaPipe Text Embedder) to generate the vector.
        // For now, we simulate storage.
        withContext(Dispatchers.Default) {
            mutex.withLock {
                nodes.add(EmbeddingNode(
                    id = System.nanoTime().toString(),
                    vector = generateSimulatedVector(text),
                    textPayload = text
                ))
            }
        }
    }

    override suspend fun queryRelevantContext(snapshot: CurrentContextSnapshot): List<String> = withContext(Dispatchers.Default) {
        val queryVector = generateSimulatedVector(snapshot.toString())
        
        mutex.withLock {
            nodes.asSequence()
                .map { node -> node.textPayload to calculateCosineSimilarity(queryVector, node.vector) }
                .sortedByDescending { it.second }
                .take(5)
                .map { it.first }
                .toList()
        }
    }

    /**
     * Core mathematical calculation for vector similarity.
     * Calculated on Dispatchers.Default for CPU efficiency.
     */
    private fun calculateCosineSimilarity(vectorA: FloatArray, vectorB: FloatArray): Float {
        var dotProduct = 0.0f
        var normA = 0.0f
        var normB = 0.0f
        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += vectorA[i] * vectorA[i]
            normB += vectorB[i] * vectorB[i]
        }
        val denominator = sqrt(normA.toDouble()) * sqrt(normB.toDouble())
        return if (denominator <= 0.0) 0.0f else (dotProduct / denominator).toFloat()
    }

    private fun generateSimulatedVector(text: String): FloatArray {
        // Stub: Replace with real embedding model inference
        return FloatArray(384) { text.hashCode().toFloat() / Int.MAX_VALUE }
    }
}

/**
 * Base interface for vector retrieval.
 */
interface VectorMemoryEngine {
    suspend fun storeEmbedding(text: String)
    suspend fun queryRelevantContext(snapshot: CurrentContextSnapshot): List<String>
}
