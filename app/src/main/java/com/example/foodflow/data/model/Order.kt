package com.example.foodflow.data.model

data class Order(
    val id: String = "",
    val customerId: String = "",
    val restaurantId: String = "",
    val driverId: String? = null, // NEW: Null means no driver has claimed it yet
    val itemNames: List<String> = emptyList(),
    val status: OrderStatus = OrderStatus.PLACED,
    val createdAt: Long = System.currentTimeMillis(),

    // V2 Payment & Monetization Fields
    val paymentMethod: PaymentMethod = PaymentMethod.COD,

    // The Split (Calculated at checkout based on PlatformSettings)
    val subtotal: Double = 0.0,              // Total of food items
    val deliveryFee: Double = 0.0,           // Flat delivery fee from settings
    val platformFee: Double = 0.0,           // Flat platform fee
    val totalAmount: Double = 0.0,           // What the Customer pays (Subtotal + DeliveryFee + PlatformFee)
    val restaurantEarnings: Double = 0.0,    // What the Restaurant gets
    val driverEarnings: Double = 0.0,        // What the Driver gets
    val platformEarnings: Double = 0.0,       // What the Platform gets
    val transactionImageUrl: String? = null // Image of payment transaction with bank transfer as paymentmethod
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