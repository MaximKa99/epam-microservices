package com.epam

import com.epam.exception.CustomException

enum class ResourceType(
    val type: String,
    val bucket: String,
    val queue: String,
) {
    Audio("audio/mpeg", "audio", "audio.fifo");

    companion object {
        fun of(value: String): ResourceType {
            return when(value) {
                Audio.type -> Audio
                else -> throw CustomException("Unrecognised type", 400)
            }
        }
    }
}