package com.food.ordering.system.payment.service.domain.exception

import com.food.ordering.system.domain.exception.DomainException

class PaymentDomainException : DomainException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
