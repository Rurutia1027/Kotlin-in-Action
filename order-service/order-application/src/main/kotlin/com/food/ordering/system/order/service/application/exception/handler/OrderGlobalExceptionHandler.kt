package com.food.ordering.system.order.service.application.exception.handler

import com.food.ordering.system.application.handler.ErrorDTO
import com.food.ordering.system.application.handler.GlobalExceptionHandler
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class OrderGlobalExceptionHandler : GlobalExceptionHandler() {

    private val log = LoggerFactory.getLogger(OrderGlobalExceptionHandler::class.java)

    @ResponseBody
    @ExceptionHandler(OrderDomainException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleOrderDomainException(orderDomainException: OrderDomainException): ErrorDTO {
        log.error(orderDomainException.message, orderDomainException)
        return ErrorDTO(
            code = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = orderDomainException.message ?: "Order domain error",
        )
    }

    @ResponseBody
    @ExceptionHandler(OrderNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleOrderNotFoundException(orderNotFoundException: OrderNotFoundException): ErrorDTO {
        log.error(orderNotFoundException.message, orderNotFoundException)
        return ErrorDTO(
            code = HttpStatus.NOT_FOUND.reasonPhrase,
            message = orderNotFoundException.message ?: "Order not found",
        )
    }
}
