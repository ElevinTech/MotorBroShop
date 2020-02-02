package com.elevintech.motorbroshop.Database

import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.Model.ShopUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MotorBroDatabase {


    fun getShops(callback: (MutableList<Shop>) -> Unit) {

        var shopList = mutableListOf<Shop>()

        FirebaseFirestore.getInstance().collection("shops").get()
            .addOnSuccessListener {

                for (shop in it){
                    val shop = shop.toObject(Shop::class.java)
                    shopList.add(shop)
                }

                callback(shopList)

            }

    }

    fun registerUser(user: ShopUser, callback: () -> Unit) {

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()


        // Add a new document with a generated ID
        db.collection("shops").document(user.shopId).collection("users").document(FirebaseAuth.getInstance().uid!!)
            .set(user)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }

    }



}