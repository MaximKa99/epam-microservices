package com.epam.model

import com.epam.exception.CustomException

enum class ResourceType(
    val type: String,
    val bucket: String,
    val queueIn: String,
    val queueOut: String
) {
    Audio("audio/mpeg", "audio", "audio.out.fifo", "audio.in.fifo");

    companion object {
        const val HEADER_NAME = "Resource-Type"
        fun of(value: String): ResourceType {
            return when(value) {
                Audio.type -> Audio
                else -> throw CustomException("Unrecognised type", 400)
            }
        }
    }
}