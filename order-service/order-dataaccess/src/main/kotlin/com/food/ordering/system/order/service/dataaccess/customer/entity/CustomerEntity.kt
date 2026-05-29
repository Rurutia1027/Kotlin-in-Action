package com.food.ordering.system.order.service.dataaccess.customer.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "customers")
class CustomerEntity(
    @Id
    var id: UUID? = null,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
)
