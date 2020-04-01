package com.elevintech.motorbroshop.Database

import android.net.Uri
import com.elevintech.motorbroshop.DispatchGroup
import com.elevintech.motorbroshop.Model.*
import com.elevintech.motorbroshop.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
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

    fun saveBikeParts(bikeParts: BikeParts, customerId: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("customers").document(customerId).collection("bike-parts")
            .add(bikeParts)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
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

    fun getShopCustomerData(shopId: String, callback: (MutableList<CustomerShopData>) -> Unit) {

        var customersList = mutableListOf<CustomerShopData>()

        val db = FirebaseFirestore.getInstance()
        db.collection("shops")
            .document(shopId)
            .collection("customers")
            .orderBy("dateScanned", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {querySnapshot ->

                for(customerSnapshot in querySnapshot){

                    val customer = customerSnapshot.toObject(CustomerShopData::class.java)
                    customersList.add(customer!!)

                }
                callback(customersList)

            }
            .addOnFailureListener {
                    e -> println(e)
                callback(customersList)
            }

    }

    fun getCustomerByUid(uid: String, callback: (Customer?) -> Unit){
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("customers").document(uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val customer = document!!.toObject(Customer::class.java)
                    callback(customer)

                } else {
                    callback(Customer())
                }
            }
            .addOnFailureListener { exception ->

                println("Error getting User: $exception")
                callback(Customer())

            }
    }

    fun getShopCustomers(shopId: String, callback: (MutableList<Customer>) -> Unit) {

        println("shopId: $shopId")

        getShopCustomerData(shopId){ customerShopDataList ->

                if (customerShopDataList.isEmpty()){
                        callback(mutableListOf())

                } else {

                        val listOfCustomerId = mutableListOf<String>()
                        for (customerShopData in customerShopDataList){
                            listOfCustomerId.add(customerShopData.customerId)
                        }


                        val listOfCustomers = mutableListOf<Customer>()
                        val db = FirebaseFirestore.getInstance()
                        db.collection("customers")
                            .whereIn("uid", listOfCustomerId)
                            .get()
                            .addOnSuccessListener {

                                for(customerSnapshot in it){

                                    val customer = customerSnapshot.toObject(Customer::class.java)
                                    listOfCustomers.add(customer)

                                }
                                callback(listOfCustomers)

                            }
                            .addOnFailureListener {
                                callback(listOfCustomers)
                            }
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

            var user = documentSnapshot.toObject(UserType::class.java)!!
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

    fun getShopBranches(shopId: String, callback: (MutableList<Branch>) -> Unit) {

        var branchList = mutableListOf<Branch>()

        FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("branches")
            .get()
            .addOnSuccessListener {

                for (branchDocument in it){
                    val branch = branchDocument.toObject(Branch::class.java)
                    branchList.add(branch)
                }

                callback(branchList)

            }

    }

    fun saveBranch(shopId: String, branch: Branch, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId).collection("branches").document(branch.id)
            .set(branch)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun getBranch(shopId: String, branchId: String, callback: (Branch) -> Unit) {

        val db = FirebaseFirestore.getInstance()

        db.collection("shops").document(shopId).collection("branches").document(branchId)
            .get()
            .addOnSuccessListener {

                val branch = it.toObject(Branch::class.java)!!
                callback(branch)


            }
    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (url: String) -> Unit) {

        var filename = UUID.randomUUID().toString()
        var storageRef = FirebaseStorage.getInstance().getReference("/user_uploads/$filename.jpg")

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

    fun updateShop(shop: Shop, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()

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

    fun getLastMessages(callback: (chat : MutableList<ChatMessage>)-> Unit){

        val uid = FirebaseAuth.getInstance().uid!!
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("user-chat-last-messages").document(uid).collection("last-message-from").orderBy("createdDate")


        ref
            .addSnapshotListener { querysnapshot, e ->

                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in querysnapshot!!.documents){


                    val message = snapshot.toObject(ChatMessage::class.java)!!
                    chatLogList.add(message)


                }

                callback(chatLogList)

            }
    }

    fun getCustomerById(id: String, callback: (Customer) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("customers").document(id)

        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(Customer::class.java)!!
            callback( user )
        }
    }

    fun getChatLog(fromId: String, toId: String, callback: (chat : MutableList<ChatMessage>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("user-chat-messages")
            .document(fromId)
            .collection(toId)
            .orderBy("createdDate", Query.Direction.DESCENDING)
            .limit(7)


        ref
            .addSnapshotListener { querysnapshot, e ->

                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in querysnapshot!!.documentChanges.reversed()){

                    if (snapshot.type == DocumentChange.Type.ADDED){

                        val message = snapshot.document.toObject(ChatMessage::class.java)!!
                        chatLogList.add(message)
                    }

                }

                callback(chatLogList)

            }

    }

    fun saveChatToSender(chatMessage : ChatMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("user-chat-messages").document(chatMessage.fromId).collection(chatMessage.toId)

        ref
            .add(chatMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun saveChatToReceiver(chatMessage : ChatMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("user-chat-messages").document(chatMessage.toId).collection(chatMessage.fromId)

        ref
            .add(chatMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun saveLastMessageToSender(chatMessage : ChatMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("user-chat-last-messages").document(chatMessage.fromId).collection("last-message-from").document(chatMessage.toId)

        ref
            .set(chatMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun saveLastMessageToReceiver(chatMessage : ChatMessage, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("user-chat-last-messages").document(chatMessage.toId).collection("last-message-from").document(chatMessage.fromId)

        ref
            .set(chatMessage)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }


    }

    fun getPreviousChatLog(fromId: String, toId: String, previousCreatedDate: Int, callback: (chat : MutableList<ChatMessage>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("user-chat-messages")
            .document(fromId).collection(toId)
            .orderBy("createdDate", Query.Direction.DESCENDING)
            .startAfter(previousCreatedDate)
            .limit(7)

        docRef.get()
            .addOnSuccessListener {

                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in it){
                    val message = snapshot.toObject(ChatMessage::class.java)!!
                    chatLogList.add(message)
                }

                callback(chatLogList)

            }

    }

    fun saveProduct(shopId: String, product: Product, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId).collection("products").document(product.id)
            .set(product)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }

    }

    fun getShopProducts(shopId: String, callback: (MutableList<Product>) -> Unit) {

        var list = mutableListOf<Product>()
        val db = FirebaseFirestore.getInstance()
        db.collection("shops")
            .document(shopId)
            .collection("products")
            .get()
            .addOnSuccessListener {

                println()
                for (productDocument in it){
                    val product = productDocument.toObject(Product::class.java)
                    list.add(product)
                    println("product: " + product.name)
                }

                callback(list)

                println("addOnSuccessListener")
            }
            .addOnFailureListener {
                e -> println("FailureListener: $e")
                callback(list)
            }

    }

    fun getChatRoomOfShop(shopId: String, callback: (MutableList<ChatRoom>)-> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms")
            .whereEqualTo("participants.shop", shopId)
            .orderBy("lastMessage.createdDate", Query.Direction.DESCENDING)

        ref
            .addSnapshotListener { querysnapshot, e ->

                val chatRoomList = arrayListOf<ChatRoom>()

                for ( snapshot in querysnapshot!!.documentChanges){
                    if ( snapshot.type == DocumentChange.Type.ADDED || snapshot.type == DocumentChange.Type.MODIFIED ){
                        val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                        chatRoomList.add(chatRoom)
                    }
                }

                callback(chatRoomList)

            }
    }

    fun getChatRoomMessages(chatRoomId: String, callback: (MutableList<ChatMessage>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms")
            .document(chatRoomId)
            .collection("chat-messages")
            .orderBy("createdDate", Query.Direction.DESCENDING)

        ref
            .addSnapshotListener { querysnapshot, e ->
                val chatLogList = arrayListOf<ChatMessage>()

                for (snapshot in querysnapshot!!.documentChanges.reversed()){
                    if (snapshot.type == DocumentChange.Type.ADDED){
                        val message = snapshot.document.toObject(ChatMessage::class.java)!!
                        chatLogList.add(message)
                    }
                }

                callback(chatLogList)

            }
    }

    fun createNewChatRoom(participants: Map<String, String>, callback: (String) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms")

        chatRoomRef
            .add( mapOf("participants" to participants) )
            .addOnSuccessListener {
                println("chat room created!: " + it.id)
                callback( it.id )
            }
            .addOnFailureListener { e ->
                println(e)
                callback( "" )
            }
    }

    fun saveMessageInChatRoom(chatMessage: ChatMessage, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms").document(chatMessage.chatRoomId).collection("chat-messages")

        chatRoomRef
            .add( chatMessage )
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                println(e)
                callback()
            }

    }

    fun updateChatRoomLastMessage(chatRoomId: String, latestMessage: ChatMessage, callback:() -> Unit){

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms").document(chatRoomId)

        chatRoomRef
            .update( mapOf("lastMessage" to latestMessage) )
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                println(e)
                callback()
            }

    }

    fun getCustomerDeviceToken(customerId: String, callback: (String) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(customerId)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = documentSnapshot.toObject(User::class.java)!!
            callback( user.token )
        }

    }

    // Get a "User" class object - which is a data class that both Employee and Owner has a value of
    // (e.g. they both have "firstName", "lastName", "deviceToken", "shopId", etc...
    fun getUserCommonData(callback: (User) -> Unit){
        getUserType { userType ->
            if (userType == UserType.Type.OWNER) {
                getOwner {
                    callback(it)
                }
            } else if (userType == UserType.Type.EMPLOYEE) {
                getEmployee {
                    callback(it)
                }
            }
        }
    }

    fun updateFcmToken(token: String) {

        getUserCommonData{
            updateShopTokens(it.shopId, token)
            updateUserToken(it.uid, token)
        }


    }

    private fun updateUserToken(uid: String, token: String) {

        val db = FirebaseFirestore.getInstance()
        val userBio = db.collection("users").document(uid)

        userBio
            .update("token", token)
            .addOnSuccessListener {}
            .addOnFailureListener { e -> println("error update user's fcm token: $e") }


    }

    private fun updateShopTokens(shopId: String, token: String) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("shops").document(shopId)

        userBio
            .update("deviceTokens.$uid", token)
            .addOnSuccessListener { println("success saving shop token: $shopId")}
            .addOnFailureListener { e -> println("error update user's fcm token: $e") }

    }

}