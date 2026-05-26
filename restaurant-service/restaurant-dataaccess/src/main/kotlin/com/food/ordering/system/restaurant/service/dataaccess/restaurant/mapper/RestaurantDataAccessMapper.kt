package com.food.ordering.system.restaurant.service.dataaccess.restaurant.mapper

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.ProductId
import com.food.ordering.system.domain.valueobject.RestaurantId
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity
import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail
import com.food.ordering.system.restaurant.service.domain.entity.Product
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantDataAccessMapper {

    fun restaurantToRestaurantProducts(restaurant: Restaurant): List<UUID> =
        restaurant.orderDetail.products.map { product -> product.id!!.value }

    fun restaurantEntityToRestaurant(restaurantEntities: List<RestaurantEntity>): Restaurant {
        val restaurantEntity = restaurantEntities.firstOrNull()
            ?: throw RestaurantDataAccessException("No restaurants found!")

        val restaurantProducts = restaurantEntities.map { entity ->
            Product.builder()
                .productId(ProductId(entity.productId!!))
                .name(entity.productName!!)
                .price(Money(entity.productPrice!!))
                .available(entity.productAvailable!!)
                .build()
        }

        return Restaurant.builder()
            .restaurantId(RestaurantId(restaurantEntity.restaurantId!!))
            .orderDetail(
                OrderDetail.builder()
                    .products(restaurantProducts)
                    .build(),
            )
            .active(restaurantEntity.restaurantActive!!)
            .build()
    }

    fun orderApprovalToOrderApprovalEntity(orderApproval: OrderApproval): OrderApprovalEntity =
        OrderApprovalEntity(
            id = orderApproval.id!!.value,
            restaurantId = orderApproval.restaurantId.value,
            orderId = orderApproval.orderId.value,
            status = orderApproval.approvalStatus,
        )

    fun orderApprovalEntityToOrderApproval(orderApprovalEntity: OrderApprovalEntity): OrderApproval =
        OrderApproval.builder()
            .orderApprovalId(OrderApprovalId(orderApprovalEntity.id!!))
            .restaurantId(RestaurantId(orderApprovalEntity.restaurantId!!))
            .orderId(OrderId(orderApprovalEntity.orderId!!))
            .approvalStatus(orderApprovalEntity.status!!)
            .build()
}
