package com.example.janagroandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    val status: String? = null,
    val message: String? = null,
    val data: OrderListData? = null
)

data class OrderListData(
    val orders: List<OrderDto> = emptyList()
)

data class SingleOrderResponse(
    val status: String? = null,
    val message: String? = null,
    val data: SingleOrderData? = null
)

data class SingleOrderData(
    val order: OrderDto? = null
)

data class OrderDto(
    val id: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("merchant_id")
    val merchantId: Long,
    @SerializedName("voucher_id")
    val voucherId: Long? = null,
    @SerializedName("total_price")
    val totalPrice: String,
    @SerializedName("shipping_cost")
    val shippingCost: String? = null,
    @SerializedName("voucher_discount")
    val voucherDiscount: String? = null,
    val courier: String? = null,
    val status: String,
    @SerializedName("payment_token")
    val paymentToken: String? = null,
    @SerializedName("snap_url")
    val snapUrl: String? = null,
    @SerializedName("shipping_address")
    val shippingAddress: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("is_reviewed")
    val isReviewed: Boolean = false,
    val items: List<OrderItemDto>? = null,
    val merchant: MerchantDto? = null
) {
    val totalPriceDouble: Double get() = totalPrice.toDoubleOrNull() ?: 0.0
}

data class OrderItemDto(
    val id: Long,
    @SerializedName("order_id")
    val orderId: Long,
    @SerializedName("product_id")
    val productId: Long,
    val quantity: Int,
    @SerializedName("price_at_purchase")
    val priceAtPurchase: String,
    val product: ProductDto? = null
) {
    val priceAtPurchaseDouble: Double get() = priceAtPurchase.toDoubleOrNull() ?: 0.0
}
