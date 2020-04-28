package com.elevintech.motorbroshop.Customer

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbroshop.Chat.ChatLogActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
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

    }

    private fun updateUI(customer: Customer) {

        userNameToolbar.text = (customer.firstName + " " + customer.lastName)
        userNameFull.text = (customer.firstName + " " + customer.lastName)
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
