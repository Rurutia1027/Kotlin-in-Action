package com.food.ordering.system.order.service.domain.entity

import com.food.ordering.system.domain.entity.AggregateRoot
import com.food.ordering.system.domain.valueobject.CustomerId

class Customer : AggregateRoot<CustomerId> {
    var username: String? = null
        private set
    var firstName: String? = null
        private set
    var lastName: String? = null
        private set

    constructor(customerId: CustomerId, username: String, firstName: String, lastName: String) {
        id = customerId
        this.username = username
        this.firstName = firstName
        this.lastName = lastName
    }

    constructor(customerId: CustomerId) {
        id = customerId
    }
}
