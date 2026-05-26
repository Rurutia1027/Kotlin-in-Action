package com.food.ordering.system.restaurant.service.domain.exception

import com.food.ordering.system.domain.exception.DomainException

class RestaurantDomainException : DomainException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}
