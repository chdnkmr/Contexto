package com.contexto.www.cognition.engine

import android.util.Log
import com.contexto.www.cognition.memory.VectorMemoryEngine
import com.contexto.www.cognition.prompt.ContextPromptBuilder
import com.contexto.www.perception.aggregator.ContextAggregatorImpl
import com.contexto.www.perception.model.CurrentContextSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Refactored Cognitive Engine optimized for AICore Prefix Caching.
 * Decouples static system instructions from dynamic context tokens to maximize hardware reuse.
 */
class OptimizedCognitiveEngine(
    private val scope: CoroutineScope,
    private val aggregator: ContextAggregatorImpl,
    private val memoryEngine: VectorMemoryEngine,
    private val fallbackHandler: AicoreFallbackHandler
) {
    private val _agentActions = MutableSharedFlow<String>()
    val agentActions: SharedFlow<String> = _agentActions.asSharedFlow()

    private var lastProcessedTimestamp: Long = 0L

    init {
        scope.launch {
            aggregator.snapshot
                .filter { it.isSignificantChange() }
                .collectLatest { snapshot ->
                    runInferencePipeline(snapshot)
                }
        }
    }

    private suspend fun runInferencePipeline(snapshot: CurrentContextSnapshot) {
        withContext(Dispatchers.Default) {
            try {
                // 1. Context Retrieval
                val memories = memoryEngine.queryRelevantContext(snapshot)

                // 2. Optimized Prompt Construction
                // We simulate Prefix Caching by passing system instructions separately 
                // to the underlying engine if supported by the SDK.
                val staticPrefix = ContextPromptBuilder.getSystemInstruction() 
                val dynamicPrompt = ContextPromptBuilder.buildDynamicContext(snapshot, memories)

                // 3. Robust Execution with AICore Fallback
                fallbackHandler.executeWithRetry {
                    performHardwareInference(staticPrefix, dynamicPrompt)
                }.collect { result ->
                    when (result) {
                        is AicoreResult.Success -> {
                            _agentActions.emit(result.output)
                            lastProcessedTimestamp = snapshot.timestamp
                        }
                        is AicoreResult.Failure -> {
                            Log.e("CognitiveEngine", "Terminal Inference Failure", result.error)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CognitiveEngine", "Critical pipeline error", e)
            }
        }
    }

    private suspend fun performHardwareInference(prefix: String, prompt: String): String {
        // Mocking hardware-accelerated inference with Prefix Caching support
        // In reality, this calls GenerativeModel.generateContent(content { text(prompt) })
        // where the prefix is already 'warmed' in AICore cache.
        return "{\"action\": \"NO_ACTION\"}"
    }

    private fun CurrentContextSnapshot.isSignificantChange(): Boolean {
        return (timestamp - lastProcessedTimestamp) > 30_000L
    }
}
