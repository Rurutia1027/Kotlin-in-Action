package com.food.ordering.system.customer.service.domain.create

import org.jetbrains.annotations.NotNull
import java.util.*

data class CreateCustomerCommand(
    @field: NotNull
    val customerId: UUID,
    @field:NotNull
    val username: String,
    @field:NotNull
    val firstName: String,
    @field:NotNull
    val lastName: String,
)