package com.contexto.www.cognition.engine

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import kotlin.math.pow

/**
 * Handles AICore-specific failure modes including system busyness and thermal/battery constraints.
 * Implements exponential backoff for transient system-level AI service errors.
 */
class AicoreFallbackHandler {

    /**
     * Executes a generative task with robust retry logic for AICore system exceptions.
     */
    fun executeWithRetry(
        task: suspend () -> String
    ): Flow<AicoreResult> = flow {
        emit(AicoreResult.Success(task()))
    }.retryWhen { cause, attempt ->
        val shouldRetry = when (cause) {
            is AicoreBusyException -> attempt < 3
            is AicoreBatteryExceededException -> false // Hard stop to preserve device health
            is IOException -> attempt < 2
            else -> false
        }

        if (shouldRetry) {
            val backoffDelay = 2.0.pow(attempt.toDouble()).toLong() * 1000L
            Log.w("AicoreFallback", "AICore busy. Retrying in ${backoffDelay}ms (Attempt ${attempt + 1})")
            delay(backoffDelay)
            true
        } else {
            Log.e("AicoreFallback", "AICore execution failed permanently", cause)
            false
        }
    }
}

sealed interface AicoreResult {
    data class Success(val output: String) : AicoreResult
    data class Failure(val error: Throwable) : AicoreResult
}

class AicoreBusyException : Exception("AICore is currently overloaded with other tasks.")
class AicoreBatteryExceededException : Exception("AICore execution suspended due to battery/thermal limits.")
