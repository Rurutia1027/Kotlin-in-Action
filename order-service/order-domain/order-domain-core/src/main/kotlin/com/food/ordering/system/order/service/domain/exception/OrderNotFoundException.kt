package com.food.ordering.system.order.service.domain.exception

open class OrderNotFoundException : OrderDomainException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
