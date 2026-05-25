package com.example.janagroandroid.data.remote

import com.example.janagroandroid.data.local.entity.ProductEntity
import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("merchant_id")
    val merchantId: Int = 0,

    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,

    @SerializedName("image_url")
    val imageUrl: String="",

    val description: String=""
)

fun ProductDto.toEntity(): ProductEntity{
    return ProductEntity(
        productId=id,
        sellerId=merchantId,
        name=name,
        category=category,
        price=price,
        stock=stock,
        imageUrl=imageUrl,
        description=description
    )
}