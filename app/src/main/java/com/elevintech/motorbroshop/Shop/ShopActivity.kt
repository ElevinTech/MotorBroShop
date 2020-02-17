package com.elevintech.motorbroshop.Shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import kotlinx.android.synthetic.main.activity_shop.*
import java.util.*

class ShopActivity : AppCompatActivity() {

    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        shopId = intent.getStringExtra("shopId")!!

        backButton.setOnClickListener {
            finish()
        }

        editShopButton.setOnClickListener{
            val intent = Intent(this, UpdateShop::class.java)
            intent.putExtra("shopId", shopId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        MotorBroDatabase().getShop(shopId){
            updateUi(it)
        }

    }

    private fun updateUi(shop: Shop) {
        shopName.text = shop.name.capitalize()
        shopAddress.text = shop.address.capitalize()
        dateEstablished.text = Utils().convertMillisecondsToDate(shop.dateEstablished.toLong(),"MMM d yyyy")
        shopDescriptionEditText.setText(shop.description)

        if (shop.imageUrl != ""){ Glide.with(this).load(shop.imageUrl).into(shopImageView) }

    }


}
