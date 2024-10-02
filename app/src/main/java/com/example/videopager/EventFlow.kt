package com.example.videopager

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Wrapper class for some event/data which allows access it only once
 *
 * [consume] returns [content], but only if it not disposed
 *
 * This class is used to implement some source of events where every event waits to be consumed
 * so it useful for attachable/detachable consumers (such as Arch Components ViewModel)
 */
data class Event<out T>(internal val content: T) {
    var consumed = false
        private set

    fun consume(): T? {
        return if (consumed) {
            null
        } else {
            consumed = true
            content
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (content != other.content) return false
        if (consumed != other.consumed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + consumed.hashCode()
        return result
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val CONSUMED: Event<Nothing> = Event(null).apply { consumed = true } as Event<Nothing>
    }
}

@Suppress("FunctionName")
fun <T> MutableEventFlow() = MutableStateFlow<Event<T>>(Event.CONSUMED)

fun <T> MutableStateFlow<Event<T>>.sendEvent(content: T) {
    value = Event(content)
}
