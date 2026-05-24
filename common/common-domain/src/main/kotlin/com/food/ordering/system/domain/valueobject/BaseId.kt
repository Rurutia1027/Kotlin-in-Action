package com.food.ordering.system.domain.valueobject

abstract class BaseId<T>(val value: T) {
    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false
        val baseId = other as BaseId<*>
        return value == baseId.value
    }

    override fun hashCode(): Int = value.hashCode()
}