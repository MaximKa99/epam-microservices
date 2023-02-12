package com.epam.controller

import com.epam.exception.CustomException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AdviceController {

    @ExceptionHandler(CustomException::class)
    fun handleException(ex: CustomException): ResponseEntity<String> {
        if (ex.code == 400) {
            return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
        }

        if (ex.code == 404) {
            return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
        }

        return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}