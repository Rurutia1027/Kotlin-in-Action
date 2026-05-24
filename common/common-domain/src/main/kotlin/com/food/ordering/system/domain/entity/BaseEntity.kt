package com.food.ordering.system.domain.entity

abstract class BaseEntity<ID> {
    var id: ID? = null
        protected set

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as BaseEntity<*>
        return id == that.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}