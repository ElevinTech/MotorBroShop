package com.elevintech.motorbroshop.Model

class BikeParts(
    var id: String = "",
    var userId: String = "",
    var bikeId: String = "",
    var date: String = "",
    var dateLong: Long = 0.toLong(),
    var odometer: Double = 0.0,
    var typeOfParts: String = "",
    var brand: String = "",
    var note: String = "",
    var price: Double = 0.0,
    var shopId: String ="",
    var createdByShop: Boolean = false,
    var imageUrl: String ="")
