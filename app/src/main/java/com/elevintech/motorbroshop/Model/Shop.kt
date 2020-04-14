package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Shop (

    var shopId: String = "",
    var ownerId: String = "",

    var name: String = "",
    var logoUrl: String = "",

    var dateEstablished: String = "",

    var description: String = "",
    var address: String = "", // will be replace by fullAddress
    var fullAddress: Address = Address(),

    var imageUrl: String = "",

    var searchTags: List<String> = listOf(), // contains an array of words (such as the shop's name, city, province, and street) used for searching a shop in the consumer app ( via firestore's "whereArrayContainsAny" query )

    var deviceTokens: Map<String, String> = mapOf() // FCM device tokens of all the users working for the shop (e.g. the shop owner and employees)

) : Serializable