package com.epam.exception

data class CustomException(
    override val message: String,
    val code: Int
): RuntimeException(message)
