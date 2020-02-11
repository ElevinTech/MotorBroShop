package com.elevintech.motorbroshop.Database

import com.elevintech.motorbroshop.DispatchGroup
import com.elevintech.motorbroshop.Model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class MotorBroDatabase {


    fun getShops(callback: (MutableList<Shop>) -> Unit) {

        var shopList = mutableListOf<Shop>()

        FirebaseFirestore.getInstance().collection("shops").get()
            .addOnSuccessListener {

                for (shopDocument in it){
                    val shop = shopDocument.toObject(Shop::class.java)
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

    fun getOwner(callback: (ShopOwner) -> Unit){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("owners").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = documentSnapshot.toObject(ShopOwner::class.java)!!
            callback( user )
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

    // used for gettings details of the employee after logging in
    fun getEmployee(callback: (Employee) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("employees")
                        .whereEqualTo("uid", uid)

        docRef.get().addOnSuccessListener {

            for (documentSnapshot in it){
                val employee = documentSnapshot.toObject(Employee::class.java)!!
                callback( employee )
            }

        }

    }

    // used for gettings details of the employee before creating login account
    fun getEmployee(employeeId: String, callback: (Employee?) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("employees").document(employeeId)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            if (documentSnapshot != null && documentSnapshot.exists()) {

                val employee = documentSnapshot.toObject(Employee::class.java)!!
                callback( employee )

            } else {

                callback( null )

            }

        }

    }

    fun getConsumer(uid: String, callback: (Consumer?) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user: Consumer? = null

            if (documentSnapshot != null && documentSnapshot.exists()) {
                user = documentSnapshot.toObject(Consumer::class.java)!!
                user.uid = documentSnapshot.id

            }

            callback( user )
        }
    }

    fun isConsumerExist(uid: String, callback: (Boolean) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var isExist: Boolean = false

            if (documentSnapshot != null && documentSnapshot.exists()) {

                isExist = true

            }

            callback( isExist )
        }
    }

    fun createShop(shop: Shop, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()

        db.collection("shops").document(shop.shopId)
            .set(shop)
            .addOnSuccessListener {

                updateOwnerShopId(shop.shopId){
                    callback()
                }

            }
            .addOnFailureListener {
                e -> println(e)
                callback()
            }
    }

    fun updateOwnerShopId(shopId: String, callback: () -> Unit){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!

        db.collection("owners").document(uid)
            .update(mapOf("shopId" to shopId))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { callback() }
    }

    fun getShopEmployees(owner: ShopOwner, callback: (MutableList<Employee>) -> Unit) {

        var employeeList = mutableListOf<Employee>()

        FirebaseFirestore.getInstance().collection("employees")
            .whereEqualTo("shopId", owner.shopId)
            .get()
            .addOnSuccessListener {

                for (employeeDocument in it){
                    val employee = employeeDocument.toObject(Employee::class.java)
                    employeeList.add(employee)
                }

                callback(employeeList)

            }

    }

    fun generateEmployeeId(callback: (employeeId: String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("shop-users").document()
        val employeeId = ref.id

        callback(employeeId)
    }

    fun getShopId(callback: (shopId: String) -> Unit){
        getUser {

            callback(it.shopId)

        }
    }

    fun addShopCustomer(consumer: Consumer, callback: () -> Unit){
        getShopId{

            val shopId = it

            val db = FirebaseFirestore.getInstance()
            db.collection("shops").document(shopId).collection("customers").document(consumer.uid)
                .set(consumer)
                .addOnSuccessListener { callback()}
                .addOnFailureListener {
                    e -> println(e)
                    callback()
                }


        }
    }

    fun getCustomers(callback: (MutableList<Consumer>) -> Unit) {

        var customersList = mutableListOf<Consumer>()

        getShopId{shopId ->

            val db = FirebaseFirestore.getInstance()
            db.collection("shops").document(shopId).collection("customers").get()
                .addOnSuccessListener {querySnapshot ->

                    for(consumer in querySnapshot.documents){

                        val customer = consumer.toObject(Consumer::class.java)
                        customersList.add(customer!!)

                    }
                    callback(customersList)

                }
                .addOnFailureListener {
                    e -> println(e)
                    callback(customersList)
                }
        }


    }

    fun createEmployee(employee: Employee, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        db.collection("employees").document(employee.employeeId)
            .set(employee)
            .addOnSuccessListener {
                callback()
            }
    }

    fun createShopOwner(owner: ShopOwner, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        db.collection("owners").document(owner.uid)
            .set(owner)
            .addOnSuccessListener {

                var user = User( owner.uid, User.UserType.OWNER )

                createNewUser(owner.uid, user){
                    callback()
                }
            }
    }

    fun createNewUser(id: String, user: User, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(id)
            .set(user)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                e -> println(e)
                callback()
            }
    }

    fun getUserType(callback: (String) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = documentSnapshot.toObject(User::class.java)!!
            callback( user.userType )
        }

    }

    fun updateEmployeeFields(employeeId: String, email: String, uid: String, callback: () -> Unit){
        val db = FirebaseFirestore.getInstance()

        db.collection("employees").document(employeeId)
            .update(mapOf("hasSetupLogin" to true,
                            "email" to email,
                            "uid"   to uid))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { callback() }
    }


}