package com.elevintech.motorbroshop.Model

import java.io.Serializable

open class Shop (
    var shopId: String = "",
    var name: String = "",
    var spotlight: Boolean = false,
    var description: String = "",
    var imageUrl: String = "",
    var dateEstablished: String = "",
    var address: String = "",
    var fullAddress: Address = Address(),
    var searchTags: List<String> = listOf(), // contains an array of words (such as the shop's name, city, province, and street) used for searching a shop in the consumer app ( via firestore's "whereArrayContainsAny" query )
    var deviceTokens: Map<String, String> = mapOf(), // FCM device tokens of all the users working for the shop (e.g. the shop owner and employees)
    var contactNumber: String = "",
    var email: String = "",
    var ownerId: String = ""
) : Serializable