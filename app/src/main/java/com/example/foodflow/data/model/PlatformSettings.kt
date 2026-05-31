package com.example.foodflow.data.model

data class PlatformSettings(
    val deliveryFee: Double = 2.00,          // Flat fee charged to customer
    val platformCommissionRate: Double = 0.10, // 10% of subtotal taken by platform
    val driverCommissionRate: Double = 0.70,   // 70% of delivery fee given to driver
    val platformBankAccount: String = "ABA Bank - 013572914 - FoodFlow Inc.", // For manual transfer
    val platformBankAccountUrl: String = "https://link.payway.com.kh/aba?id=DCAA8EB9C3C9&dynamic=true&source_caller=sdk&pid=af_app_invites&link_action=abaqr&shortlink=cgokhc63&amount={amount}&created_from_app=true&acc=013572914&af_siteid=968860649&userid=DCAA8EB9C3C9&code=925945&c=abaqr&af_referrer_uid=1771668317939-0575284 "
)