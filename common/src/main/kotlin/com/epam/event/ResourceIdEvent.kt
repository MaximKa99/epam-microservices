package com.epam.event

data class ResourceIdEvent(
    val resourceId: Int
) {
    companion object {
        const val EVENT_TYPE: String = "ResourceIdEvent"
    }
}