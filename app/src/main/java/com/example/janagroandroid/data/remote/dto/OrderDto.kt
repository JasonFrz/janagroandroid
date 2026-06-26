package com.example.janagroandroid.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckoutRequest(
    @Json(name = "shipping_address")
    val shippingAddress: String,
    val courier: String? = null,
    @Json(name = "voucher_code")
    val voucherCode: String? = null,
    @Json(name = "payment_type")
    val paymentType: String? = null,
    @Json(name = "cart_item_ids")
    val cartItemIds: List<Long>? = null
)

@JsonClass(generateAdapter = true)
data class OrderResponse(
    val status: String? = null,
    val message: String? = null,
    val data: OrderListData? = null
)

@JsonClass(generateAdapter = true)
data class OrderListData(
    val orders: List<OrderDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SingleOrderResponse(
    val status: String? = null,
    val message: String? = null,
    val data: SingleOrderData? = null
)

@JsonClass(generateAdapter = true)
data class SingleOrderData(
    val order: OrderDto? = null
)

@JsonClass(generateAdapter = true)
data class OrderDto(
    val id: Long,
    @Json(name = "user_id")
    val userId: Long,
    @Json(name = "merchant_id")
    val merchantId: Long,
    @Json(name = "voucher_id")
    val voucherId: Long? = null,
    @Json(name = "total_price")
    val totalPrice: String,
    @Json(name = "shipping_cost")
    val shippingCost: String? = null,
    @Json(name = "voucher_discount")
    val voucherDiscount: String? = null,
    val courier: String? = null,
    val status: String,
    @Json(name = "payment_token")
    val paymentToken: String? = null,
    @Json(name = "payment_method")
    val paymentMethod: String? = null,
    @Json(name = "snap_url")
    val snapUrl: String? = null,
    @Json(name = "qr_string")
    val qrString: String? = null,
    @Json(name = "deeplink_url")
    val deeplinkUrl: String? = null,
    @Json(name = "va_number")
    val vaNumber: String? = null,
    val bank: String? = null,
    @Json(name = "shipping_address")
    val shippingAddress: String? = null,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String? = null,
    @Json(name = "is_reviewed")
    val isReviewed: Boolean = false,
    val items: List<OrderItemDto>? = null,
    val merchant: MerchantDto? = null
) {
    val totalPriceDouble: Double get() = totalPrice.toDoubleOrNull() ?: 0.0
}

@JsonClass(generateAdapter = true)
data class OrderItemDto(
    val id: Long,
    @Json(name = "order_id")
    val orderId: Long,
    @Json(name = "product_id")
    val productId: Long,
    val quantity: Int,
    @Json(name = "price_at_purchase")
    val priceAtPurchase: String,
    val product: ProductDto? = null
) {
    val priceAtPurchaseDouble: Double get() = priceAtPurchase.toDoubleOrNull() ?: 0.0
}
