package com.food.ordering.system.restaurant.service.messaging.mapper

import com.food.ordering.system.domain.valueobject.ProductId
import com.food.ordering.system.domain.valueobject.RestaurantOrderStatus
import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.food.ordering.system.restaurant.service.domain.entity.Product
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantMessagingDataMapper {
    fun restaurantApprovalRequestAvroModelToRestaurantApproval(
        restaurantApprovalRequestAvroModel: RestaurantApprovalRequestAvroModel,
    ): RestaurantApprovalRequest =
        RestaurantApprovalRequest.builder()
            .id(restaurantApprovalRequestAvroModel.id.toString())
            .sagaId(restaurantApprovalRequestAvroModel.sagaId.toString())
            .restaurantId(restaurantApprovalRequestAvroModel.restaurantId.toString())
            .orderId(restaurantApprovalRequestAvroModel.orderId.toString())
            .restaurantOrderStatus(
                RestaurantOrderStatus.valueOf(
                    restaurantApprovalRequestAvroModel.restaurantOrderStatus.name
                ),
            )
            .products(
                restaurantApprovalRequestAvroModel.products.map { avroProduct ->
                    Product.builder()
                        .productId(ProductId(UUID.fromString(avroProduct.id.toString())))
                        .quantity(avroProduct.quantity)
                        .build()
                },
            )
            .price(restaurantApprovalRequestAvroModel.price)
            .createdAt(restaurantApprovalRequestAvroModel.createdAt)
            .build()

    fun orderEventPayloadToRestaurantApprovalResponseAvroModel(
        sagaId: String,
        orderEventPayload: OrderEventPayload,
    ): RestaurantApprovalResponseAvroModel =
        RestaurantApprovalResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID())
            .setSagaId(UUID.fromString(sagaId))
            .setOrderId(UUID.fromString(orderEventPayload.orderId))
            .setRestaurantId(UUID.fromString(orderEventPayload.restaurantId))
            .setCreatedAt(orderEventPayload.createdAt.toInstant())
            .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderEventPayload.orderApprovalStatus))
            .setFailureMessages(orderEventPayload.failureMessages)
            .build()
}