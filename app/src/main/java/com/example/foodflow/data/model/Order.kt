    package com.example.foodflow.data.model

    data class Order(
        val id: String = "",
        val customerId: String = "",
        val restaurantId: String = "",
        val driverId: String? = null,

        val items: List<OrderItem> = emptyList(), // Replaces itemNames
        val status: OrderStatus = OrderStatus.PLACED,
        val createdAt: Long = System.currentTimeMillis(),

        // Denormalized data for Detail Screens
        val customerName: String = "Unknown Customer",
        val restaurantName: String = "Unknown Restaurant",
        val deliveryAddress: String = "",

        // V2 Payment & Monetization Fields
        val paymentMethod: PaymentMethod = PaymentMethod.COD,
        val subtotal: Double = 0.0,
        val deliveryFee: Double = 0.0,
        val platformFee: Double = 0.0,
        val totalAmount: Double = 0.0,
        val restaurantEarnings: Double = 0.0,
        val driverEarnings: Double = 0.0,
        val platformEarnings: Double = 0.0,
        val transactionImageUrl: String? = null
    )

    data class OrderItem(
        val menuItemId: String = "",
        val name: String = "",
        val quantity: Int = 0,
        val price: Double = 0.0,
        val imageUrl: String = "" // NEW: Snapshot of the image at time of purchase
    )

    enum class  PaymentMethod {
        COD,
        BANK_TRANSFER
    }

    enum class OrderStatus {
        PENDING_PAYMENT_VERIFICATION,
        PLACED,
        PREPARING,
        READY,
        ON_THE_WAY,
        DELIVERED,
        REJECTED
    }