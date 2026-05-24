package com.food.ordering.system.application.handler

import jakarta.validation.ConstraintViolationException
import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {
    protected val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ResponseBody
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: java.lang.Exception): ErrorDTO {
        log.error(exception.message, exception)
        return ErrorDTO(
            code = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "Unexpected error!"
        )
    }

    @ResponseBody
    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(validationException: ValidationException): ErrorDTO {
        return if (validationException is ConstraintViolationException) {
            val violations = extractViolationsFromException(validationException)
            log.error(violations, validationException)
            ErrorDTO(
                code = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = violations,
            )
        } else {
            val exceptionMessage = validationException.message ?: "Validation error"
            log.error(exceptionMessage, validationException)
            ErrorDTO(
                code = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = exceptionMessage,
            )
        }
    }

    private fun extractViolationsFromException(validationException: ConstraintViolationException): String =
        validationException.constraintViolations
            .joinToString("--") { it.message }
}