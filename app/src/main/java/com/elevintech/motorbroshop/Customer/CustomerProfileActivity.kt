package com.elevintech.motorbroshop.Customer

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.BikeView.BikeViewActivity
import com.elevintech.motorbroshop.Chat.ChatLogActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.BikeInfo
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import kotlinx.android.synthetic.main.activity_customer_profile.*

class CustomerProfileActivity : AppCompatActivity() {

    lateinit var customer: Customer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        customer = intent.getSerializableExtra("customer") as Customer

        updateUI(customer)

        btnBack.setOnClickListener {
            finish()
        }

        chatUserButton.setOnClickListener {
            chatWithUser()
        }

        MotorBroDatabase().getUserBikes(customer.uid) {

            if (it.count() == 0) {
                noBikes.visibility = View.VISIBLE
                return@getUserBikes
            }

            val bikes = it
            for ((index, bike) in bikes.withIndex()) {
                setBikeValues(bike, index)
            }
        }



    }

    private fun updateUI(customer: Customer) {

        userNameToolbar.text = (customer.firstName + " " + customer.lastName)
        userNameFull.text = (customer.firstName + " " + customer.lastName)
        emailEditText.text = "Email: " + customer.email

        if (customer.profilePictureUrl != "") {
            Glide.with(this).load(customer.profilePictureUrl).into(userProfileImage)
        } else {
            // Put an empty image here
//            Glide.with(this).load(R.drawable.new_empty_data_icon).into(userProfileImage)
        }
    }

    private fun setBikeValues(bike: BikeInfo, index: Int) {

        userBike1.visibility = View.VISIBLE
        var bikeNameView = bikeName
        var bikeImage = bikeImageView

        if (index == 2) {
            userBike2.visibility = View.VISIBLE
            bikeNameView = bikeName2
            bikeImage = bikeImageView2

            userBike2.setOnClickListener {
                val intent = Intent(this, BikeViewActivity::class.java)
                intent.putExtra("bike", bike)
                startActivity(intent)
            }

        } else if (index == 3) {
            userBike3.visibility = View.VISIBLE
            bikeNameView = bikeName3
            bikeImage = bikeImageView3

            userBike3.setOnClickListener {
                val intent = Intent(this, BikeViewActivity::class.java)
                intent.putExtra("bike", bike)
                startActivity(intent)
            }

        } else if (index == 1) {
            userBike1.setOnClickListener {
                val intent = Intent(this, BikeViewActivity::class.java)
                intent.putExtra("bike", bike)
                startActivity(intent)
            }
        }

        bikeNameView.text = bike.getUserBikeName()
//        v.plateNumberText.setText("#" + bike.plateNumber.toUpperCase())
//        if (bike.lastOdometerUpdate != "") {
//            val lastOdometerUpdate =
//                Utils().convertMillisecondsToDate(bike.lastOdometerUpdate.toLong(), "MMM d, yyyy")
//            v.odometerStatementText.text =
//                "Odometer : ${bike.odometer} km updated as of ${lastOdometerUpdate}"
//        }

        if (bike.imageUrl != "") {
            Glide.with(this).load(bike.imageUrl).into(bikeImage)
        } else {
            // Put an empty image here
            Glide.with(this).load(R.drawable.new_empty_data_icon).into(bikeImage)
        }

//        motorcycleLayout.setOnClickListener {
//
//            val intent = Intent(activity, ViewBikeActivity::class.java)
//            intent.putExtra("bike", bike)
//            startActivity(intent)
//
//        }
    }


    fun chatWithUser(){

        val progressDialog = Utils().easyProgressDialog(this, "Please wait...")
        progressDialog.show()

        MotorBroDatabase().getUserCommonData {
            val shopId = it.shopId

            MotorBroDatabase().getChatRoomByParticipants( customer.uid, shopId ){ chatRoomId ->

                if (chatRoomId == ""){
                    // create new chat room
                    val participants = mapOf("user" to customer.uid, "shop" to shopId)
                    MotorBroDatabase().createNewChatRoom ( participants ){ newChatRoomId ->

                        val intent = Intent(this, ChatLogActivity::class.java)
                        intent.putExtra("customerId", customer.uid)
                        intent.putExtra("shopId", shopId)
                        intent.putExtra("chatRoomId", newChatRoomId)
                        startActivity(intent)

                        progressDialog.dismiss()
                    }
                }

                else {
                    val intent = Intent(this, ChatLogActivity::class.java)
                    intent.putExtra("customerId", customer.uid)
                    intent.putExtra("shopId", shopId)
                    intent.putExtra("chatRoomId", chatRoomId)
                    startActivity(intent)

                    progressDialog.dismiss()
                }





            }
        }



    }
}
