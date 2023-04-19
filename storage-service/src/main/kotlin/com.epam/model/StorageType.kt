package com.epam.model

import com.epam.exception.CustomException

enum class StorageType(
    private val type: String,
) {
    STAGING("STAGING"),
    PERMANENT("PERMANENT");

    companion object {
        fun of(string: String): StorageType {
            return when(string) {
                STAGING.type -> STAGING
                PERMANENT.type -> PERMANENT
                else -> throw CustomException("No storage type $string", 500)
            }
        }
    }

    fun value(): String = this.type
}