package com.food.ordering.system.customer.service.application.handler

import com.food.ordering.system.application.handler.ErrorDTO
import com.food.ordering.system.application.handler.GlobalExceptionHandler
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class CustomerGlobalExceptionHandler : GlobalExceptionHandler() {

    private val log = LoggerFactory.getLogger(CustomerGlobalExceptionHandler::class.java)

    @ResponseBody
    @ExceptionHandler(CustomerDomainException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleException(exception: CustomerDomainException): ErrorDTO {
        log.error(exception.message, exception)
        return ErrorDTO(
            code = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = exception.message ?: "Customer domain error",
        )
    }
}
