package com.elevintech.motorbroshop.Register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.activity_register_shop.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class RegisterShop : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_shop)

        saveShopButton.setOnClickListener {
            if (hasCompletedValues()) {
                saveShop()
            }
        }

    }

    private fun saveShop() {

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("shops").document()
        val shopId = ref.id
        val shopName = shopNameEditText.text.toString()

        var shop = Shop("$shopName", "", "$shopId")
        MotorBroDatabase().saveShop(shop){
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun hasCompletedValues(): Boolean {

        if (shopNameEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the shop name field", Toast.LENGTH_LONG).show()
            return false
        }

        if (shopAddressEditText.text.isEmpty()) {
            Toast.makeText(this, "Please fill up the shop address field", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}
