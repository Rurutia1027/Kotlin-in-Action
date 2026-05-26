package com.food.ordering.system.order.service.domain.valueobject

import java.util.*

class StreetAddress(
    val id: UUID,
    val street: String,
    val postalCode: String,
    val city: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as StreetAddress
        return street == that.street && postalCode == that.postalCode && city == that.city
    }

    override fun hashCode(): Int {
        var result = street.hashCode()
        result = 31 * result + postalCode.hashCode()
        result = 31 * result + city.hashCode()
        return result
    }
}
