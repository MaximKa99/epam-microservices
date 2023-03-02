package com.epam.model.event

data class ResourceIdEvent(
    val resourceId: Long
) {
    companion object {
        const val EVENT_TYPE: String = "ResourceIdEvent"
    }
}