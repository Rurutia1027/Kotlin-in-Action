package com.food.ordering.system.customer.service.domain.create

import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateCustomerResponse(
    @field:NotNull val customerId: UUID,
    @field:NotNull val message: String,
)
