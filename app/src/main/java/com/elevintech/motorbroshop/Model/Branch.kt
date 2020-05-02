package com.elevintech.motorbroshop.Model

import java.io.Serializable

class Branch (


    var id: String = "",
    var address: String = "",

    var name: String = "",
    var contactNumber: String = "",
    var email: String = "",

    var fullAddress: Address = Address(),
    var searchTags: List<String> = listOf(), // contains an array of words (such as the shop's name, city, province, and street) used for searching a shop in the consumer app ( via firestore's "whereArrayContainsAny" query )

    var imageUrl: String = "",
    var isMain: Boolean = false

) : Serializable