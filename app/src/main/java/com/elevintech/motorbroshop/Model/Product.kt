package com.elevintech.motorbroshop.Model

class Product (

    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: String = "",
    var imageUrl: String = "",
    var type: String = "", // either a part or service
    var brand: String = "",
    var shopId: String = "",
    var dateCreated: String ="",
    var odometer: Double = 0.0,
    var isShopProduct: Boolean = true,
    var customerId: String = ""


)