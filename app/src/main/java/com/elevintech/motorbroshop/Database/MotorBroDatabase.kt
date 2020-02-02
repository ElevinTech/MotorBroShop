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


    fun addToShopUsersCollection(user: ShopUser, callback: () -> Unit) {

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

    fun registerShopUser(user: ShopUser, callback: () -> Unit) {

        val uid = FirebaseAuth.getInstance().uid!!

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Add a new document with a generated ID
        db.collection("shop-users").document(uid)
            .set(user)
            .addOnSuccessListener {

                addToShopUsersCollection(user){
                    callback()
                }

            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }

    }



    fun getUser(callback: (ShopUser) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("shop-users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = ShopUser()

            if (documentSnapshot != null && documentSnapshot.exists()) {
                user = documentSnapshot.toObject(ShopUser::class.java)!!

            }

            callback( user )
        }
    }


}