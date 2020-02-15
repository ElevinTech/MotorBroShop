package com.elevintech.motorbroshop.Database

import android.net.Uri
import com.elevintech.motorbroshop.DispatchGroup
import com.elevintech.motorbroshop.Model.*
import com.elevintech.motorbroshop.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import java.util.*

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

//    fun getUser(callback: () -> Unit){
//
//        val db = FirebaseFirestore.getInstance()
//        val uid = FirebaseAuth.getInstance().uid!!
//        val docRef = db.collection("shop-users").document(uid)
//
//        docRef.get().addOnSuccessListener { documentSnapshot ->
//
//            var user = ShopUser()
//
//            if (documentSnapshot != null && documentSnapshot.exists()) {
//                user = documentSnapshot.toObject(ShopUser::class.java)!!
//
//            }
//
//            callback( user )
//        }
//    }

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

    fun getCustomer(customerId: String, callback: (Customer?) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("customers").document(customerId)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var customer: Customer? = null

            if (documentSnapshot != null && documentSnapshot.exists()) {
                customer = documentSnapshot.toObject(Customer::class.java)!!
            }

            callback( customer )
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

    fun getShopEmployees(shopId: String, callback: (MutableList<Employee>) -> Unit) {

        var employeeList = mutableListOf<Employee>()

        FirebaseFirestore.getInstance().collection("employees")
            .whereEqualTo("shopId", shopId)
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

//    fun getShopId(callback: (shopId: String) -> Unit){
//        getUser {
//
//            callback(it.shopId)
//
//        }
//    }

    fun getShop(shopId: String, callback: (shop: Shop) -> Unit){

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId)
            .get()
            .addOnSuccessListener {

                var shop = Shop()

                if (it != null && it.exists()) {
                    shop = it.toObject(Shop::class.java)!!

                }

                callback(shop)
            }
    }

    fun addShopCustomer(shopId: String, customer: CustomerShopData, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId).collection("customers").document(customer.customerId)
            .set(customer)
            .addOnSuccessListener { callback()}
            .addOnFailureListener {
                e -> println(e)
                callback()
            }

    }

    fun getShopCustomers(shopId: String, callback: (MutableList<Customer>) -> Unit) {

        var customersList = mutableListOf<Customer>()

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId).collection("customers").get()
            .addOnSuccessListener {querySnapshot ->

                for(customerSnapshot in querySnapshot.documents){

                    val customer = customerSnapshot.toObject(Customer::class.java)
                    customersList.add(customer!!)

                }
                callback(customersList)

            }
            .addOnFailureListener {
                e -> println(e)
                callback(customersList)
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

                var user = UserType( owner.uid, UserType.Type.OWNER )

                createNewUser(owner.uid, user){
                    callback()
                }
            }
    }

    fun createNewUser(id: String, userType: UserType, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(id)
            .set(userType)
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

    fun getDocument(documentName: String, shopId: String, callback: (Document?) -> Unit) {

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("shops").document(shopId).collection("documents").document(documentName)

        docRef.get()
            .addOnSuccessListener {

                if (it != null && it.exists()) {
                    val document = it.toObject(Document::class.java)!!
                    callback(document)
                } else {
                    callback(null)

                }


            }

    }

    fun uploadDocumentsToFirebaseStorage(imageUri: Uri, callback: (url: String) -> Unit) {

        var filename = UUID.randomUUID().toString()
        var storageRef = FirebaseStorage.getInstance().getReference("/documents/$filename.jpg")

        // UPLOAD TO FIREBASE
        storageRef.putFile(imageUri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {
                    var url = it.toString()

                    callback(url)
                }

            }
            .addOnFailureListener{
                println( it.toString())
            }
    }

    fun saveShopDocument(shopId: String, documentType: String, imageUri: Uri, callback: () -> Unit) {

        uploadDocumentsToFirebaseStorage(imageUri){ imageUrl ->
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("shops").document(shopId).collection("documents").document("$documentType")

            val document = Document(documentType, imageUrl, Utils().getCurrentTimestamp())

            docRef
                .set(document)
                .addOnSuccessListener { callback() }
                .addOnFailureListener { e -> callback() }
        }

    }


}