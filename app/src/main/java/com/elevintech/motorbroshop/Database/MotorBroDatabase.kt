package com.elevintech.motorbroshop.Database

import com.elevintech.motorbroshop.Model.Consumer
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

                callback()
//
//                addToShopUsersCollection(user){
//                    callback()
//                }

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

    fun getConsumer(uid: String, callback: (Consumer?) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user: Consumer? = null

            if (documentSnapshot != null && documentSnapshot.exists()) {
                user = documentSnapshot.toObject(Consumer::class.java)!!

            }

            callback( user )
        }
    }

    fun saveShop(shop: Shop, callback: () -> Unit) {

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Add a new document with a generated ID
        db.collection("shops").document(shop.shopId)
            .set(shop)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                e -> println(e)
                callback()
            }
    }

    fun updateOwnerShopId(shopId: String, callback: () -> Unit){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!

        db.collection("shop-user").document(uid)
            .update(mapOf("shopId" to shopId))
            .addOnSuccessListener {}
            .addOnFailureListener {e -> println(e)}
    }

    fun getShopEmployees(callback: (MutableList<ShopUser>) -> Unit) {

        getUser {

            var employeeList = mutableListOf<ShopUser>()

            FirebaseFirestore.getInstance().collection("shop-users")
                .whereEqualTo("shopId","${it.shopId}")
                .whereEqualTo("shopOwner",false)
                .get()
                .addOnSuccessListener {

                    for (shop in it){
                        val shop = shop.toObject(ShopUser::class.java)
                        employeeList.add(shop)
                    }

                    callback(employeeList)

                }

        }

    }

    fun generateEmployeeId(callback: (employeeId: String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("shop-users").document()
        val employeeId = ref.id

        callback(employeeId)
    }

    fun createEmployee(employee: ShopUser, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()

        db.collection("shop-users").document(employee.uid)
            .set(employee)
            .addOnSuccessListener { callback()}
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }


}